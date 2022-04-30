package com.dmstechsolution.shemesu



import android.app.Service
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.dmstechsolution.shemesu.model.Product
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore



class Utlity {
   private var connectivityManager: ConnectivityManager?=null
   private var networkinfo: NetworkInfo?=null

    fun cheekinternet(context: Context):Boolean{
        connectivityManager=context.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(connectivityManager!=null){
            networkinfo=connectivityManager!!.activeNetworkInfo
            if(networkinfo!=null){

                if(networkinfo!!.state==NetworkInfo.State.CONNECTED){
                    return true
                }
            }

        }
        return false
    }





}