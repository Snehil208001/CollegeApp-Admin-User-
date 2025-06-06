package com.example.collegeapp.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.collegeapp.models.BannerModel
import com.example.collegeapp.utils.Constant.BANNER
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class BannerViewModel : ViewModel() {

    private val bannerRef = Firebase.firestore.collection(BANNER)
    private val storageRef = Firebase.storage.reference

    private val _isPosted = MutableLiveData<Boolean>()
    val isPosted: LiveData<Boolean> = _isPosted

    private val _isDeleted = MutableLiveData<Boolean>()
    val isDeleted: LiveData<Boolean> = _isDeleted

    private val _bannerList = MutableLiveData<List<BannerModel>>()
    val bannerList: LiveData<List<BannerModel>> = _bannerList

    // Upload banner image
    fun saveImage(uri: Uri) {
        _isPosted.value = false
        val randomUid = UUID.randomUUID().toString()
        val imageRef = storageRef.child("$BANNER/$randomUid.jpg")

        imageRef.putFile(uri)
            .addOnSuccessListener {
                imageRef.downloadUrl
                    .addOnSuccessListener { downloadUri ->
                        uploadImageToFirestore(downloadUri.toString(), randomUid)
                    }
                    .addOnFailureListener { e ->
                        Log.e("BannerVM", "Failed get download URL", e)
                        _isPosted.value = false
                    }
            }
            .addOnFailureListener { e ->
                Log.e("BannerVM", "Failed upload file", e)
                _isPosted.value = false
            }
    }

    private fun uploadImageToFirestore(imageUrl: String, docId: String) {
        val data = mapOf(
            "url" to imageUrl,
            "docId" to docId
        )
        bannerRef.document(docId)
            .set(data)
            .addOnSuccessListener { _isPosted.value = true }
            .addOnFailureListener { e ->
                Log.e("BannerVM", "Failed write Firestore", e)
                _isPosted.value = false
            }
    }

    // Fetch all banners
    fun getBanner() {
        bannerRef.get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents
                    .mapNotNull { it.toObject(BannerModel::class.java) }
                _bannerList.value = list
            }
            .addOnFailureListener { e ->
                Log.e("BannerVM", "Error fetching banners", e)
            }
    }

    // Delete banner image and Firestore document
    fun deleteBanner(bannerModel: BannerModel) {
        _isDeleted.value = false

        storageRef.child("$BANNER/${bannerModel.docId}.jpg")
            .delete()
            .addOnSuccessListener {
                bannerRef.document(bannerModel.docId)
                    .delete()
                    .addOnSuccessListener {
                        _isDeleted.value = true
                    }
                    .addOnFailureListener { e ->
                        Log.e("BannerVM", "Failed delete Firestore doc", e)
                        _isDeleted.value = false
                    }
            }
            .addOnFailureListener { e ->
                Log.e("BannerVM", "Failed delete storage file", e)
                _isDeleted.value = false
            }
    }
}
