package com.example.collegeapp.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.collegeapp.models.FacultyModel
import com.example.collegeapp.utils.Constant.FACULTY
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class FacultyViewModel : ViewModel() {

    private val facultyRef = Firebase.firestore.collection(FACULTY)
    private val storageRef = Firebase.storage.reference

    private val _isPosted     = MutableLiveData<Boolean>()
    val isPosted: LiveData<Boolean> = _isPosted

    private val _isDeleted    = MutableLiveData<Boolean>()
    val isDeleted: LiveData<Boolean> = _isDeleted

    private val _facultyList  = MutableLiveData<List<FacultyModel>>()
    val facultyList: LiveData<List<FacultyModel>> = _facultyList

    private val _categoryList = MutableLiveData<List<String>>()
    val categoryList: LiveData<List<String>> = _categoryList

    fun saveFaculty(
        uri: Uri,
        name: String,
        email: String,
        position: String,
        department: String
    ) {
        _isPosted.value = false
        val randomUid = UUID.randomUUID().toString()
        val imageRef = storageRef.child("$FACULTY/$randomUid.jpg")

        imageRef.putFile(uri)
            .addOnSuccessListener {
                imageRef.downloadUrl
                    .addOnSuccessListener { downloadUri ->
                        uploadFacultyToFirestore(
                            imageUrl = downloadUri.toString(),
                            docId   = randomUid,
                            name     = name,
                            email    = email,
                            position = position,
                            department = department
                        )
                    }
                    .addOnFailureListener { e ->
                        Log.e("FacultyVM", "Failed to get download URL", e)
                        _isPosted.value = false
                    }
            }
            .addOnFailureListener { e ->
                Log.e("FacultyVM", "Failed to upload file to Storage", e)
                _isPosted.value = false
            }
    }

    private fun uploadFacultyToFirestore(
        imageUrl: String,
        docId: String,
        name: String,
        email: String,
        position: String,
        department: String
    ) {
        val data = mapOf(
            "imageUrl"   to imageUrl,
            "docId"      to docId,
            "name"       to name,
            "email"      to email,
            "position"   to position,
            "department" to department
        )

        facultyRef.document(docId)
            .set(data)
            .addOnSuccessListener {
                _isPosted.value = true
            }
            .addOnFailureListener { e ->
                Log.e("FacultyVM", "Failed to write Firestore document", e)
                _isPosted.value = false
            }
    }

    fun getFaculty() { // This fetches your FacultyModel (teachers)
        facultyRef.get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents
                    .mapNotNull { it.toObject(FacultyModel::class.java) }
                _facultyList.value = list
            }
            .addOnFailureListener { e ->
                Log.e("FacultyVM", "Error fetching faculty list", e)
            }
    }

    fun getCategory() { // This fetches category strings
        facultyRef.get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents
                    .mapNotNull { it.getString("category") } // Getting "category" field as String
                _categoryList.value = list
            }
            .addOnFailureListener { e ->
                Log.e("FacultyVM", "Error fetching categories", e)
            }
    }

    fun uploadCategoryToFirestore(category: String) {
        val data = mapOf("category" to category)
        facultyRef.document(category) // Doc ID is the category name
            .set(data)
            .addOnSuccessListener {
                _isPosted.value = true
            }
            .addOnFailureListener { e ->
                Log.e("FacultyVM", "Failed to write Firestore document", e)
                _isPosted.value = false
            }
    }

    fun deleteFaculty(facultyModel: FacultyModel) {
        _isDeleted.value = false
        val docId = facultyModel.docId
        if (docId.isBlank()) {
            Log.e("FacultyVM", "docId is blank; cannot delete")
            _isDeleted.value = false
            return
        }

        val storagePath = "$FACULTY/$docId.jpg"
        storageRef.child(storagePath)
            .delete()
            .addOnSuccessListener {
                facultyRef.document(docId)
                    .delete()
                    .addOnSuccessListener {
                        _isDeleted.value = true
                    }
                    .addOnFailureListener { e ->
                        Log.e("FacultyVM", "Failed to delete Firestore doc", e)
                        _isDeleted.value = false
                    }
            }
            .addOnFailureListener { e ->
                Log.e("FacultyVM", "Failed to delete Storage file", e)
                _isDeleted.value = false
            }
    }

    fun deleteCategory(category: String) { // This deletes a category document
        _isDeleted.value = false
        if (category.isBlank()) {
            Log.e("FacultyVM", "category is blank; cannot delete")
            _isDeleted.value = false
            return
        }
        facultyRef.document(category)
            .delete()
            .addOnSuccessListener {
                _isDeleted.value = true
            }
            .addOnFailureListener { e ->
                Log.e("FacultyVM", "Failed to delete Firestore doc", e)
                _isDeleted.value = false
            }
    }
}