package com.example.lenovo.emptypro.Fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.example.lenovo.emptypro.Activities.MainActivity
import com.example.lenovo.emptypro.Listeners.OnFragmentInteractionListener
import com.example.lenovo.emptypro.ModelClasses.AllApiResponse
import com.example.lenovo.emptypro.R
import com.example.lenovo.emptypro.Utilities.Utilities
import com.example.lenovo.emptypro.Utils.GlobalData
import com.facebook.internal.Utility
import com.iww.classifiedolx.recyclerview.setUp
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_sub_cat.*
import kotlinx.android.synthetic.main.item_sub_category_lst.view.*
import javax.inject.Inject


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
 private const val ARG_PARAM2 = "param2"

class SubCatFrag : Fragment(), View.OnClickListener {
    override fun onClick(v: View?) {
        when(v!!.id)
        {
            R.id.tv_chooseOne ->
            {
                if (subCatItemData != null) {
                    showSubCatDialog()
                } else {
                    utility.snackBar(tv_chooseOne, "check internet or try again")
                }
//                ll_buttons.visibility=View.GONE
  //              rv_subCatLst.visibility=View.VISIBLE
            }
            R.id.tv_showAll->
            {
                try {
                    utility.enterNextReplaceFragment(
                            R.id.fl_subCatFrag,
                            SubCateBaseAdd.newInstance(""+mainCatId,"all"),
                            (ctx as MainActivity).supportFragmentManager
                    )
                    Log.e(TAG + "choosedSubCatID", "all" )


                } catch (exp: Exception) {

                }

            }

        }

    }

    private fun showSubCatDialog() {
        val subcatList = arrayOfNulls<String>(subCatItemData!!.size)

        for (i in 0..(subCatItemData!!.size - 1)) {
            var subCatName: String? = subCatItemData!![i].subCategoryTitle
            subcatList [i] = "" + subCatName
        }
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("Choose an City")

        var tempChecked = 0
        builder.setSingleChoiceItems(subcatList , 0!!, DialogInterface.OnClickListener { dialog, which ->
            tempChecked = which
        })

        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
var            choosedSubCat= subCatItemData!![tempChecked!!].subCategoryTitle


            try {
                utility.enterNextReplaceFragment(
                        R.id.fl_subCatFrag,
                        SubCateBaseAdd.newInstance(""+mainCatId,""+ choosedSubCat),
                        (ctx as MainActivity).supportFragmentManager
                )
                Log.e(TAG + "choosedCityID", "" + choosedSubCat)


            } catch (exp: Exception) {

            }
        })
        builder.setNegativeButton("Cancel", null)
// create and show the alert dialog
        val dialog = builder.create()
        dialog.show()


    }

    // TODO: Rename and change types of parameters
  //  private var param1: String? = null
    private var listener: OnFragmentInteractionListener? = null
   // @Inject
 //   internal lateinit var apiService: ApiService
    private var mainCatId: String? = null
    var ctx: Context? = null
    var TAG="SubCatFrag "
    private var subCatItemData: MutableList<AllApiResponse.CategoryResponse.CategorySubListModel>? = null
var utility = Utilities()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
          //  param1 = it.getString(ARG_PARAM1)
            mainCatId= it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sub_cat, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    @SuppressLint("WrongConstant")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        subCatItemData = mutableListOf()
        rv_subCatLst.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(context)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_subCatLst.layoutManager = mLayoutManager

        rv_subCatLst.setUp(subCatItemData!!, R.layout.item_sub_category_lst, { it1 ->
            this.tv_subCatTitle.text = it1.subCategoryTitle

            this.ll_subCatItem.setOnClickListener {
                utility.enterNextReplaceFragment(
                        R.id.fl_subCatFrag,
                        SubCateBaseAdd.newInstance(""+mainCatId,""+it1.subCategoryTitle),
                        (ctx as MainActivity).supportFragmentManager
                )
            }
        }, { view1: View, i: Int -> })
         utility = Utilities()
        if(!utility .isConnected(ctx!!))
            utility .snackBar(rv_subCatLst, "Please check internet connection ")

        rv_subCatLst.adapter!!.notifyDataSetChanged()
        rv_subCatLst.layoutManager = LinearLayoutManager(context)

        swipe_refresh.setOnRefreshListener {
            if(!utility .isConnected(ctx!!))
                utility .snackBar(rv_subCatLst, "Please check internet connection ")
         //   fetchAllSubCat("get_subcategory")
        }
        if(subCatItemData!!.size==0)
        {
            utility .snackBar(rv_subCatLst, "No Sub Category Found")

        }

        tv_chooseOne.setOnClickListener(this)
        tv_showAll.setOnClickListener(this)

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
        fun newInstance(param1: MutableList<AllApiResponse.CategoryResponse.CategorySubListModel> , param2: String) =
                SubCatFrag().apply {
                    arguments = Bundle().apply {
                        subCatItemData=param1
//                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
