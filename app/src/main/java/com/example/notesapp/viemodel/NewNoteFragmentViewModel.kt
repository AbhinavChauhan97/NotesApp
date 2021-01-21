package com.example.notesapp.viemodel

import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.model.Note
import com.example.notesapp.repository.FirebaseRepository
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import java.io.FileInputStream

class NewNoteFragmentViewModel : ViewModel() {

   lateinit var note: Note
   var imageUri:Uri? = null
   var doOnNoteAdded:(() -> Unit)? = null
   var doOnNoteFailedToAdd:(() -> Unit)? = null
   var doOnStartAddingNote:(() -> Unit)? = null


   fun addNote(){
      setLastModified()
      viewModelScope.launch {
         doOnStartAddingNote?.invoke()
         val deferred = viewModelScope.async(Dispatchers.IO) {
               val uri = imageUri
               if(uri != null) {
                  FirebaseRepository.saveNote(note,uri)
               }else{
                  FirebaseRepository.saveNote(note)
               }
         }
         val noteAdded = deferred.await()
         if (noteAdded) {
            doOnNoteAdded?.invoke()
         } else {
            doOnNoteFailedToAdd?.invoke()
         }
      }
   }

   private fun setLastModified(){
      val currentTime =  LocalDateTime.now()
      note.lmYear = currentTime.year
      note.lmMonth = currentTime.monthValue
      note.lmDay = currentTime.dayOfMonth
      note.lmHour = currentTime.hour
      note.lmMinute = currentTime.minute
   }
   fun doOnNoteAdded(doOnNoteAdded:(() -> Unit)){
      this.doOnNoteAdded = doOnNoteAdded
   }

   fun doOnNoteFailedToAdd(doOnNoteFailedToAdd:(() -> Unit)){
      this.doOnNoteFailedToAdd = doOnNoteFailedToAdd
   }

   fun doWhenNoteAdding(whenNoteAdding:(() -> Unit)){
      doOnStartAddingNote = whenNoteAdding
   }
}