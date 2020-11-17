package com.example.fyp.model

class Address(var addressId : String, var addressType : String, var addressLine : String, var addressLine2 : String, var city : String, var state : String, var postcode : String, var userId : String) {
    constructor():this("","","","","","","",""){

    }
}