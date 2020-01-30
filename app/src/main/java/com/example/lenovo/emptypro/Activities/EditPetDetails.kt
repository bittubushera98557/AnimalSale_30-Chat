package com.example.lenovo.emptypro.Activities

import android.app.Activity
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.lenovo.emptypro.ApiCallClasses.RetrofitClasses.GetDataService
import com.example.lenovo.emptypro.ApiCallClasses.RetrofitClasses.RetrofitClientInstance
import com.example.lenovo.emptypro.ModelClasses.AllApiResponse
import com.example.lenovo.emptypro.R
import com.example.lenovo.emptypro.Utilities.Utilities
import com.example.lenovo.emptypro.Utils.GlobalData
import com.example.lenovo.emptypro.Utils.SharedPrefUtil
import com.glide.slider.library.SliderTypes.TextSliderView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_edit_pet_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditPetDetails : AppCompatActivity() {
    internal var oldPetId: String? = ""
    internal var oldChoosedMainCatName:String? = ""
     internal var service: GetDataService? =   RetrofitClientInstance.getRetrofitInstance().create(GetDataService::class.java)

    internal var TAG = "EditPetDetails "
    internal var utilities = Utilities()
    var petDetailModel: AllApiResponse.PetDetailRes.PetDetail? = null
    var cityArrayList: List<AllApiResponse.CityResponse.CityModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_pet_details)
     getOldIntentData()

        if(oldChoosedMainCatName!!.toLowerCase().equals("dog"))
        {
            txtInptLyt_PetName.visibility= View.VISIBLE
            txtInptLyt_Title.visibility= View.VISIBLE
            ll_Price.visibility= View.VISIBLE
            //   txtInptLyt_petType.visibility=View.VISIBLE
            txtInptLyt_breed.visibility= View.VISIBLE
            txtInptLyt_age.visibility= View.VISIBLE
            txtInptLyt_desc.visibility= View.VISIBLE
        }
        else
            if(oldChoosedMainCatName!!.toLowerCase().equals("cat"))
            {
                txtInptLyt_PetName.visibility= View.VISIBLE
                txtInptLyt_Title.visibility= View.VISIBLE
                //txtInptLyt_Price.visibility=View.VISIBLE
                ll_Price.visibility= View.VISIBLE
                //     txtInptLyt_petType.visibility=View.VISIBLE
                txtInptLyt_breed.visibility= View.VISIBLE
                //   txtInptLyt_age.visibility=View.VISIBLE
                txtInptLyt_desc.visibility= View.VISIBLE
            } else
                if(oldChoosedMainCatName!!.toLowerCase().equals("bird"))
                {
                    txtInptLyt_PetName.visibility= View.VISIBLE
                    txtInptLyt_Title.visibility= View.VISIBLE
                    ll_Price.visibility= View.VISIBLE
                    // txtInptLyt_petType.visibility=View.VISIBLE
                    //  txtInptLyt_breed.visibility=View.VISIBLE
                    //   txtInptLyt_age.visibility=View.VISIBLE
                    txtInptLyt_desc.visibility= View.VISIBLE
                } else
                    if(oldChoosedMainCatName!!.toLowerCase().equals("cow"))
                    {
                        txtInptLyt_PetName.visibility= View.VISIBLE
                        txtInptLyt_Title.visibility= View.VISIBLE
                        //txtInptLyt_Price.visibility=View.VISIBLE
                        ll_Price.visibility= View.VISIBLE
                        //  txtInptLyt_petType.visibility=View.VISIBLE
                        //   txtInptLyt_breed.visibility=View.VISIBLE
                        //  txtInptLyt_age.visibility=View.VISIBLE
                        txtInptLyt_desc.visibility= View.VISIBLE
                    } else
                        if(oldChoosedMainCatName!!.toLowerCase().equals("buffalo"))
                        {
                            txtInptLyt_PetName.visibility= View.VISIBLE
                            txtInptLyt_Title.visibility= View.VISIBLE
                            ll_Price.visibility= View.VISIBLE
                            //  txtInptLyt_petType.visibility=View.VISIBLE
                            txtInptLyt_breed.visibility= View.VISIBLE
                            txtInptLyt_age.visibility= View.VISIBLE
                            txtInptLyt_desc.visibility= View.VISIBLE
                        }
                        else
                            if(oldChoosedMainCatName!!.toLowerCase().equals("others"))
                            {
                                txtInptLyt_PetName.visibility= View.VISIBLE
                                txtInptLyt_Title.visibility= View.VISIBLE
                                ll_Price.visibility= View.VISIBLE
                                txtInptLyt_petType.visibility= View.VISIBLE
                                txtInptLyt_breed.visibility= View.VISIBLE
                                txtInptLyt_age.visibility= View.VISIBLE
                                txtInptLyt_desc.visibility= View.VISIBLE
                            }
        fetchCityListApi()
    }

    private fun getOldIntentData() {

        oldPetId= intent.extras!!.getString("oldPetId")
        oldChoosedMainCatName= intent.extras!!.getString("petMainCat")
        tv_mainCategory.text = "" + oldChoosedMainCatName
        getPetDetail()
    }
    private fun getPetDetail() {
        Log.e(TAG + " getPetDetail", "single-pet/?userID=" + SharedPrefUtil.getUserId(this@EditPetDetails) + "&petID=" + oldPetId)
        var call = service!!.getPetDetailsApi(""+ SharedPrefUtil.getUserId(this@EditPetDetails), "" + oldPetId)

        call.enqueue(object : Callback<AllApiResponse.PetDetailRes> {
            override fun onResponse(call: Call<AllApiResponse.PetDetailRes>, response: Response<AllApiResponse.PetDetailRes>) {
                Log.e("getPetDetail res", "" + Gson().toJson(response.body()))

                if (response.body()!!.status.equals("200") && response.body()!!.data.size > 0) {
                    petDetailModel = response.body()!!.data[0]
/* txtInptLyt_PetName.visibility= View.VISIBLE
            txtInptLyt_Title.visibility= View.VISIBLE
            ll_Price.visibility= View.VISIBLE
            //   txtInptLyt_petType.visibility=View.VISIBLE
            txtInptLyt_breed.visibility= View.VISIBLE
            txtInptLyt_age.visibility= View.VISIBLE
            txtInptLyt_desc.visibility= View.VISIBLE*/


                    tv_subCategory.text = "" + petDetailModel!!.subCategory

                    tv_citySet.text = "" + petDetailModel!!.userCity

                            et_state.setText( petDetailModel!!.userState)

                    et_Title.setText(petDetailModel!!.petTitle)
                    et_desc.setText( petDetailModel!!.petDescription)
                            et_PetName.setText( petDetailModel!!.petName)
                            et_Price.setText( petDetailModel!!.petPrice)
                                    et_petType.setText("")
                    et_breed.setText( petDetailModel!!.petBreed)
                    et_age.setText( petDetailModel!!.petAge)

                     Log.e(TAG + " getPetDetail", "size=" + response.body()!!.data.size)

                } else {
                    GlobalData.showSnackbar(this@EditPetDetails as Activity, response.body()!!.messageType)
                }
            }

            override fun onFailure(call: Call<AllApiResponse.PetDetailRes>, t: Throwable) {
                // progress_bar.setVisibility(View.GONE);
                Toast.makeText(this@EditPetDetails, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun fetchCityListApi() {
        var dialog: Dialog? = utilities.dialog(this@EditPetDetails)

        service!!.allCityListApi().enqueue(object : Callback<AllApiResponse.CityResponse> {
            override fun onResponse(
                    call: Call<AllApiResponse.CityResponse>,
                    response: Response<AllApiResponse.CityResponse>
            ) {
                dialog!!.cancel()
                Log.e(TAG + " CityList res", "" + Gson().toJson(response.body()))
                if (response.isSuccessful && (response.body()!!.status.equals("200"))) {

                    cityArrayList = response.body()!!.data

                } else {
                    //swipe_refresh.isRefreshing = false

                    utilities.snackBar(tv_mainCategory, "" + response.body()!!.message)
                }
            }

            override fun onFailure(call: Call<AllApiResponse.CityResponse>, t: Throwable) {
                t.printStackTrace()
                dialog!!.cancel()

            }
        })
    }

}
