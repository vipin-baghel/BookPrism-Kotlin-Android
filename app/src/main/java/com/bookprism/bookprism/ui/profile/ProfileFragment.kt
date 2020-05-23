package com.bookprism.bookprism.ui.profile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bookprism.bookprism.MainActivity
import com.bookprism.bookprism.R
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_profile.*
import java.util.*

class ProfileFragment : Fragment() {
    private lateinit var root: View
    private lateinit var signInButton: SignInButton
    private lateinit var signOutLayout: LinearLayout
    private lateinit var changeBtn:Button
    private var mAuth: FirebaseAuth? = null
    private val db = FirebaseFirestore.getInstance()
    private var sharedPreferences: SharedPreferences? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? { // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_profile, container, false)
        sharedPreferences = activity?.getSharedPreferences("p", Context.MODE_PRIVATE)
        val signOutBtn = root.findViewById<Button>(R.id.sign_out)
        signOutLayout = root.findViewById(R.id.sign_out_layout)
        signOutLayout.visibility = View.GONE
        signOutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            sharedPreferences!!.edit().putString("name", "").apply()
            sharedPreferences!!.edit().putString("uid", "").apply()
            Snackbar.make(root, "Signed Out", Snackbar.LENGTH_SHORT).show()
            assert(fragmentManager != null)
            fragmentManager!!.beginTransaction().detach(this@ProfileFragment).attach(this@ProfileFragment).commit()
            (Objects.requireNonNull(activity) as MainActivity)
                    .updateHeader(
                            "Guest",
                            "",
                            Uri.parse("https://firebasestorage.googleapis.com/v0/b/book-prism.appspot.com/o/profilepic.jpg?alt=media&token=a5472cec-174b-40e5-9494-63d7aa552184"))
        }
        signInButton = root.findViewById(R.id.sign_in_button)
        signInButton.setOnClickListener { signIn() }

        changeBtn = root.findViewById(R.id.change)
        changeBtn.setOnClickListener{ startActivity(Intent(context,AddressActivity::class.java))}
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()
        return root
    }

    private fun signIn() { // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        // GoogleSignInClient with the options specified by gso.
        val mGoogleSignInClient = context?.let { GoogleSignIn.getClient(it, gso) }
        // Clearing cache
        mGoogleSignInClient?.signOut()
        mGoogleSignInClient?.revokeAccess()
        // signing in user
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try { // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) { // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                Snackbar.make(root, "Google sign in failed", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct!!.id)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        activity?.let {
            mAuth!!.signInWithCredential(credential)
                    .addOnCompleteListener(it) { task ->
                        if (task.isSuccessful) { // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success")
                            val user = mAuth!!.currentUser
                            if (user != null) {
                                val name = user.displayName
                                val uid = user.uid
                                sharedPreferences!!.edit().putString("name", name).apply()
                                sharedPreferences!!.edit().putString("uid", uid).apply()
                                //Checking if database exists for this user
                                db.collection("users").document(uid)
                                        .get()
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                val document = task.result!!.data
                                                if (document != null) {
                                                    if (sharedPreferences!!.getString("address","") == ""){
                                                        val add = document["address"].toString()
                                                        val phn = document["phone"].toString()

                                                        sharedPreferences!!.edit().putString("address", add).apply()
                                                        sharedPreferences!!.edit().putString("phone", phn).apply()

                                                        updateAddressUI()

                                                    }

                                                } else {
                                                    Log.d(TAG, "No such document")
                                                    startActivity(Intent(context,AddressActivity::class.java))
                                                }
                                            } else {
                                                Log.d(TAG, "get failed with ", task.exception)
                                            }
                                        }
                            }
                            Snackbar.make(root, "Signed In Successfully", Snackbar.LENGTH_SHORT).show()
                            updateUI(user)
                        } else { // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.exception)
                            Snackbar.make(root, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                            updateUI(null)
                        }
                    }
        }
    }

    private fun updateAddressUI() {

        val ad = sharedPreferences?.getString("address","")
        val pn = sharedPreferences?.getString("phone","")
        address.text = ad
        phone.text = pn

    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth!!.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            signInButton.visibility = View.GONE
            signOutLayout.visibility = View.VISIBLE
            val profilePic: SimpleDraweeView = root.findViewById(R.id.profile_pic)
            val name = root.findViewById<TextView>(R.id.name_tv)
            profilePic.setImageURI(currentUser.photoUrl)
            name.text = currentUser.displayName
            (Objects.requireNonNull(activity) as MainActivity)
                    .updateHeader(
                            currentUser.displayName,
                            currentUser.email,
                            currentUser.photoUrl)

            updateAddressUI()
        }
    }

    companion object {
        private const val TAG = "TAG"
        private const val RC_SIGN_IN = 101
    }
}