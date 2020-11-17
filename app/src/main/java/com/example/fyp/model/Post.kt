package com.example.fyp.model

class Post(var postId : String, var content : String, var datetime : String, var postImage : String, var userId : String) {
    constructor():this("","","","","")
}