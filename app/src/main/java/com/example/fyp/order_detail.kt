package com.example.fyp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fyp.model.Address
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_order_detail.*

class order_detail : AppCompatActivity() {

    lateinit var ref : DatabaseReference
    lateinit var ref1 : DatabaseReference
    lateinit var ref2 : DatabaseReference
    lateinit var addressList:MutableList<Address>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        orderid.text = intent.getStringExtra("OrderId")
        paymentMethod.text = intent.getStringExtra("PaymentMethod")
        status.text = intent.getStringExtra("Status")
        //subtot.text = intent.getStringExtra("SubTotal")
        //delifee.text = intent.getStringExtra("DeliveryFee")
        //total.text = intent.getStringExtra("TotalAmount")
        orderdatetime.text = intent.getStringExtra("OrderDateTime")
        val userId = intent.getStringExtra("UserId")


        back1.setOnClickListener {
            this.finish()
        }

        ref = FirebaseDatabase.getInstance().getReference("Orders")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for(h in snapshot.children){
                        if(h.child("userId").getValue().toString().equals(userId)){
                            val sub = h.child("subtotal").getValue().toString()
                            val deli = h.child("deliveryfee").getValue().toString()
                            val tot = h.child("totalAmount").getValue().toString()
                            subtot.text = sub
                            delifee.text = deli
                            total.text = tot
                        }
                    }
                }
            }
        })

        ref = FirebaseDatabase.getInstance().getReference("Address")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for(h in snapshot.children){
                        if(h.child("userId").getValue().toString().equals(userId)){
                            val address = h.child("addressLine").getValue().toString()
                            currentAddr.text = address
                        }
                    }
                }
            }
        })

        ref = FirebaseDatabase.getInstance().getReference("Carts")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for(h in snapshot.children){
                        if(h.child("userId").getValue().toString().equals(userId)){
                            val r = h.child("remark").getValue().toString()
                            remark.text = r
                        }
                    }
                }
            }
        })
    }
}