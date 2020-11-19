package com.example.fyp.model

class Order (val orderId: String,val orderDateTime: String,val status: String,val subtotal : Double, val deliveryfee : Double,val totalAmount: Double, val paymentMethod : String, val userId : String){
    constructor():this("","","",0.0,0.0,0.0,"",""){

    }
}