package com.example.fyp

import com.example.fyp.model.Order
import com.example.fyp.model.Post
import com.example.fyp.model.Users

class CountOrder{

    companion object{
        var number:Int = 0
        var total:Int = 0
        var commentCount : Int = 0

        var getUser : Users = Users()
        var getPost : Post = Post()
        var getOrder : Order = Order()

        fun get():Int{
            return CountOrder.number
        }

        fun get1():Int{
            return CountOrder.total
        }

        fun get2():Int{
            return CountOrder.commentCount
        }
    }
}