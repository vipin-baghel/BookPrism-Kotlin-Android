package com.bookprism.bookprism.ui.profile

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bookprism.bookprism.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_address.*
import java.util.HashMap

class AddressActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        save_btn.setOnClickListener {
            when {
                etHno.text.toString() == "" -> {
                    etHno.error = "Please Enter House No."
                    etHno.requestFocus()

                }
                etLocality.text.toString() == "" -> {
                    etLocality.error = "Please Enter Locality"
                    etLocality.requestFocus()

                }
                etArea.text.toString() == "" -> {
                    etArea.error = "Please Enter Area"
                    etArea.requestFocus()

                }
                etCity.text.toString() == "" -> {
                    etCity.error = "Please Enter City"
                    etCity.requestFocus()

                }
                etPhn.text.toString() == "" -> {
                    etPhn.error = "Please Enter Phone no."
                    etPhn.requestFocus()

                }
                else -> {
                    val address = etHno.text.toString() + " " +
                            etLocality.text.toString() + " " +
                            etArea.text.toString() + " " +
                            etCity.text.toString()
                    val phn = etPhn.text.toString()

                    val sharedPreferences = getSharedPreferences("p", Context.MODE_PRIVATE)!!
                    sharedPreferences.edit().putString("address", address).apply()
                    sharedPreferences.edit().putString("phone", phn).apply()

                    val mAuth = FirebaseAuth.getInstance()
                    val user = mAuth.currentUser!!
                    val db = FirebaseFirestore.getInstance()

                    val map: MutableMap<String, Any> = HashMap()
                    map["name"] = user.displayName.toString()
                    map["address"] = address
                    map["phone"] = phn

                    db.collection("users").document(user.uid)
                            .set(map)
                            .addOnSuccessListener {
                                Toast.makeText(baseContext, "Done", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(baseContext, "Problem saving data", Toast.LENGTH_SHORT).show()
                            }


                }

            }

        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
