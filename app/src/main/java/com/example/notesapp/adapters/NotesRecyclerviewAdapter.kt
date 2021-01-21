package com.example.notesapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.databinding.RowNoteBinding
import com.example.notesapp.model.Note
import com.example.notesapp.repository.FirebaseRepository
import com.example.notesapp.view.MainFragmentDirections
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class NotesRecyclerviewAdapter(options: FirestoreRecyclerOptions<Note>) :
    FirestoreRecyclerAdapter<Note, NotesRecyclerviewAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(parent.context);
        val rowBinding = RowNoteBinding.inflate(inflater, parent, false)
        return ViewHolder(rowBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, note: Note) {
        holder.bind(note)
    }

    class ViewHolder(private val noteRowBinding: RowNoteBinding) :
        RecyclerView.ViewHolder(noteRowBinding.root) {

        fun bind(note: Note) {
            Log.d("log",note.toString())
            noteRowBinding.note = note
            noteRowBinding.delete.setOnClickListener {
                FirebaseRepository.deleteNote(note.id)
            }
            noteRowBinding.executePendingBindings()
            val action = MainFragmentDirections.actionMainFragmentToNoteFragment(note)
            val listener = Navigation.createNavigateOnClickListener(action)
            itemView.setOnClickListener(listener)
        }

    }

}


