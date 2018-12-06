package co.winds.nearByPartners.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import co.winds.R
import co.winds.model.NearbyVendor
import co.winds.restservices.ApiUtils
import co.winds.utils.toast
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.filter_dialog_frament.view.*


class FilterDialogFragment : DialogFragment(), SearchView.OnQueryTextListener {
    private var mAdapter: VendorCategoryAdapter? = null
    private val apiServices by lazy { ApiUtils.apiService }
    private lateinit var list: ArrayList<NearbyVendor>
    private lateinit var recyFilter: RecyclerView
    private lateinit var tv_nodata_: TextView
    private lateinit var search_view: SearchView
    private lateinit var mContext: Context
    private lateinit var mActivity:AppCompatActivity




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext=(activity!! as AppCompatActivity)
        mActivity=(activity!! as AppCompatActivity)
        list= ArrayList()
        nearBy()

       // setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
       // val mArgs = arguments!!
       // url= mArgs.getString("message")!!
    }

    @SuppressLint("CheckResult", "SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.setCanceledOnTouchOutside(false);
        val rootView = inflater.inflate(R.layout.filter_dialog_frament, container, false)
        recyFilter=rootView.recy_select
        tv_nodata_=rootView.tv_nodata

        rootView.button_apply.setOnClickListener {
            applyButton()
        }

        rootView.btn_cancel.setOnClickListener {
            this.dismiss()
        }


        search_view=rootView.search_views
        search_view.isFocusable = false;
        recyFilter.layoutManager = LinearLayoutManager(activity)
        try {
            setupSearchView()
        } catch (e: Exception) {
        }
        return rootView
    }

    @SuppressLint("CheckResult")
    private fun nearBy() {
        apiServices.getNearby()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    list = result.data!!.nearbyVendor!! as ArrayList<NearbyVendor>
                    Toast.makeText(activity, "${result.message}", Toast.LENGTH_SHORT).show()
                    if(list.isEmpty()){
                        noDataShow()
                    }else {
                        noDataHide()
                        mAdapter = VendorCategoryAdapter(list)
                        recyFilter.adapter = mAdapter
                        Log.d("Tags", " $list")
                    }
                },
                { error ->
                    noDataShow()
                    Log.d("TAHS", "ERROR ${error.message}")
                }
            )

    }



    fun noDataShow(){
        tv_nodata_.visibility=View.VISIBLE
    }
    fun noDataHide(){
        tv_nodata_.visibility=View.GONE
    }


  /*Search For Listing  Start*/
    private fun setupSearchView() {
        search_view.setIconifiedByDefault(false)
        search_view.setOnQueryTextListener(this)
        search_view.isSubmitButtonEnabled = true
        search_view.queryHint = "Search Here";
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val filteredModelList = filter(list, newText!!)
        try {
            mAdapter!!.animateTo(filteredModelList)
        } catch (e: Exception) {
        }
        if(filteredModelList.isEmpty()) noDataShow() else noDataHide()
        return true
    }

    private fun filter(numbers: List<NearbyVendor>, query: String): List<NearbyVendor> {
        var query = query
        query = query.toLowerCase()

        val filteredCompanyList = ArrayList<NearbyVendor>()
        for (client in numbers) {
            val textOne = client.category!!.toLowerCase()
            //   if (textOne.contains(query) || textTen.contains(query) || textHundred.contains(query)) {
            if (textOne.contains(query)) {
                filteredCompanyList.add(client)
            }
        }
        return filteredCompanyList
    }
  /*Search For listing END*/


   private fun applyButton(){
       val mylist: ArrayList<HashMap<String, String>> = ArrayList()
       var map: HashMap<String, String>? = null
       for (i in 0 until list.size) {
           val m: NearbyVendor = list[i]
           if (m.isSelected) {
               map = HashMap()
               map.put("id", m.userId.toString())
               map.put("name", m.businessName!!)
               map.put("categoty", m.category!!)
               mylist.add(map)
           }

       }
       val data = Gson().toJson(mylist)
       Log.d("TAGS", data)
       mContext.toast(data)

       this.dismiss()
   }


}