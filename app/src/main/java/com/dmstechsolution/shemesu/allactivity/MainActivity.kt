package com.dmstechsolution.shemesu.allactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dmstechsolution.shemesu.R
import com.dmstechsolution.shemesu.adapter.ProductAdapter
import com.dmstechsolution.shemesu.model.Product
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private var backclicked=false
    private var listofpro:ArrayList<Product> = arrayListOf()
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rec_main=findViewById<RecyclerView>(R.id.rec_main)
        val fab=findViewById<FloatingActionButton>(R.id.floatingActionButton)

        val adapter= ProductAdapter(listofpro,this)
        rec_main.layoutManager= StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)//LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        rec_main.adapter=adapter

        fab.setOnClickListener {
            startActivity(Intent(this,SellActivity::class.java))
        }
    }
    override fun onStart() {
        super.onStart()
        db.collection("products")
            .addSnapshotListener { value, error ->
                if (error == null) {
                    for (dc: DocumentChange in value!!.documentChanges) {
                        if (dc.type== DocumentChange.Type.ADDED){
                            listofpro.add(dc.document.toObject(Product::class.java))
                        }
                    }
                }
            }
       // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }
    override fun onBackPressed() {
        if (backclicked){
            super.onBackPressed()
        }
        backclicked=true
        Toast.makeText(this,"click again to exit", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({
            backclicked=false
        },2000)
    }
}