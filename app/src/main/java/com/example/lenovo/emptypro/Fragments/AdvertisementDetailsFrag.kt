package com.example.lenovo.emptypro.Fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.lenovo.emptypro.Activities.P2PChatView
import com.example.lenovo.emptypro.ApiCallClasses.RetrofitClasses.GetDataService
import com.example.lenovo.emptypro.ApiCallClasses.RetrofitClasses.RetrofitClientInstance

import com.example.lenovo.emptypro.Listeners.OnFragmentInteractionListener
import com.example.lenovo.emptypro.ModelClasses.AllApiResponse
import com.example.lenovo.emptypro.R
import com.example.lenovo.emptypro.Utilities.Utilities
import com.example.lenovo.emptypro.Utils.GlobalData
import com.example.lenovo.emptypro.Utils.SharedPrefUtil
import com.glide.slider.library.SliderTypes.TextSliderView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_advertisement_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdvertisementDetailsFrag : Fragment(), View.OnClickListener, OnMapReadyCallback {
    override fun onMapReady(googleMap: GoogleMap?) {

        googleMap!!.uiSettings.isZoomControlsEnabled = false
        googleMap.uiSettings.isMyLocationButtonEnabled = false
        gMap=googleMap
    }

    override fun onClick(v: View?) {
when(v!!.id)
{
    R.id.tv_bottomChat ->
    {
        var intentMain = Intent((ctx!! as Activity), P2PChatView::class.java)
        //    intentMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        intentMain.putExtra("oldPetId", petId)
        startActivity(intentMain)

    }
}
    }

    // TODO: Rename and change types of parameters
    private var petId: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    var petDetailModel: AllApiResponse.PetDetailRes.PetDetail? = null
    var ctx: Context? = null
    var lati="0"
    var utilities:Utilities?=Utilities()
    var longi="0"
     internal var service: GetDataService? = null
     var gMap:GoogleMap?=null
    var TAG="AdvertisementDetailsFrag "
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            petId= it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_call.setOnClickListener(this)
        tv_bottomChat.setOnClickListener(this)
        fl_AdvertisementDetailsFrag.setOnClickListener(this)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService::class.java)
        utilities= Utilities()
 getPetDetail()

    }

    private fun getPetDetail() {
        Log.e(TAG + " getPetDetail", "single-pet/?userID="+SharedPrefUtil.getUserId(context)+"&petID="+petId )
        val call = service!!.getPetDetailsApi(SharedPrefUtil.getUserId(context),""+petId)

        call.enqueue(object : Callback<AllApiResponse.PetDetailRes> {
            override fun onResponse(call: Call<AllApiResponse.PetDetailRes>, response: Response<AllApiResponse.PetDetailRes>) {
                Log.e("testAddPetApi res", "" + Gson().toJson(response.body()))

                if(response.body()!!.status.equals("200") && response.body()!!.data.size>0) {

                    petDetailModel = response.body()!!.data[0]
                    for (item in petDetailModel!!.images) {
                        val sliderView = TextSliderView(context)
                        sliderView.image(item.img).setProgressBarVisible(true)
                        slider.addSlider(sliderView)
                    }

                    if (petDetailModel!!.images.size == 0) {

                    }
                    tv_advertiseLoc.text = "" + petDetailModel!!.userCity + ", " + petDetailModel!!.userState
                    tv_advertiseDate.text = "" + GlobalData.dateSplit(petDetailModel!!.createdOn)
                    tv_petTitle.text = petDetailModel!!.petTitle
                    tv_advertiseDesc.text = petDetailModel!!.petDescription
                    tv_advertisePrice.text = petDetailModel!!.petPrice
                    tv_owmerName.text = petDetailModel!!.firstName + " " + petDetailModel!!.lastName
                    tv_adsId.text = "PetID: " + petDetailModel!!.petID

                    iv_favImg.visibility = View.VISIBLE
                    if (petDetailModel!!.favourite.equals("yes"))
                        iv_favImg.setImageResource(R.drawable.ic_favorite_filled)

                    if (petDetailModel!!.favourite.equals("no"))
                        iv_favImg.setImageResource(R.drawable.ic_favorite_empty)

                    iv_favImg.setOnClickListener {

                        if (petDetailModel!!.favourite.equals("yes")) {
                            if (utilities!!.isConnected(ctx!!))
                                callChangeFavApi("remove", petDetailModel!!)
                            else
                                utilities!!.snackBar(tv_advertiseLoc, "Please check internet ")
                        } else {
                            if (utilities!!.isConnected(ctx!!))
                                callChangeFavApi("add", petDetailModel!!)
                            else
                                utilities!!.snackBar(tv_advertiseLoc, "Please check internet ")

                        }
                    }
                    ll_bottomChatCall.visibility=View.VISIBLE

                    if (petDetailModel!!.ownerID.toString().equals(""+SharedPrefUtil.getUserId(context))) {
                    ll_bottomChatCall.visibility=View.GONE
                    }

                }             else{
                    GlobalData.showSnackbar(context as Activity, response.body()!!.messageType)

                }
            }

            override fun onFailure(call: Call<AllApiResponse.PetDetailRes>, t: Throwable) {
                // progress_bar.setVisibility(View.GONE);
                Toast.makeText(context, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun callChangeFavApi(s: String, petDetailModel: AllApiResponse.PetDetailRes.PetDetail) {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_advertisement_details, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
       ctx=context
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(petOldId: String, param2: String) =
                AdvertisementDetailsFrag().apply {
                    arguments = Bundle().apply {
                    //    petDetailModel=param1
                        putString(ARG_PARAM1, petOldId)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
