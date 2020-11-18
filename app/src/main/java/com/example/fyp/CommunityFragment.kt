package com.example.fyp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fyp.adapter.CommunityAdapter
import com.example.fyp.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_community.*
import kotlinx.android.synthetic.main.fragment_community.view.*

class CommunityFragment : Fragment(){

    lateinit var ref : DatabaseReference
    lateinit var postlist : MutableList<Post>
    lateinit var query : Query
    var stop : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val root: View = inflater.inflate(R.layout.fragment_community, container, false)

        postlist = mutableListOf()

        addToList(root)

        return root
    }

    private fun addToList(root:View){
        ref = FirebaseDatabase.getInstance().getReference("Posts")

        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    postlist.clear()

                    for(h in snapshot.children){
                        val post =h.getValue(Post::class.java)
                        //val postId = h.child("postId").getValue()
                        postlist.add(post!!)
                    }

                    val preference = root.context.getSharedPreferences("post", Context.MODE_PRIVATE)
                    //val postId = preference.getString("PostID","")

                    val mLayoutManager = LinearLayoutManager(activity)
                    mLayoutManager.reverseLayout = true

                    root.reCommunity.layoutManager = mLayoutManager
                    root.reCommunity.scrollToPosition(postlist.size-1)
                    root.reCommunity.adapter =
                        CommunityAdapter(postlist)
                }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addCommunity.setOnClickListener {
            val intent =Intent(activity,AddCommunity::class.java)
            startActivity(intent)
        }


    }
}