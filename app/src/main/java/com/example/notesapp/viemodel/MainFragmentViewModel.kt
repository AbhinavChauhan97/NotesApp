package com.example.notesapp.viemodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.notesapp.model.Note
import com.example.notesapp.repository.FirebaseRepository
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class MainFragmentViewModel : ViewModel() {

   var query = FirebaseRepository.getNotesReference().orderBy("id")
    set(value) {
        options = FirestoreRecyclerOptions.Builder<Note>()
            .setQuery(value,Note::class.java)
            .build()
    }
    var options = FirestoreRecyclerOptions.Builder<Note>()
        .setQuery(query,Note::class.java)
        .build()
}