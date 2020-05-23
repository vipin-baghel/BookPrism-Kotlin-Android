package com.bookprism.bookprism.ui.aboutus

import android.app.Fragment
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes

import com.bookprism.bookprism.R


class AboutUsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_about_us, container, false)

        val mailTV:TextView = root.findViewById(R.id.mailTV);
        mailTV.setOnClickListener {
            sendEmail()
        }


        return root
    }

    private fun sendEmail() {

        val recipient:String = getString(R.string.email)
        val mIntent = Intent(Intent.ACTION_SENDTO,
                Uri.fromParts("mailto", recipient, null))

        try {
            //start email intent
            startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
        }
        catch (e: Exception){
            //if any thing goes wrong for example no email client application or any exception
            //get and show exception message
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }

    }

    companion object {

    }
}
