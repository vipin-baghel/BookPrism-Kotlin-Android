package com.bookprism.bookprism.ui.home

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bookprism.bookprism.R
import com.facebook.drawee.view.SimpleDraweeView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class BookRvAdapter(options: FirestoreRecyclerOptions<BookModel?>, var context: Context?, setClick: Int) : FirestoreRecyclerAdapter<BookModel, BookRvAdapter.Holder>(options) {
    var setClick: Int = setClick
    var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val v = LayoutInflater.from(parent.context).inflate(
                R.layout.rv_item, parent, false)
        return Holder(v)
    }

    override fun onBindViewHolder(holder: Holder, position: Int, model: BookModel) {
        holder.nameTV.text = model.name
        holder.authorTV.text = "by " + model.author
        holder.ratingTV.text = "Rating : " + model.rating
        holder.costTv.text = "₹ " + model.cost
        holder.commentTv.text = model.comment
        holder.img.setImageURI(model.imgUrl)
        val data: MutableMap<String, Any?> = HashMap()
        data["name"] = model.name
        data["author"] = model.author
        data["rating"] = model.rating
        data["cost"] = model.cost
        data["comment"] = model.comment
        data["imgUrl"] = model.imgUrl
        if (setClick == 1) {
            holder.itemLayout.setOnClickListener {
                // On click
                if (mAuth.currentUser == null) {
                    Toast.makeText(context, "Please Sign in First", Toast.LENGTH_SHORT).show()
                } else {
                    val i = Intent(context, BookDetailsActivity::class.java)
                    i.putExtra("name", model.name)
                    i.putExtra("author", model.author)
                    i.putExtra("rating", model.rating)
                    i.putExtra("cost", model.cost)
                    i.putExtra("comment", model.comment)
                    i.putExtra("imgUrl",model.imgUrl)
                    context!!.startActivity(i)
                }
            }
        } else if (setClick == 2) {
            holder.itemLayout.setOnClickListener {
                // On click
                if (mAuth.currentUser == null) {
                    Toast.makeText(context, "Please Sign in First", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, " Borrowing ! ", Toast.LENGTH_SHORT).show()
                    db.collection("users").document(mAuth.currentUser!!.uid)
                            .collection("BooksBorrowed")
                            .add(data)
                            .addOnSuccessListener { Toast.makeText(context, " Borrowed ! ", Toast.LENGTH_SHORT).show() }
                            .addOnFailureListener { e -> Toast.makeText(context, "Failed with exception : $e", Toast.LENGTH_LONG).show() }
                }
            }
        }
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameTV: TextView
        var authorTV: TextView
        var ratingTV: TextView
        var commentTv: TextView
        var costTv: TextView
        var itemLayout: LinearLayout
        var img: SimpleDraweeView

        init {
            nameTV = itemView.findViewById(R.id.name_tv)
            authorTV = itemView.findViewById(R.id.author_tv)
            ratingTV = itemView.findViewById(R.id.rating_tv)
            costTv = itemView.findViewById(R.id.cost_tv)
            commentTv = itemView.findViewById(R.id.comment_tv)
            itemLayout = itemView.findViewById(R.id.item_layout)
            img = itemView.findViewById(R.id.pic)
        }
    }

}