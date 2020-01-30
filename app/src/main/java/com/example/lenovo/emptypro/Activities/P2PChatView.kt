package com.example.lenovo.emptypro.Activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.lenovo.emptypro.ApiCallClasses.RetrofitClasses.GetDataService
import com.example.lenovo.emptypro.ApiCallClasses.RetrofitClasses.RetrofitClientInstance
import com.example.lenovo.emptypro.ModelClasses.AllApiResponse
import com.example.lenovo.emptypro.R
import com.example.lenovo.emptypro.Utilities.Utilities
import com.example.lenovo.emptypro.Utils.GlobalData
import com.example.lenovo.emptypro.Utils.SharedPrefUtil
import com.google.firebase.database.*
import com.google.gson.Gson
import com.iww.classifiedolx.recyclerview.setUp
import kotlinx.android.synthetic.main.activity_p2_pchat_view.*
import kotlinx.android.synthetic.main.header.*
import kotlinx.android.synthetic.main.item_chat.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class P2PChatView : AppCompatActivity(), View.OnClickListener {
    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.img_back -> {
                onBackPressed()
            }
            R.id.fl_sendMsg -> {
                if (et_chatMsg.text.toString().trim() != "")
                    addChatMessage()
                else
                    et_chatMsg.setError("Enter message")
            }
        }
    }

    private fun addChatMessage() {

        var chatRef: DatabaseReference? = null
        chatRef = dbReference.child("/AllChat").child("" + petBaseChatId)

        if (petBaseChatId == "") {
            chatRef = dbReference.child("/AllChat")
            petBaseChatId = "" + chatRef.push().key

            chatRef = FirebaseDatabase.getInstance().reference.child("/AllChat").child("" + petBaseChatId)
            chatRef!!.child("petName").setValue("")
            chatRef!!.child("petId").setValue("" + petId)
            chatRef!!.child("petImage").setValue("")
            chatRef!!.child("ownerId").setValue(ownerId)
            chatRef!!.child("intersterId").setValue("" + SharedPrefUtil.getUserId(this@P2PChatView))

        }
        val msgId = chatRef.push().key

        chatRef!!.child("/Chat").child("" + msgId).child("sender").setValue("" + SharedPrefUtil.getUserId(this@P2PChatView))
        chatRef.child("/Chat").child("" + msgId).child("reciever").setValue("" + ownerId)
        chatRef.child("/Chat").child("" + msgId).child("time").setValue(ServerValue.TIMESTAMP)
        chatRef.child("/Chat").child("" + msgId).child("message").setValue("" + et_chatMsg.text.toString())
        et_chatMsg.setText("")
    }

    internal var service: GetDataService? = null
    var TAG = "P2PChatView "
    internal var utilities = Utilities()
    var petId = ""
    var petImg = ""
    var ownerId = ""
    var petBaseChatId = ""
    var strPetName = ""
    var dbReference = FirebaseDatabase.getInstance().reference
    private var firebaseChatData: MutableList<AllApiResponse.FirebaseOldChatModel>? = null

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_p2_pchat_view)
        fl_sendMsg.setOnClickListener(this)
        service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService::class.java)
        img_back.setOnClickListener(this)
        firebaseChatData=mutableListOf()
        rv_recievedChat.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(this@P2PChatView)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_recievedChat.layoutManager = mLayoutManager
        mLayoutManager.stackFromEnd = true

        rv_recievedChat.setUp(firebaseChatData!!, R.layout.item_chat, { it1 ->


            this.tv_recievedTm.setText(it1.strTime)
            this.tv_recievedMsg.setText(it1.msg)
            this.tv_sentTm.setText(it1.strTime.toString())
            this.tv_sentMsg.setText(it1.msg)


            this.ll_sentMsg.setVisibility(View.GONE)
            this.ll_recieveMsg.setVisibility(View.GONE)

            if (SharedPrefUtil.getUserId(this@P2PChatView).equals(it1.senderId)) {
                ll_sentMsg.setVisibility(View.VISIBLE)
            } else {
                ll_recieveMsg.setVisibility(View.VISIBLE)
            }
        }, { view1: View, i: Int -> })

        try {
            getOldIntentData()
        } catch (exp: Exception) {
            Log.e(TAG + " catch", "excep=" + exp)

        }
    }

    private fun getOldIntentData() {

        petId = intent.extras!!.getString("oldPetId")
        if (!petId.equals("")) {
            Log.e(TAG + " oldIntent", "petID=")
            getPetDetail()
        }
    }


    private fun getPetDetail() {
        var dialog = utilities.dialog(this@P2PChatView)
        Log.e(TAG + " getPetDetail", "single-pet/?userID=" + SharedPrefUtil.getUserId(this@P2PChatView) + "&petID=" + petId)
        val call = service!!.getPetDetailsApi("" + SharedPrefUtil.getUserId(this@P2PChatView), "" + petId)

        call.enqueue(object : Callback<AllApiResponse.PetDetailRes> {
            override fun onResponse(call: Call<AllApiResponse.PetDetailRes>, response: Response<AllApiResponse.PetDetailRes>) {
                Log.e(TAG + " getPetDetail", "" + Gson().toJson(response.body()))
                dialog.cancel()
                if (response.body()!!.status.equals("200") && response.body()!!.data.size > 0) {
                    Log.e(TAG + " getPetDetail", "size=" + response.body()!!.data.size)
                    ownerId = "" + response.body()!!.data[0].ownerID
                    strPetName = "" + response.body()!!.data[0].petName
                    if (response.body()!!.data[0].images.size > 0) {
                        petImg = response.body()!!.data[0].images[0].img
                    }
                    tv_date.text = "" + response.body()!!.data[0].createdOn
                    tv_petLocation.text = "" + response.body()!!.data[0].userCity + ", " + response.body()!!.data[0].userState
                    getOldChat(ownerId, SharedPrefUtil.getUserId(this@P2PChatView), petId)
                    tv_petName.text = "" + response.body()!!.data[0].petName
                    tv_petTitle.text = "" + response.body()!!.data[0].petTitle + " " + response.body()!!.data[0].subCategory
                } else {
                    GlobalData.showSnackbar(this@P2PChatView as Activity, response.body()!!.messageType)
                }
            }


            override fun onFailure(call: Call<AllApiResponse.PetDetailRes>, t: Throwable) {
                dialog.cancel()
                 Toast.makeText(this@P2PChatView, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun getOldChat(strOwnerId: String, strIntersterId: String, strPetId: String) {
        Log.e(TAG + " ", "function searching  getOldChat()")

        var allUsersListener: ValueEventListener? = null
        var loggeInRef = dbReference.child("/AllChat")


        allUsersListener = object : ValueEventListener {
            @SuppressLint("WrongConstant")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.e(TAG + " ", "function Calling  getOldChat()")

                val children = dataSnapshot!!.children

                var txtTest = "searching data"
                children.forEach {

                    //    var strImg= it.child("profileImg").value.toString()
                    var strDB_OwnerId = it.child("ownerId").value.toString()
                    var strDB_PetId = it.child("petId").value.toString()
                    var strDB_IntersterId = it.child("intersterId").value.toString()
                    Log.e(
                            TAG + " getOldChat",
                            "  ownerId=" + it.child("ownerId").value.toString() + "    petId=" + it.child("petId").value.toString() + "   " + "intersterId=" + it.child("intersterId").value.toString()
                    )


                    if (strDB_OwnerId == strOwnerId && strDB_IntersterId == strIntersterId && strDB_PetId == strPetId) {
                        petBaseChatId = "" + it.key

                        val chatDataList= it!!.child("Chat").children
                        chatDataList.forEach {itCHild->
var firebaseChatItem= AllApiResponse.FirebaseOldChatModel()
                            Log.e(TAG, "chatMsg  :  "+itCHild.child("message").value )
firebaseChatItem.recieverId=""+itCHild.child("reciever").value
                            firebaseChatItem.msg=""+itCHild.child("message").value
                            firebaseChatItem.senderId=""+itCHild.child("sender").value
                            firebaseChatItem.strTime=""+itCHild.child("time").value
                            firebaseChatData!!.add(firebaseChatItem)
                        }
                        setChatRecyleData()

                             Log.e(TAG, "getOldChat  :  userLoginMsg")


                        txtTest = "matched data "
                        return

                    }
                }

                Toast.makeText(applicationContext, "" + txtTest, Toast.LENGTH_LONG).show()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.e(TAG, "getOldChat  :onCancelled", databaseError.toException())

            }
        }
        loggeInRef.addValueEventListener(allUsersListener!!)


    }

    private fun setChatRecyleData() {
        rv_recievedChat.adapter!!.notifyDataSetChanged()
if(firebaseChatData!!.size>0)
{
    tv_dummyTxt.visibility=View.GONE
}

    }


}
