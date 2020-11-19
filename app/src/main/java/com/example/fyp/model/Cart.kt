package com.example.fyp.model

class Cart (val cartId: String,val userId: String,val cartQuantity: Int,val foodname: String,val price: Double,val remark : String, val image: String,val foodId : String){
    constructor():this("","",0,"",0.0,"","",""){

    }
}