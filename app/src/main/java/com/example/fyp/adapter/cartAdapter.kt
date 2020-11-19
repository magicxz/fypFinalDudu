package com.example.fyp.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.R
import com.example.fyp.model.Cart
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class cartAdapter (var cart: MutableList<Cart>):
    RecyclerView.Adapter<cartAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.cart_list_layout,parent,false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name1.text = cart[position].foodname
        holder.price1.text = "RM " + cart[position].price.toString()
        holder.quantity.text = cart[position].cartQuantity.toString()

        Picasso.get().load(cart[position].image).into(holder.image1)

        holder.close.setOnClickListener {
            val cartId = cart[position].cartId
            val dialogBuilder = AlertDialog.Builder(holder.close.context)
                .setTitle("Remove Cart Item").setIcon(R.drawable.icon).setPositiveButton("Yes"){ _, _ ->
                    FirebaseDatabase.getInstance().getReference("Carts").child(cartId).removeValue()
                }
                .setNegativeButton("No"){_, _ ->
                }.create()
            dialogBuilder.show()
        }
    }

    override fun getItemCount(): Int {
        return cart.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val name1: TextView = itemView.findViewById(R.id.name2)
        val quantity: TextView = itemView.findViewById(R.id.textView3)
        val price1: TextView = itemView.findViewById(R.id.price1)
        val image1: ImageView = itemView.findViewById((R.id.image2))
        val close : ImageView = itemView.findViewById(R.id.deleteCart)
    }
}