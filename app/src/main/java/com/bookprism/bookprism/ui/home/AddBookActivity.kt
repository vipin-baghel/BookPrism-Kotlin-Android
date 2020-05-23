package com.bookprism.bookprism.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bookprism.bookprism.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.app_bar_main.*
import java.util.*

class AddBookActivity : AppCompatActivity() {
    var nameET: EditText? = null
    var authorET: EditText? = null
    var ratingET: EditText? = null
    var costET: EditText? = null
    var commentET: EditText? = null
    var uploadBtn: Button? = null
    var progressBar: ProgressBar? = null
    var db: FirebaseFirestore? = null
    lateinit var storageReference: StorageReference
    var imageUrl: String? = null
    private val map: MutableMap<String, Any> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Add Books to Library"

        storageReference = FirebaseStorage.getInstance().getReference("upload")
        db = FirebaseFirestore.getInstance()
        nameET = findViewById(R.id.name_et)
        authorET = findViewById(R.id.author_et)
        ratingET = findViewById(R.id.rate_et)
        costET = findViewById(R.id.cost_et)
        commentET = findViewById(R.id.comment_et)
        uploadBtn = findViewById(R.id.upload_btn)
        progressBar = findViewById(R.id.progress_bar)
        progressBar?.visibility = View.GONE

        uploadBtn?.setOnClickListener {
            when {
                nameET?.text.toString() == "" -> {
                    nameET?.error = "Please Enter Book name"
                    nameET?.requestFocus()
                }
                authorET?.text.toString() == "" -> {
                    authorET?.error = "Please Enter author name"
                    authorET?.requestFocus()
                }
                ratingET?.text.toString() == "" -> {
                    ratingET?.error = "Please give rating"
                    ratingET?.requestFocus()
                }
                ratingET?.text.toString().toFloat() > 10f -> {
                    ratingET?.error = "Please give a rating below 10"
                    ratingET?.requestFocus()
                }
                ratingET?.text.toString().toFloat() == 0f -> {
                    ratingET?.error = "Rating cannot be 0"
                    ratingET?.requestFocus()
                }
                costET?.text.toString() == "" -> {
                    costET?.error = "Please enter cost of the book"
                    costET?.requestFocus()
                }
                commentET?.text.toString() == "" -> {
                    commentET?.error = "Please give a comment"
                    commentET?.requestFocus()
                }
                else -> {
                    uploadBtn?.isEnabled = false
                    progressBar?.visibility = View.VISIBLE

                    val bookName = nameET?.text.toString()
                    val authorName = authorET?.text.toString()
                    val rating = ratingET?.text.toString()
                    val cost = costET?.text.toString()
                    val comment = commentET?.text.toString()

                    map["name"] = bookName
                    map["author"] = authorName
                    map["rating"] = rating
                    map["cost"] = cost
                    map["comment"] = comment

                    val i = Intent()
                    i.type = "image/*"
                    i.action = Intent.ACTION_GET_CONTENT
                    startActivityForResult(Intent.createChooser(i, "Select Picture"), PIC_IMAGE_CODE)

                }
            }
        }
    }

    companion object {
        private const val PIC_IMAGE_CODE = 1000
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PIC_IMAGE_CODE) {
            val uploadTask = storageReference.putFile(data?.data!!)
            val task = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(baseContext,
                            "Failed to get image",
                            Toast.LENGTH_SHORT).show()
                }
                storageReference.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloaduri = task.result
                    imageUrl = downloaduri.toString()
                    map["imgUrl"] = imageUrl!!
                    uploadData(map)
                }
            }
        }
    }

    private fun uploadData(data: Map<String, Any>) {
        db!!.collection("books")
                .add(data)
                .addOnSuccessListener {
                    Toast.makeText(applicationContext, "Book successfully added to library", Toast.LENGTH_SHORT).show()
                    uploadBtn?.isEnabled = true
                    progressBar?.visibility = View.GONE
                    finish()
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "Problem adding book to library", Toast.LENGTH_SHORT).show()
                    uploadBtn?.isEnabled = true
                    progressBar?.visibility = View.GONE
                }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}