package com.example.fyp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.adapter.cartAdapter
import com.example.fyp.model.Cart
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_cart_detail.*

class CartDetail : AppCompatActivity() {

    lateinit var mRecyclerView: RecyclerView
    lateinit var mDatabase: DatabaseReference
    lateinit var cart: MutableList<Cart>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_detail)

        cart = mutableListOf()

        mRecyclerView = recyclerView1

        mDatabase = FirebaseDatabase.getInstance().getReference("Carts")

        val intent = Intent(this,payment::class.java)

        mDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                cart.clear()

                if(dataSnapshot!!.exists()){
                    var fee = 0.0

                    var subTotal = 0.0

                    var total = 0.0


                    for(h in dataSnapshot.children){
                        val cartList =h.getValue(Cart::class.java)
                        cart.add(cartList!!)

                        subTotal += cartList.price.toDouble()* cartList.cartQuantity

                        subtotal.text = "RM " + String.format("%.2f",subTotal)

                        fee = subTotal*0.1

                        deliveryFee.text = "RM " + String.format("%.2f",fee)

                        total = subTotal + fee

                        totalAmount.text = "RM " + String.format("%.2f",total)
                        intent.putExtra("Total",totalAmount.text.toString())
                        intent.putExtra("sub",subtotal.text.toString())
                        intent.putExtra("DeliveryFee",deliveryFee.text.toString())

                    }

                    val adapter = cartAdapter(cart)

                    //mRecyclerView = findViewById(R.id.recyclerview)

                    mRecyclerView.setHasFixedSize(true)

                    mRecyclerView.layoutManager = LinearLayoutManager(this@CartDetail,RecyclerView.VERTICAL,false)

                    mRecyclerView.adapter =adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        placeOrder.setOnClickListener{

            //val intent = Intent(this,payment::class.java)

            startActivity(intent)
        }

        back1.setOnClickListener{
            this.finish()
        }
    }
}