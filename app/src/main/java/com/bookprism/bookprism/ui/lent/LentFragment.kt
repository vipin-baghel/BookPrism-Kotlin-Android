package com.bookprism.bookprism.ui.lent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bookprism.bookprism.R
import com.bookprism.bookprism.ui.home.BookModel
import com.bookprism.bookprism.ui.home.BookRvAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class LentFragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var adapter: BookRvAdapter? = null
    private var mAuth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? { // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_lent, container, false)
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth!!.currentUser
        recyclerView = root.findViewById(R.id.recyclerview)
        if (currentUser == null) {
            Toast.makeText(context, "Please Login First", Toast.LENGTH_LONG).show()
        } else {
            setUpRecyclerView()
        }
        return root
    }

    private fun setUpRecyclerView() { //query for vertical recyclerview
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("users").document(currentUser!!.uid)
                .collection("lentBooksByMe").orderBy("rating", Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<BookModel>()
                .setQuery(query, BookModel::class.java)
                .build()
        // vertical recyclerview adaptor
        adapter = BookRvAdapter(options, context, 0)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        recyclerView!!.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        if (currentUser != null) {
            adapter!!.startListening()
        }
    }

    override fun onStop() {
        super.onStop()
        if (currentUser != null) {
            adapter!!.stopListening()
        }
    }
}