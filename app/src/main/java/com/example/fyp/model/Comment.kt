package com.example.fyp.model

class Comment(var commentId : String, var postId : String, var userId : String, var commentContent : String, var date : String) {
    constructor():this("","","","",""){

    }
}