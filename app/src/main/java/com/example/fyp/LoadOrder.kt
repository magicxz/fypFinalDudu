package com.example.fyp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fyp.adapter.OrderAdapter
import com.example.fyp.model.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.order_layout.*

class LoadOrder : AppCompatActivity() {

    lateinit var orderList : MutableList<Order>
    lateinit var ref : Query

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.order_layout)

        orderList = mutableListOf()

        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        ref = FirebaseDatabase.getInstance().getReference("Orders").orderByChild("userId").equalTo(currentUser)

        ref.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    orderList.clear()
                    norecord.isVisible = false
                    for (h in snapshot.children){
                        val o = h.getValue<Order>(Order::class.java)
                        orderList.add(o!!)
                    }

                    val adapter = OrderAdapter(orderList)

                    val mLayoutManager = LinearLayoutManager(applicationContext)
                    mLayoutManager.reverseLayout = true

                    reOrder.layoutManager = mLayoutManager
                    reOrder.scrollToPosition(orderList.size-1)
                    reOrder.adapter = adapter
                }
            }
        })

        back.setOnClickListener {
            this.finish()
        }
    }
}