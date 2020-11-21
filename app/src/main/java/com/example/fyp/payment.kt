package com.example.fyp

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.fyp.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.paypal.android.sdk.payments.PayPalConfiguration
import com.paypal.android.sdk.payments.PayPalPayment
import com.paypal.android.sdk.payments.PayPalService
import com.paypal.android.sdk.payments.PaymentActivity
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.home.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class payment : AppCompatActivity() {

    lateinit var ref1: DatabaseReference
    lateinit var ref2: DatabaseReference
    lateinit var addressList: MutableList<Address>
    lateinit var cartList : MutableList<Cart>
    lateinit var foodList : MutableList<Food>
    lateinit var dialog : Dialog
    lateinit var dialog1 : Dialog
    var config:PayPalConfiguration?=null
    var amount:Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        var currentUser= FirebaseAuth.getInstance().currentUser!!.uid

        ref2 = FirebaseDatabase.getInstance().getReference().child("Address")
        ref1 = FirebaseDatabase.getInstance().getReference().child("Users")

        addressList= mutableListOf()
        cartList= mutableListOf()
        foodList= mutableListOf()

        back1.setOnClickListener{
            this.finish()
        }

        ref1.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    addressList.clear()
                    for (h in snapshot.children) {
                        val u = h.getValue(Address::class.java)
                        addressList.add(u!!)
                    }
                }
                ref2.addValueEventListener(object:ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            addressList.clear()
                            //userList.clear()
                            for (j in snapshot.children) {
                                if(j.child("userId").getValue().toString().equals(currentUser)){
                                    textView11.text = j.child("addressLine").getValue().toString()
                                }
                            }
                        }
                    }
                })
            }
        })

        dialog1 = Dialog(this)
        dialog1.setContentView(R.layout.okalertbox)
        val content = dialog1.findViewById<Button>(R.id.txt1)

        dialog = Dialog(this)
        dialog.setContentView(R.layout.confirm_payment)
        val yes = dialog.findViewById<Button>(R.id.yesBtn)
        val cancel = dialog.findViewById<Button>(R.id.cancelBtn)
        var total = intent.getStringExtra("Total")
        payAmount.text = "RM " + total

        payment.setOnClickListener{
            var currentUser= FirebaseAuth.getInstance().currentUser!!.uid
            val ref = FirebaseDatabase.getInstance().getReference("Orders")
            val ref1 = FirebaseDatabase.getInstance().getReference("Carts").orderByChild("userId").equalTo(currentUser)
            val ref3 = FirebaseDatabase.getInstance().getReference("OrderDetails")
            val orderId = ref.push().key
            var total = intent.getStringExtra("Total")
            var sub = intent.getStringExtra("sub")
            var deliveryFee = intent.getStringExtra("DeliveryFee")

            var store = 0

            yes.setOnClickListener {
                if(textView11.text.toString() != "") {
                    if (radioButton3.isChecked) {
                        ref1.addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                if(store == 0) {
                                    if (snapshot.exists()) {
                                        cartList.clear()
                                        for (h in snapshot.children) {
                                            val cart = h.getValue(Cart::class.java)
                                            cartList.add(cart!!)

                                            val foodId = cart.foodId
                                            val qty = cart.cartQuantity
                                            val rema = cart.remark
                                            var orderDetailId = ref3.push().key

                                            val storeOrderDetail =
                                                orderDetail(orderDetailId!!, foodId, orderId!!, qty,rema)
                                            ref3.child(orderDetailId).setValue(storeOrderDetail)
                                        }
                                        for (x in cartList) {
                                            Log.d("abc", x.cartId)
                                            FirebaseDatabase.getInstance().getReference("Carts")
                                                .child(x.cartId).removeValue()
                                        }
                                    }
                                    store++
                                    val ok = dialog1.findViewById<Button>(R.id.ok)
                                    content.text = "Paid Successful !"
                                    ok.setOnClickListener {
                                        dialog1.dismiss()
                                        Toast.makeText(applicationContext, "Order Success!!!", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(applicationContext, Home::class.java))
                                    }
                                    dialog1.setCancelable(true)
                                    dialog1.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                    dialog1.show()
                                }
                            }
                        })
                        val storeOrder = Order(orderId!!, getTime(), "pending", sub.toDouble(), deliveryFee.toDouble(), total.toDouble(), radioButton3.text.toString(), currentUser)
                        ref.child(orderId).setValue(storeOrder)
                    }else if(paypal.isChecked){
                        var total = intent.getStringExtra("Total")
                        config=PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX).clientId(UserInfo.clientId)
                        var i = Intent(this,PayPalService::class.java)
                        i.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config)
                        startService(i)

                        amount = total.toDouble()
                        var pay = PayPalPayment(BigDecimal.valueOf(amount),"USD","24 Fats App",PayPalPayment.PAYMENT_INTENT_SALE)
                        val intent = Intent(this,PaymentActivity::class.java)
                        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config)
                        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,pay)
                        startActivityForResult(intent,123)
                    } else {
                        dialog.dismiss()
                        Toast.makeText(applicationContext, "Please select your payment method", Toast.LENGTH_LONG).show()
                    }

                }else{
                    dialog.dismiss()
                    Toast.makeText(applicationContext, "Please add your address first !!", Toast.LENGTH_SHORT).show()
                }
            }
            cancel.setOnClickListener {
                dialog.dismiss()
            }
            dialog.setCancelable(true)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==123){
            if(resultCode== Activity.RESULT_OK){
                val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
                var store = 0
                val ref = FirebaseDatabase.getInstance().getReference("Orders")
                val orderId = ref.push().key
                val ref3 = FirebaseDatabase.getInstance().getReference("OrderDetails")
                val ref1 = FirebaseDatabase.getInstance().getReference("Carts").orderByChild("userId").equalTo(currentUser)
                var total = intent.getStringExtra("Total")
                var sub = intent.getStringExtra("sub")
                var deliveryFee = intent.getStringExtra("DeliveryFee")
                ref1.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(store == 0) {
                            if (snapshot.exists()) {
                                cartList.clear()
                                for (h in snapshot.children) {
                                    val cart = h.getValue(Cart::class.java)
                                    cartList.add(cart!!)

                                    val foodId = cart.foodId
                                    val qty = cart.cartQuantity
                                    val rema = cart.remark
                                    var orderDetailId = ref3.push().key

                                    val storeOrderDetail =
                                        orderDetail(orderDetailId!!, foodId, orderId!!, qty,rema)
                                    ref3.child(orderDetailId).setValue(storeOrderDetail)
                                }
                                for (x in cartList) {
                                    Log.d("abc", x.cartId)
                                    FirebaseDatabase.getInstance().getReference("Carts")
                                        .child(x.cartId).removeValue()
                                }
                            }
                            store++
                        }
                    }
                })
                dialog.dismiss()
                val storeOrder = Order(orderId!!, getTime(), "paid", sub.toDouble(), deliveryFee.toDouble(), total.toDouble(), paypal.text.toString(), currentUser)
                ref.child(orderId).setValue(storeOrder)
                Toast.makeText(applicationContext, "Order Success!!!", Toast.LENGTH_SHORT).show()
                val ok = dialog1.findViewById<Button>(R.id.ok)
                ok.setOnClickListener {
                    dialog1.dismiss()
                    startActivity(Intent(this,Home::class.java))
                }
                dialog1.setCancelable(true)
                dialog1.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog1.show()
            }
        }
    }

    private fun getTime(): String {

        val today = LocalDateTime.now(ZoneId.systemDefault())

        return today.format(DateTimeFormatter.ofPattern("d MMM uuuu HH:mm:ss "))
    }

    override fun onDestroy() {
        stopService(Intent(this,PayPalService::class.java))
        super.onDestroy()
    }
}