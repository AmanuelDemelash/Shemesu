package com.dmstechsolution.shemesu.adapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dmstechsolution.shemesu.R
import com.dmstechsolution.shemesu.allactivity.DetailActivity
import com.dmstechsolution.shemesu.model.Product
import com.dmstechsolution.shemesu.model.User
import com.google.firebase.firestore.FirebaseFirestore

class ProductAdapter(private val pro: ArrayList<Product>, val context: Context):RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    private var db:FirebaseFirestore=FirebaseFirestore.getInstance()
    class ViewHolder(view: View):RecyclerView.ViewHolder(view){

        val product_image=view.findViewById<ImageView>(R.id.product_image)
        val product_price=view.findViewById<TextView>(R.id.price)
        val pro_title=view.findViewById<TextView>(R.id.protitle)
        val prodesc=view.findViewById<TextView>(R.id.prodesc)
        val card=view.findViewById<CardView>(R.id.card_item)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate= LayoutInflater.from(parent.context).inflate(R.layout.product_item,parent,false)
        return ViewHolder(inflate)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product=pro[position]
        var phone=""
        db.collection("User")
            .document(product.ownerId)
            .get()
            .addOnSuccessListener {
             phone = it.toObject(User::class.java)!!.phone.toString()
            }
        holder.product_price.text=product.price+" Birr"
        holder.pro_title.text=product.name
        holder.prodesc.text=product.description
        Glide.with(context)
            .load(product.image)
            .centerCrop()
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.product_image);
        holder.card.setOnClickListener {
          val intent=Intent(it.context,DetailActivity::class.java)
            intent.putExtra("name",product.name)
            intent.putExtra("desc",product.description)
            intent.putExtra("price",product.price)
            intent.putExtra("phone",phone)
            it.context.startActivity(intent)
        }

    }
    override fun getItemCount(): Int {
        return pro.size
    }

}