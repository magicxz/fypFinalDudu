package com.example.fyp.model

class orderDetail(val orderDetailId : String, val foodId : String,val orderId : String, val quantity : Int,val remark : String) {
    constructor():this("","","",0,"")
}