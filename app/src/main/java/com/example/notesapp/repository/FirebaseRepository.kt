package com.example.notesapp.repository

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import com.example.notesapp.model.Note
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileInputStream

object FirebaseRepository {

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    private val firestoreReference = FirebaseFirestore.getInstance().collection("users")
    private val photosReference = FirebaseStorage.getInstance().reference
    fun getNotesReference() = firestoreReference.document(currentUserId.toString()).collection("notes")
    fun getNewNoteId() = getNotesReference().document().id


    suspend fun saveNote(note: Note): Boolean {
        val notesReference = getNotesReference()
        val task = notesReference.document(note.id).set(note)
        task.await()
        return task.isSuccessful
    }

    suspend fun saveNote(note: Note,photoUri : Uri):Boolean{
            val photoUrl = savePhoto(photoUri);
            if(photoUrl != null) {
                note.photoUrl = photoUrl.toString()
                return saveNote(note)
            }
        return false
    }

    private suspend fun savePhoto(photoUri: Uri): Uri? {

        val uploadTask = photosReference
            .child(photoUri.lastPathSegment.toString())
            .putFile(photoUri)
        uploadTask.await()
        val urlDownloadTask = photosReference.child(photoUri.lastPathSegment.toString()).downloadUrl
        urlDownloadTask.await()
        return urlDownloadTask.result
    }

    fun deleteNote(noteId:String){
        getNotesReference().document(noteId).delete()
    }
}