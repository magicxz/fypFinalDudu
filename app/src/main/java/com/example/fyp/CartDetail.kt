package com.example.fyp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.adapter.cartAdapter
import com.example.fyp.model.Cart
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_cart_detail.*

class CartDetail : AppCompatActivity() {

    lateinit var mRecyclerView: RecyclerView
    lateinit var mDatabase: DatabaseReference
    lateinit var query: Query
    lateinit var cart: MutableList<Cart>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_detail)

        cart = mutableListOf()

        mRecyclerView = recyclerView1

        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid

        query = FirebaseDatabase.getInstance().getReference("Carts").orderByChild("userId").equalTo(currentUser)

        val intent = Intent(this,payment::class.java)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                cart.clear()
                if(dataSnapshot!!.exists()){
                    nocart.isVisible = false
                    imgcart.isVisible = false
                    var fee = 0.0
                    var subTotal = 0.0
                    var total = 0.0

                    for(h in dataSnapshot.children){
                        val cartList =h.getValue(Cart::class.java)
                        cart.add(cartList!!)

                        val cartId = cartList.cartId

                        subTotal += cartList.price.toDouble()* cartList.cartQuantity
                        subtotal.text = String.format("%.2f",subTotal)
                        fee = subTotal*0.1
                        deliveryFee.text = String.format("%.2f",fee)
                        total = subTotal + fee
                        totalAmount.text = String.format("%.2f",total)

                        intent.putExtra("Total",totalAmount.text.toString())
                        intent.putExtra("sub",subtotal.text.toString())
                        intent.putExtra("DeliveryFee",deliveryFee.text.toString())
                        intent.putExtra("CartId",cartId)
                    }
                    val adapter = cartAdapter(cart)
                    mRecyclerView.setHasFixedSize(true)
                    mRecyclerView.layoutManager = LinearLayoutManager(this@CartDetail,RecyclerView.VERTICAL,false)
                    mRecyclerView.adapter =adapter
                }else{
                    textView6.isVisible = false
                    subtotal.isVisible = false
                    textView8.isVisible = false
                    deliveryFee.isVisible = false
                    textView9.isVisible = false
                    totalAmount.isVisible = false
                    placeOrder.isVisible = false
                    nocart.isVisible = true
                    imgcart.isVisible = true
                    val adapter = cartAdapter(cart)
                    mRecyclerView.setHasFixedSize(true)
                    mRecyclerView.layoutManager = LinearLayoutManager(this@CartDetail,RecyclerView.VERTICAL,false)
                    mRecyclerView.adapter =adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        placeOrder.setOnClickListener{
            startActivity(intent)
        }

        back1.setOnClickListener{
            this.finish()
        }
    }
}