package com.dmstechsolution.shemesu.allactivity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.dmstechsolution.shemesu.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val call=findViewById<FloatingActionButton>(R.id.call)
         val title=findViewById<TextView>(R.id.prod_title)
        val desc=findViewById<TextView>(R.id.prod_desc)
        val price=findViewById<TextView>(R.id.prod_price)

        val intent=Intent()
        title.setText(intent.getStringExtra("name").toString())

        call.setOnClickListener {
            // call to user
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + Intent().getStringExtra("phone"))
            startActivity(dialIntent)
        }
    }
}