package com.example.fyp

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.fyp.model.Address
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.add_address.*
import java.util.*

class AddAddress : AppCompatActivity(){

    lateinit var usersRef: DatabaseReference
    lateinit var ref: DatabaseReference
    lateinit  var addressList : MutableList<Address>
    var address = Address()
    var valid: Boolean = false
    lateinit var epicDialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_address)

        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val search = findViewById<EditText>(R.id.searchAddress)
        epicDialog = Dialog(this)

        ref = FirebaseDatabase.getInstance().getReference("Address")

        Places.initialize(application,"AIzaSyCuM194Wot9yEMDanGPFzJvUGlSo5byW2I")

        search.setOnClickListener {
            var fieldList : List<Place.Field>  = Arrays.asList(
                Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME
            )

            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fieldList
            ).build(this)

            startActivityForResult(intent, 100)
        }

        back1.setOnClickListener {
            startActivity(Intent(this, LoadAddress::class.java))
            this.finish()
        }

        addressline.addTextChangedListener {
            val addLine = addressline.text.toString().trim()
            val ref = FirebaseDatabase.getInstance().getReference("Address")
            var exist = "false"
            ref.addValueEventListener(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        for (h in snapshot.children){
                            if (h.child("addressLine").getValue().toString().equals(addLine) && h.child("userId").getValue().toString().equals(currentUserID)){
                                exist = "true"
                                hiddenexit.text = exist
                                Log.d("abc",exist)
                                validateAddress(currentUserID)
                            }else {
                                hiddenexit.text =exist
                                validateAddress(currentUserID)
                            }
                        }
                    }
                }
            })
            validateAddress(currentUserID)
        }

        save.setOnClickListener {
            val addLine= addressline.text.toString().trim()



            if(!valid && validateAddress(currentUserID)){
                val addressType = findViewById<EditText>(R.id.addressType)
                val addressLine = findViewById<EditText>(R.id.addressline)
                val addressLine2 = findViewById<EditText>(R.id.addressline2)
                val city = findViewById<EditText>(R.id.city)
                val state = findViewById<EditText>(R.id.state)
                val postcode = findViewById<EditText>(R.id.postcode)
                var addressId = ref.push().key.toString()
                val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid

                val mapAddress = Address(
                    addressId,
                    addressType.text.toString(),
                    addressLine.text.toString(),
                    addressLine2.text.toString(),
                    city.text.toString(),
                    state.text.toString(),
                    postcode.text.toString(),
                    currentUserID
                )

                ref.child(addressId).setValue(mapAddress).addOnCompleteListener{task ->
                    if(task.isSuccessful){
                        addressLine.setError(null)
                        Toast.makeText(applicationContext,"Add Successful!!!",Toast.LENGTH_LONG).show()
                        showDialog()
                    }else{
                        Toast.makeText(applicationContext,"Add Fail...",Toast.LENGTH_LONG).show()
                    }
                }
                //showDialog()
            }

        }
    }

    private fun validateAddress(currentUserID : String):Boolean{
        addressline.setError(null)

        if(hiddenexit.text.equals("true")) {
            addressline.setError("This address is already exist")
            return false
        }

        if(addressType.text.isEmpty()){
            addressType.setError("Address Type cannot be empty")
            return false
        }

        if(addressline2.text.isEmpty()){
            addressline2.setError("Address Line 2 cannot be empty")
            return false
        }

        if(state.text.isEmpty()){
            state.setError("State cannot be empty")
            state.requestFocus()
            return false
        }

        if(postcode.text.isEmpty()) {
            postcode.setError("State cannot be empty")
            return false
        }

        if(postcode.text.length > 5) {
            postcode.setError("Postcode cannot be over 5 digit!")
            return false
        }

        if(city.text.isEmpty()) {
            city.setError("State cannot be empty")
            return false
        }

        if(addressline.text!!.isEmpty()){
            addressline.setError("Address Line cannot be empty")
            return false
        }else{
            addressline.setError(null)
            return true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100 && resultCode == Activity.RESULT_OK) {

            val place = Autocomplete.getPlaceFromIntent(data!!)

            searchAddress.setText(place.name)

            addressline.setText(place.name)

        }else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            val status = Autocomplete.getStatusFromIntent(data!!)

            Toast.makeText(this,status.statusMessage, Toast.LENGTH_SHORT).show()
        }
    }


    private fun addAddress(addLine : String){
        val alertbox = AlertDialog.Builder(this)
        alertbox.setTitle("Error")
        alertbox.setIcon(R.mipmap.icon)

        addressList = mutableListOf()

        val progressDialog = ProgressDialog(this)

        usersRef = FirebaseDatabase.getInstance().getReference("Address")

        alertbox.setNegativeButton("Close"){dialog, which ->
            dialog.dismiss()
        }

            val addressType = findViewById<EditText>(R.id.addressType)
            val addressLine = findViewById<EditText>(R.id.addressline)
            val addressLine2 = findViewById<EditText>(R.id.addressline2)
            val city = findViewById<EditText>(R.id.city)
            val state = findViewById<EditText>(R.id.state)
            val postcode = findViewById<EditText>(R.id.postcode)
            var addressId = ref.push().key.toString()
            val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid


            val mapAddress = Address(
                addressId,
                addressType.text.toString(),
                addressLine.text.toString(),
                addressLine2.text.toString(),
                city.text.toString(),
                state.text.toString(),
                postcode.text.toString(),
                currentUserID
            )

            ref.child(addressId).setValue(mapAddress).addOnCompleteListener{task ->
                if(task.isSuccessful){
                    Toast.makeText(applicationContext,"Add Successful!!!",Toast.LENGTH_LONG).show()
                    startActivity(Intent(applicationContext, LoadAddress::class.java))
                }else{
                    Toast.makeText(applicationContext,"Add Fail...",Toast.LENGTH_LONG).show()
                }
            }
        }

    private fun showDialog(){
        epicDialog.setContentView(R.layout.okalertbox)
        //val closeButton : ImageView = epicDialog.findViewById(R.id.closeBtn)
        val okButton : Button = epicDialog.findViewById(R.id.ok)
        val content : TextView = epicDialog.findViewById(R.id.txt1)

        content.text = "Add Successful"

        okButton.setOnClickListener {
            epicDialog.dismiss()
            finish()
        }
        epicDialog.setCancelable(true)
        epicDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        epicDialog.show()
    }
}