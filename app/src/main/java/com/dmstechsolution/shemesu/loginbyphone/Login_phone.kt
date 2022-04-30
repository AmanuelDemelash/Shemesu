package com.dmstechsolution.shemesu.loginbyphone

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.dmstechsolution.shemesu.R
import com.dmstechsolution.shemesu.Utlity
import com.dmstechsolution.shemesu.allactivity.MainActivity
import com.dmstechsolution.shemesu.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.hbb20.CountryCodePicker
import com.mukesh.OtpView
import java.util.concurrent.TimeUnit


class Login_phone : AppCompatActivity() {
    private lateinit var ccp: CountryCodePicker
    private lateinit var butt_cont:Button
    private lateinit var butt_verify:Button
    private lateinit var phone:EditText
    private lateinit var usernamer:EditText
    private lateinit var otp_view: OtpView
    private lateinit var constraint_login :CardView
    private lateinit var constraint_verify:CardView
    private lateinit var progressdealog:ProgressDialog
    private lateinit var root:ScrollView

    private lateinit var auth:FirebaseAuth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var storedVerificationId:String
    private lateinit var resendToken:PhoneAuthProvider.ForceResendingToken

    //firestore
    private lateinit var db:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_phone)
        auth= FirebaseAuth.getInstance()
        db= FirebaseFirestore.getInstance()

        root=findViewById(R.id.root_laayout)
        progressdealog= ProgressDialog(this)
        butt_cont=findViewById(R.id.button_continu)
        butt_verify=findViewById(R.id.button_verify)
        phone=findViewById(R.id.u_name)
        ccp=findViewById(R.id.ccp)
        usernamer=findViewById(R.id.usersnamer)
        otp_view=findViewById(R.id.otp_view)
        constraint_login=findViewById(R.id.phone_constraint)
        constraint_verify=findViewById(R.id.verify_constraint)

        //handle the verification request
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                val code=credential.smsCode
                if (code!=null){
                    otp_view.setText(code)
                }
            }
            override fun onVerificationFailed(e: FirebaseException) {

                Snackbar.make(root," Failed to authenticate",Snackbar.LENGTH_SHORT).show()
                phone.setText("")
                dismissprogess()
                Log.i("auth",e.toString())
                // Show a message and update the UI
            }

            override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
            ) {
                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
                Handler().postDelayed({
                    dismissprogess()
                    constraint_login.visibility=View.GONE
                    constraint_verify.visibility=View.VISIBLE
                },1000)

            }
        }
        // sendverifcation code to phone number
        butt_cont.setOnClickListener {
            if (validate_phoneinput()){
                if (!Utlity().cheekinternet(this))
                    AlertDialog.Builder(this).apply {
                        setMessage("Cheek your internet connection!")
                        setCancelable(false)
                        setPositiveButton("OK") { dialogInterface: DialogInterface, i: Int ->
                            setCancelable(true)
                        }
                        show()
                    }
                val phone_number="+${ccp.selectedCountryCode}${phone.text.toString().trim()}"
                showprogress()
                sendverificationcode(phone_number)
            }

        }
        // verify phone number
        butt_verify.setOnClickListener {

            val recived_verificstion_code=otp_view.text.toString().trim()
            if (validate_verify_input()){
                if (!Utlity().cheekinternet(this))
                    AlertDialog.Builder(this).apply {
                        setMessage("Cheek your internet connection!")
                        setCancelable(false)
                        setPositiveButton("OK") { dialogInterface: DialogInterface, i: Int ->
                        setCancelable(true)
                        }
                        show()
                    }
                showprogress()
                verificationcode(recived_verificstion_code)
            }
            else{
                dismissprogess()
                Snackbar.make(root,"enter correct verification code",Snackbar.LENGTH_SHORT).show()
            }
        }
    }
    private fun validate_phoneinput(): Boolean {
        val phone_num=phone.text.toString().trim()
        val username=usernamer.text.toString().trim()
        if (TextUtils.isEmpty(phone_num)){
            Snackbar.make(root,"enter correct country code and your phone number",Snackbar.LENGTH_SHORT).show()
            return false
        }
        if(TextUtils.isEmpty(username)){
            Snackbar.make(root,"enter user name",Snackbar.LENGTH_SHORT).show()
            return false
        }
            return true
    }
    fun showprogress(){
        progressdealog.setCancelable(false)
        progressdealog.setMessage("Waiting...")
        progressdealog.show()
    }
    fun dismissprogess(){
        progressdealog.dismiss()
    }

    private fun validate_verify_input(): Boolean{
        val vrification_code=otp_view.text.toString().trim()
        if (TextUtils.isEmpty(vrification_code)){
            return false
        }
        return true
    }

    //founftion for sending verification code users phone number
    private fun sendverificationcode(phone_no:String){

        val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phone_no)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verificationcode(code:String){
        val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    // sign in user after verifying phone number
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        val user = task.result?.user

                        // firestore user register
                        val userinfo=User(usernamer.text.toString(),"+${ccp.selectedCountryCode}${phone.text.toString().trim()}")
                           db.collection("User")
                             .document(FirebaseAuth.getInstance().currentUser!!.uid)
                              .set(userinfo, SetOptions.merge())
                              .addOnCompleteListener {
                                    if (it.isSuccessful){
                                        dismissprogess()
                                        val intent=Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                                .addOnFailureListener {
                                    dismissprogess()
                                    Toast.makeText(this,"${it.message}",Toast.LENGTH_SHORT).show()
                                }
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                                dismissprogess()
                            Snackbar.make(root,"Please enter correct code!",Snackbar.LENGTH_SHORT).show()
                            otp_view.setText("")
                        }
                        // Update UI
                    }
                }
    }

    override fun onStart() {
        super.onStart()
        if (!Utlity().cheekinternet(this)) {
            AlertDialog.Builder(this).apply {
                setMessage("Cheek your internet connection!")
                setCancelable(false)
                setPositiveButton("OK") { dialogInterface: DialogInterface, i: Int ->
                    setCancelable(true)
                }
                show()
            }
        }
        if (auth.currentUser!=null){
            val intent=Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        }
    }
