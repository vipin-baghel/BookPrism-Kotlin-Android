package com.bookprism.bookprism.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bookprism.bookprism.R
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(), SearchView.OnQueryTextListener {
    private var recyclerView: RecyclerView? = null
    private var adapter: BookRvAdapter? = null
    private var mAuth: FirebaseAuth? = null
    private val db = FirebaseFirestore.getInstance()


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()
        recyclerView = root.findViewById(R.id.recyclerview)
        val query = db.collection("books").orderBy("rating", Query.Direction.DESCENDING)
        setUpRecyclerView(query)
        val searchView: SearchView = root.findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(this)
        return root
    }

    private fun setUpRecyclerView(query:Query) { //query for vertical recyclerview

        val options = FirestoreRecyclerOptions.Builder<BookModel>()
                .setQuery(query, BookModel::class.java)
                .build()
        // vertical recyclerview adaptor
        adapter = BookRvAdapter(options, context, 1)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        recyclerView!!.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        adapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter!!.stopListening()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {

        val fquery = db.collection("books")
                .orderBy("rating", Query.Direction.DESCENDING)
                .startAt(query)
                .endAt("\uf8ff")
        setUpRecyclerView(fquery)

        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {

        return false
    }


}


