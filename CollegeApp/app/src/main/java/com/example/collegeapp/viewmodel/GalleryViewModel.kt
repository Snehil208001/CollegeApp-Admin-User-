package com.example.collegeapp.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // Import for viewModelScope
import com.example.collegeapp.models.GalleryModel
import com.example.collegeapp.utils.Constant
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch // Import for launch
import java.util.UUID

class GalleryViewModel : ViewModel() {

    // Firebase references are best initialized in the ViewModel to avoid re-creation
    private val galleryRef = Firebase.firestore.collection(Constant.GALLERY)
    private val storageRef = Firebase.storage.reference

    private val _isPosted = MutableLiveData<Boolean>()
    val isPosted: LiveData<Boolean> = _isPosted

    private val _isDeleted = MutableLiveData<Boolean>()
    val isDeleted: LiveData<Boolean> = _isDeleted // This can be observed for deletion feedback

    private val _galleryList = MutableLiveData<List<GalleryModel>>()
    val galleryList: LiveData<List<GalleryModel>> = _galleryList

    /** Upload an image to Storage, then add image URL to corresponding Firestore category document */
    fun saveGalleryImage(uri: Uri, category: String) {
        _isPosted.value = false // Reset state before operation
        val randomUid = UUID.randomUUID().toString()
        val imageRef = storageRef.child("${Constant.GALLERY}/$randomUid.jpg")

        imageRef.putFile(uri)
            .addOnSuccessListener {
                imageRef.downloadUrl
                    .addOnSuccessListener { downloadUri ->
                        // Call the helper function to upload to Firestore
                        uploadCategoryToFirestore(downloadUri.toString(), category)
                    }
                    .addOnFailureListener { e ->
                        Log.e("GalleryVM", "Failed to get download URL: ${e.message}", e)
                        _isPosted.value = false
                    }
            }
            .addOnFailureListener { e ->
                Log.e("GalleryVM", "Failed to upload file to Storage: ${e.message}", e)
                _isPosted.value = false
            }
    }

    /** Adds the image URL to a category document. If the document doesn't exist, it creates it. */
    fun uploadCategoryToFirestore(imageUrl: String, category: String) {
        val data = mapOf(
            "category" to category, // Ensures the category field is also stored
            "images" to FieldValue.arrayUnion(imageUrl) // Adds image URL to the 'images' array
        )

        // Using SetOptions.merge() ensures:
        // 1. If document (category) exists, it updates 'images' array.
        // 2. If document (category) does not exist, it creates it.
        galleryRef.document(category)
            .set(data, com.google.firebase.firestore.SetOptions.merge())
            .addOnSuccessListener {
                _isPosted.value = true
                getGallery() // Refresh gallery list after successful upload
            }
            .addOnFailureListener { e ->
                Log.e("GalleryVM", "Failed to write Firestore document: ${e.message}", e)
                _isPosted.value = false
            }
    }

    /** Fetch all gallery categories and their images. */
    fun getGallery() {
        viewModelScope.launch { // Use viewModelScope for coroutine context
            galleryRef.get()
                .addOnSuccessListener { snapshot ->
                    val list = snapshot.documents
                        .mapNotNull { it.toObject(GalleryModel::class.java) }
                    _galleryList.value = list
                }
                .addOnFailureListener { e ->
                    Log.e("GalleryVM", "Error fetching gallery list: ${e.message}", e)
                }
        }
    }

    /**
     * Delete an image file from Storage and remove its URL from the corresponding Firestore document.
     * @param imageUrl The full URL of the image to delete.
     * @param category The category name (which is also the Firestore document ID).
     */
    // FIX: Removed unused 'docId' parameter from signature
    fun deleteGalleryImage(imageUrl: String, category: String) {
        _isDeleted.value = false // Reset state before operation

        // Extract image file name (e.g., "randomUid.jpg") from the full URL.
        val pathSegments = imageUrl.split("?")[0].split("/")
        val fileName = pathSegments.lastOrNull()

        if (fileName.isNullOrBlank()) {
            Log.e("GalleryVM", "Could not extract file name from URL: $imageUrl")
            _isDeleted.value = false
            return
        }

        val storagePath = "${Constant.GALLERY}/$fileName"

        Log.d("DeleteDebug", "Attempting to delete Storage path: $storagePath for URL: $imageUrl")

        storageRef.child(storagePath) // Reference to the file in Firebase Storage
            .delete()
            .addOnSuccessListener {
                Log.d("DeleteDebug", "Image deleted from Storage successfully.")
                // Now remove the image URL from the 'images' array in the Firestore document
                galleryRef.document(category) // Use the category as the document ID
                    .update("images", FieldValue.arrayRemove(imageUrl))
                    .addOnSuccessListener {
                        Log.d("DeleteDebug", "Image URL removed from Firestore successfully.")
                        _isDeleted.value = true
                        getGallery() // Refresh gallery list after successful image deletion
                    }
                    .addOnFailureListener { e ->
                        Log.e("GalleryVM", "Failed to remove image URL from Firestore: ${e.message}", e)
                        _isDeleted.value = false
                    }
            }
            .addOnFailureListener { e ->
                Log.e("GalleryVM", "Failed to delete image from Storage ($storagePath): ${e.message}", e)
                _isDeleted.value = false
            }
    }

    /**
     * Delete an entire category document from Firestore.
     * Note: Images within this category in Storage will NOT be deleted automatically;
     * they will become orphaned unless manually managed.
     * @param category The name of the category (which is also the Firestore document ID).
     */
    fun deleteCategory(category: String) {
        _isDeleted.value = false // Reset state before operation

        if (category.isBlank()) {
            Log.e("GalleryVM", "Category name is blank; cannot delete category document.")
            _isDeleted.value = false
            return
        }

        galleryRef.document(category) // Reference to the category document in Firestore
            .delete()
            .addOnSuccessListener {
                _isDeleted.value = true
                getGallery() // Refresh gallery list after successful category deletion
            }
            .addOnFailureListener { e ->
                Log.e("GalleryVM", "Failed to delete category from Firestore: ${e.message}", e)
                _isDeleted.value = false
            }
    }
}