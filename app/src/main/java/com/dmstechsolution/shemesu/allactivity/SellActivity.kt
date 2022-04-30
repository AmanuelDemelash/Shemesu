package com.dmstechsolution.shemesu.allactivity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dmstechsolution.shemesu.R
import com.dmstechsolution.shemesu.Utlity
import com.dmstechsolution.shemesu.model.Product
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class SellActivity : AppCompatActivity() {
    private lateinit var proname: EditText
    private lateinit var prodesc: EditText
    private lateinit var proprice: EditText
    private lateinit var sellbut: Button
    private lateinit var rootlay: ConstraintLayout
    private lateinit var proimage: ImageView
    private  var imageurl: Uri?=null
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sell)

        auth= FirebaseAuth.getInstance()
        db= FirebaseFirestore.getInstance()

        proname=findViewById(R.id.pro_name)
        prodesc=findViewById(R.id.pro_desc)
        proprice=findViewById(R.id.pro_pric)
        sellbut=findViewById(R.id.pro_sell)
        rootlay=findViewById(R.id.R_id_rootlayout)
        proimage=findViewById(R.id.pro_image)

        proimage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this.applicationContext,android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                getimage()
            }
            else{
                ActivityCompat.requestPermissions(this as Activity, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),12)
            }
        }
        sellbut.setOnClickListener {
            addproduct()
        }
    }
    private fun addproduct(){
        if (validateinput()) {
            if (Utlity().cheekinternet(this)){
                var progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Posting....")
                progressDialog.setCancelable(false)
                progressDialog.show()
                var formater = SimpleDateFormat("yyyy_mm_dd", Locale.getDefault())
                val now = Date()
                var filename = formater.format(now)

                var storageReference =
                    FirebaseStorage.getInstance().getReference("products/$filename")
                storageReference.putFile(imageurl!!)
                    .addOnSuccessListener{
                        progressDialog.dismiss()
                        Toast.makeText(this,"uploaded${storageReference.downloadUrl}",
                            Toast.LENGTH_SHORT).show()
                        storageReference.downloadUrl.addOnSuccessListener {
                            val product = Product(
                                proname.text.toString(),
                                prodesc.text.toString(),
                                it.toString(),
                                proprice.text.toString(),
                                FirebaseAuth.getInstance().currentUser!!.uid
                            )
                            db.collection("products")
                                .document()
                                .set(product, SetOptions.merge())
                                .addOnSuccessListener {
                                    Handler().postDelayed({
                                        startActivity(Intent(this,MainActivity::class.java))
                                        finish()
                                    }, 100)
                                }
                        }


                    }
                    .addOnFailureListener{
                        progressDialog.dismiss()
                        Toast.makeText(this,"${it.message.toString()}", Toast.LENGTH_SHORT).show()
                    }
            }else{
                Toast.makeText(this,"cheek your internet connection..", Toast.LENGTH_SHORT).show()
            }

        }
    }
    private fun getimage() {
        val intent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(intent,11)
    }
    fun validateinput():Boolean{
        return if (TextUtils.isEmpty(proname.text)|| TextUtils.isEmpty(prodesc.text)|| TextUtils.isEmpty(proprice.text)||imageurl==null){
            Snackbar.make(rootlay,"Enter product information ", Snackbar.LENGTH_SHORT).show()
            false
        }
        else true
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==11 && resultCode==Activity.RESULT_OK){
            proimage.scaleType=ImageView.ScaleType.CENTER_CROP
            proimage.setImageURI(data!!.data)
            imageurl= data.data
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==12){
            if (grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getimage()
            }
            else{
                Toast.makeText(this,"you can grant permission later...", Toast.LENGTH_SHORT).show()
            }
        }
    }
}