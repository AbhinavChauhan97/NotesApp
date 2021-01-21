package com.example.notesapp.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.notesapp.R
import com.example.notesapp.databinding.FragmentNewNoteBinding
import com.example.notesapp.model.Note
import com.example.notesapp.util.ImagesUtil
import com.example.notesapp.viemodel.NewNoteFragmentViewModel
import com.example.notesapp.view.dialogfragments.DatePickerDialogFragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import java.io.FileInputStream

class NoteFragment : Fragment(R.layout.fragment_new_note) {

    lateinit var binding: FragmentNewNoteBinding
    lateinit var viewmodel: NewNoteFragmentViewModel


    private val imagePicker = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it  == true) {
            binding.image.load(viewmodel.imageUri)
            lifecycleScope.launch {
                ImagesUtil.with(requireActivity())
                   .compressAndResizeImage("${viewmodel.note.id}.jpg")
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNewNoteBinding.bind(view)
        (requireActivity() as AppCompatActivity).supportActionBar!!.hide()
        viewmodel = ViewModelProviders.of(this).get(NewNoteFragmentViewModel::class.java)
        viewmodel.note =  NoteFragmentArgs.fromBundle(requireArguments()).note
        binding.viewmodel = viewmodel
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.reminder -> {
                    val datePickerDialog = DatePickerDialog(requireActivity())
                    datePickerDialog.setOnDateSetListener { _, year, month, dayOfMonth ->
                        viewmodel.note.reminder = "$year-${month+1}-$dayOfMonth"
                        val currentTime = LocalTime.now()
                        val timeSetListener =
                            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                                viewmodel.note.reminder += " | $hourOfDay:$minute"
                            }
                        val timePickerDialog = TimePickerDialog(requireActivity(),
                            timeSetListener,
                            currentTime.hour,currentTime.minute,false)
                        timePickerDialog.show()
                    }
                    datePickerDialog.show()
                }
                R.id.photo -> {
                    if(viewmodel.imageUri == null){
                        viewmodel.imageUri = ImagesUtil.with(requireActivity())
                            .getUriForImageFile("${viewmodel.note.id}.jpg")
                    }
                    imagePicker.launch(viewmodel.imageUri)
                }
            }
            return@setOnMenuItemClickListener true
        }

        viewmodel.doOnNoteAdded {
            val action = NoteFragmentDirections.actionNoteFragmentToMainFragment()
            findNavController().navigate(action)
        }
        viewmodel.doOnNoteFailedToAdd {
            Toast.makeText(
                requireActivity(),
                "something went wrong, try again",
                Toast.LENGTH_LONG
            ).show()
            binding.noteAddingProgress.hide()
        }
        viewmodel.doWhenNoteAdding {
            binding.noteAddingProgress.show()
        }
    }
}