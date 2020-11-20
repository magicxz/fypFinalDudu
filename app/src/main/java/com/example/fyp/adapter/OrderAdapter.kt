package com.example.fyp.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp.CountOrder
import com.example.fyp.R
import com.example.fyp.model.Order
import com.example.fyp.model.Users
import com.example.fyp.order_detail
import com.google.firebase.database.*

class OrderAdapter(var order : MutableList<Order>): RecyclerView.Adapter<OrderAdapter.MyViewHolder>() {

    lateinit var query : Query
    lateinit var orderList : MutableList<Order>

    inner class MyViewHolder(itemView : View):RecyclerView.ViewHolder(itemView){
        val msg = itemView.findViewById<TextView>(R.id.message1)
        val ordertime = itemView.findViewById<TextView>(R.id.orderDateTime)
        val orderBackground = itemView.findViewById<CardView>(R.id.orderBackground)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.load_orderdetail,parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return order.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.msg.text = "You make an order !"
        holder.ordertime.text = order[position].orderDateTime

        orderList = mutableListOf()

        holder.orderBackground.setOnClickListener{
            query = FirebaseDatabase.getInstance().getReference("Orders").child(order[position].orderId)

            query.addValueEventListener(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val o = snapshot.getValue(Order::class.java)
                        orderList.add(o!!)
                        CountOrder.getOrder = o

                        query = FirebaseDatabase.getInstance().getReference("Users")
                            .orderByChild("uid").equalTo(orderList[0].userId)

                        query.addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }

                            override fun onDataChange(p0: DataSnapshot) {

                                if (p0.exists()) {
                                    for (h in p0.children) {
                                        val user = h.getValue(Users::class.java)
                                        CountOrder.getUser = user!!

                                        val intent = Intent(holder.orderBackground.context,
                                            order_detail::class.java)
                                        intent.putExtra("OrderId", CountOrder.getOrder.orderId)
                                        Log.d("abc123",CountOrder.getOrder.orderId )
                                        intent.putExtra("PaymentMethod", CountOrder.getOrder.paymentMethod)
                                        intent.putExtra("Status", CountOrder.getOrder.status)
                                        intent.putExtra("OrderDateTime", CountOrder.getOrder.orderDateTime)
                                        //intent.putExtra("SubTotal", CountOrder.getOrder.subtotal)
                                        //intent.putExtra("DeliveryFee", CountOrder.getOrder.deliveryfee)
                                        //intent.putExtra("TotalAmount", CountOrder.getOrder.totalAmount)
                                        intent.putExtra("UserId", CountOrder.getUser.uid)
                                        holder.orderBackground.context.startActivity(intent)
                                    }
                                }
                            }
                        })
                    }
                }
            })
        }
    }
}