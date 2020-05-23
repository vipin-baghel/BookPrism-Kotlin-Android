package com.bookprism.bookprism

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {
    private var mAppBarConfiguration: AppBarConfiguration? = null
    var header: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //initializing fresco
        Fresco.initialize(this)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        header = navigationView.getHeaderView(0)
        // Passing each menu ID as a set of Ids because each
// menu should be considered as top level destinations.
        mAppBarConfiguration = AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile,R.id.nav_add,R.id.nav_borrow,
                R.id.nav_borrowed,R.id.nav_lent)
                .setDrawerLayout(drawer)
                .build()
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration!!)
        NavigationUI.setupWithNavController(navigationView, navController)
        val mAuth : FirebaseAuth = FirebaseAuth.getInstance()
        val user: FirebaseUser? = mAuth.currentUser
        if (user == null) {
            val url = Uri.parse("https://firebasestorage.googleapis.com/v0/b/book-prism.appspot.com/o/profilepic.jpg?alt=media&token=a5472cec-174b-40e5-9494-63d7aa552184")
            updateHeader("Guest", "", url)
        }else{
            updateHeader(user.displayName,user.email,user.photoUrl)
        }
    }

    fun updateHeader(name: String?, email: String?, picUrl: Uri?) {
        val nameTv = header!!.findViewById<TextView>(R.id.name_tv)
        nameTv.text = name
        val mailTv = header!!.findViewById<TextView>(R.id.mail_tv)
        mailTv.text = email
        val pic: SimpleDraweeView = header!!.findViewById(R.id.profile_pic)
        pic.setImageURI(picUrl)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean { // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        return (NavigationUI.navigateUp(navController, mAppBarConfiguration!!)
                || super.onSupportNavigateUp())
    }
}