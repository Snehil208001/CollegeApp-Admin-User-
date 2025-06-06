package com.example.collegeapp.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.collegeapp.models.CollegeInfoModel
import com.example.collegeapp.utils.Constant.COLLEGE_INFO
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class CollegeInfoViewModel : ViewModel() {

    private val collegeInfoRef = Firebase.firestore.collection(COLLEGE_INFO)
    private val storageRef = Firebase.storage.reference

    private val _isPosted = MutableLiveData<Boolean>()
    val isPosted: LiveData<Boolean> = _isPosted

    private val _collegeInfo = MutableLiveData<CollegeInfoModel?>()
    val collegeInfo: LiveData<CollegeInfoModel?> = _collegeInfo


    fun saveCollegeInfo(
        uri: Uri, // This URI is the source of the image, either new or existing
        name: String,
        address: String,
        phone: String,
        email: String,
        description: String,
        websiteLink: String
    ) {
        _isPosted.value = false // Indicate that posting has started

        // Check if the URI is a local file URI (indicating a new image to upload)
        // or a remote URL (indicating an existing image that doesn't need re-uploading)
        if (uri.scheme == "content" || uri.scheme == "file") {
            // It's a new local image, upload it
            val randomUid = UUID.randomUUID().toString()
            val imageRef = storageRef.child("$COLLEGE_INFO/$randomUid.jpg")

            imageRef.putFile(uri)
                .addOnSuccessListener {
                    imageRef.downloadUrl
                        .addOnSuccessListener { downloadUri ->
                            // Upload to Firestore with the new download URL
                            uploadCollegeInfoToFirestore(
                                downloadUri.toString(), // Use the new download URL
                                name,
                                address,
                                phone,
                                email,
                                description,
                                websiteLink
                            )
                        }
                        .addOnFailureListener { e ->
                            Log.e("CollegeInfoVM", "Failed to get download URL: ${e.message}", e)
                            _isPosted.value = false
                        }
                }
                .addOnFailureListener { e ->
                    Log.e("CollegeInfoVM", "Failed to upload image file: ${e.message}", e)
                    _isPosted.value = false
                }
        } else {
            // It's likely an existing remote URL, no need to re-upload.
            // Just update Firestore with the current imageUrl (which is the passed `uri.toString()`)
            uploadCollegeInfoToFirestore(
                uri.toString(), // Use the existing URL directly
                name,
                address,
                phone,
                email,
                description,
                websiteLink
            )
        }
    }


    private fun uploadCollegeInfoToFirestore(
        imageUrl: String,
        name: String,
        address: String,
        phone: String,
        email: String,
        description: String,
        websiteLink: String
    ) {
        val collegeInfoModel = CollegeInfoModel(
            name = name,
            address = address,
            description = description,
            websiteLink = websiteLink,
            imageUrl = imageUrl, // This will now be the potentially new or existing URL
            phone = phone,
            email = email
        )

        collegeInfoRef.document("CollegeInfo")
            .set(collegeInfoModel)
            .addOnSuccessListener {
                _isPosted.value = true
                Log.d("CollegeInfoVM", "College information successfully uploaded to Firestore.")
            }
            .addOnFailureListener { e ->
                Log.e("CollegeInfoVM", "Failed to write college information to Firestore: ${e.message}", e)
                _isPosted.value = false
            }
    }


    fun getCollegeInfo() {
        collegeInfoRef.document("CollegeInfo").get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val collegeInfo = documentSnapshot.toObject(CollegeInfoModel::class.java)
                    _collegeInfo.postValue(collegeInfo)
                    Log.d("CollegeInfoVM", "College information successfully fetched.")
                } else {
                    Log.d("CollegeInfoVM", "College information document does not exist.")
                    _collegeInfo.postValue(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e("CollegeInfoVM", "Failed to fetch college information: ${e.message}", e)
                _collegeInfo.postValue(null)
            }
    }
}