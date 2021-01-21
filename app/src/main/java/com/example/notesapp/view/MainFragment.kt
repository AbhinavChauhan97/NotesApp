package com.example.notesapp.view

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.get

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.example.notesapp.DataBinderMapperImpl
import com.example.notesapp.R
import com.example.notesapp.adapters.NotesRecyclerviewAdapter
import com.example.notesapp.databinding.FragmentMainBinding
import com.example.notesapp.model.Note
import com.example.notesapp.repository.FirebaseRepository
import com.example.notesapp.viemodel.MainFragmentViewModel
import com.google.firebase.firestore.Query
import kotlin.concurrent.fixedRateTimer

class MainFragment : Fragment(R.layout.fragment_main){

    lateinit var binding:FragmentMainBinding
    lateinit var viewModel:MainFragmentViewModel
    lateinit var recyclerviewAdapter: NotesRecyclerviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar!!.show()
        binding = FragmentMainBinding.bind(view)
        binding.fragment = this
        viewModel = ViewModelProviders.of(this).get(MainFragmentViewModel::class.java)
        binding.notesRecyclerview.layoutManager = LinearLayoutManager(requireActivity())
        binding.notesRecyclerview.addItemDecoration(DividerItemDecoration(requireActivity(),LinearLayoutManager.VERTICAL))
        //binding.notesRecyclerview.setHasFixedSize(true)
        recyclerviewAdapter = NotesRecyclerviewAdapter(viewModel.options)
        binding.notesRecyclerview.adapter = recyclerviewAdapter
        //requireActivity().setActionBar(binding.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.searchview_menu,menu)

        val menuItem = menu.findItem(R.id.search).actionView as SearchView
        menuItem.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                val firbaseQuery =
                    FirebaseRepository.getNotesReference().whereEqualTo("title", query)
                return setAdapterWithQuery(firbaseQuery)
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        val sortByTitleItem = menu.findItem(R.id.sort).subMenu[0]
        sortByTitleItem.setOnMenuItemClickListener {
            val query = FirebaseRepository.getNotesReference().orderBy("title")
            setAdapterWithQuery(query)
        }

        val sortByLastModified = menu.findItem(R.id.sort).subMenu[1]
        sortByLastModified.setOnMenuItemClickListener {
            val query = FirebaseRepository.getNotesReference()
                .orderBy("lmYear")
                .orderBy("lmMonth")
                .orderBy("lmDay")
                .orderBy("lmHour")
                .orderBy("lmMinute")

            setAdapterWithQuery(query)
        }
    }

    private fun setAdapterWithQuery(query:Query):Boolean{
        viewModel.query = query
        recyclerviewAdapter = NotesRecyclerviewAdapter(viewModel.options)
        binding.notesRecyclerview.adapter = recyclerviewAdapter
        recyclerviewAdapter.startListening()
        return true
    }

    fun createNote(){
        val action = MainFragmentDirections.actionMainFragmentToNoteFragment(Note(FirebaseRepository.getNewNoteId()))
        findNavController().navigate(action)
    }

    override fun onStart() {
        super.onStart()
        recyclerviewAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        recyclerviewAdapter.stopListening()
    }
}