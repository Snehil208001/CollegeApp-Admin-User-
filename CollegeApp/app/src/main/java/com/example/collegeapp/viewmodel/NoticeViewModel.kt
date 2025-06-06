// File: com/example/collegeapp/viewmodel/NoticeViewModel.kt

package com.example.collegeapp.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.collegeapp.models.NoticeModel
import com.example.collegeapp.utils.Constant.NOTICE
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class NoticeViewModel : ViewModel() {

    private val noticeRef = Firebase.firestore.collection(NOTICE)
    private val storageRef = Firebase.storage.reference

    private val _isPosted = MutableLiveData<Boolean>()
    val isPosted: LiveData<Boolean> = _isPosted

    private val _isDeleted = MutableLiveData<Boolean>()
    val isDeleted: LiveData<Boolean> = _isDeleted

    private val _noticeList = MutableLiveData<List<NoticeModel>>()
    val noticeList: LiveData<List<NoticeModel>> = _noticeList

    // Upload notice image → Firestore
    fun saveNotice(uri: Uri, title: String, link: String) {
        _isPosted.value = false
        val randomUid = UUID.randomUUID().toString()
        val imageRef = storageRef.child("$NOTICE/$randomUid.jpg")

        imageRef.putFile(uri)
            .addOnSuccessListener {
                imageRef.downloadUrl
                    .addOnSuccessListener { downloadUri ->
                        uploadNoticeToFirestore(
                            imageUrl = downloadUri.toString(),
                            docId = randomUid,
                            title = title,
                            link = link
                        )
                    }
                    .addOnFailureListener { e ->
                        Log.e("NoticeVM", "Failed to get download URL", e)
                        _isPosted.value = false
                    }
            }
            .addOnFailureListener { e ->
                Log.e("NoticeVM", "Failed to upload file to Storage", e)
                _isPosted.value = false
            }
    }

    private fun uploadNoticeToFirestore(
        imageUrl: String,
        docId: String,
        title: String,
        link: String
    ) {
        val data = mapOf(
            "imageUrl" to imageUrl,
            "docId" to docId,
            "title" to title,
            "link" to link
        )
        noticeRef.document(docId)
            .set(data)
            .addOnSuccessListener {
                _isPosted.value = true
            }
            .addOnFailureListener { e ->
                Log.e("NoticeVM", "Failed to write Firestore document", e)
                _isPosted.value = false
            }
    }

    // Fetch all notices from Firestore
    fun getNotice() {
        noticeRef.get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents
                    .mapNotNull { it.toObject(NoticeModel::class.java) }
                _noticeList.value = list
            }
            .addOnFailureListener { e ->
                Log.e("NoticeVM", "Error fetching notices", e)
            }
    }

    // Delete a notice: first delete from Storage, then delete the Firestore document
    fun deleteNotice(notice: NoticeModel) {
        _isDeleted.value = false
        val docId = notice.docId
        if (docId.isBlank()) {
            Log.e("NoticeVM", "docId is blank or empty; cannot delete")
            _isDeleted.value = false
            return
        }

        val storagePath = "$NOTICE/$docId.jpg"
        storageRef.child(storagePath)
            .delete()
            .addOnSuccessListener {
                noticeRef.document(docId)
                    .delete()
                    .addOnSuccessListener {
                        _isDeleted.value = true
                    }
                    .addOnFailureListener { e ->
                        Log.e("NoticeVM", "Failed to delete Firestore doc", e)
                        _isDeleted.value = false
                    }
            }
            .addOnFailureListener { e ->
                Log.e("NoticeVM", "Failed to delete Storage file", e)
                _isDeleted.value = false
            }
    }
}
