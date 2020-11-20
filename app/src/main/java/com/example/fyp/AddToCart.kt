package com.example.fyp

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.fyp.model.Cart
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_to_cart.*

class AddToCart : AppCompatActivity() {

    lateinit var foodName: TextView
    lateinit var foodPrice: TextView
    lateinit var dialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_to_cart)

        var intent = intent

        val id = intent.getStringArrayExtra("DetailId")
        val name = intent.getStringExtra("Name")
        val price = intent.getStringExtra("Price")
        val image = intent.getStringExtra("Image")

        foodName = findViewById(R.id.textView)
        foodName.text = name
        foodPrice = findViewById(R.id.price3)
        foodPrice.text = price

        Picasso.get().load(image).into(imageView4)

        placeOrder.setOnClickListener{

            placeOrder(name,price,image)

        }

        number.text = "1"

        totalAmount1.text = "RM " + price.toString()

        minus.setOnClickListener{
            if(!(number.text.toString().toInt().equals(1))){
                number.text = (number.text.toString().toInt()-1).toString()

                totalAmount1.text = (number.text.toString().toInt()*price.toDouble()).toString()

            }

        }
        plus.setOnClickListener{
            number.text = (number.text.toString().toInt()+1).toString()
            totalAmount1.text = (number.text.toString().toInt()*price.toDouble()).toString()

        }
        
        back1.setOnClickListener {
            this.finish()
        }
    }

    private fun placeOrder(name :String, price: String, image:String) {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.confirmation)
        val yes = dialog.findViewById<Button>(R.id.yesBtn)
        val no = dialog.findViewById<Button>(R.id.cancelBtn)
        val id = intent.getStringExtra("FoodId")
        val img = dialog.findViewById<ImageView>(R.id.img1)
        val fname = dialog.findViewById<TextView>(R.id.foodname)

        val ref1 = FirebaseDatabase.getInstance().getReference("Food").orderByChild("foodId").equalTo(id)

        ref1.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for(h in snapshot.children){
                        val foodimage = h.child("image").getValue().toString()
                        val name = h.child("name").getValue().toString()
                        Picasso.get().load(foodimage).into(img)
                        fname.text = name
                    }
                }
            }

        })

        val ref = FirebaseDatabase.getInstance().getReference("Carts")
        var remark = findViewById<EditText>(R.id.remark)
        val cartId = ref.push().key
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        val foodId = intent.getStringExtra("FoodId")

        if(remark.text.toString() == ""){
            remark.setText("no remark")
        }

        yes.setOnClickListener {
            val cartItem = Cart(
                cartId.toString(),
                currentUser,
                number.text.toString().toInt(),
                name,
                price.toDouble(),
                remark.text.toString(),
                image,
                foodId
            )

            ref.child(cartId.toString()).setValue(cartItem)
            dialog.dismiss()
            Toast.makeText(this,"Add Successful !!!",Toast.LENGTH_SHORT).show()
            this.finish()
        }

        no.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

    }

}
