package co.winds.nearByPartners.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.View
import android.widget.Toast
import co.winds.R
import co.winds.model.CategoryModel
import co.winds.restservices.ApiUtils
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.filter_dialog_frament.*


class FilterActivityDialog:AppCompatActivity(),SearchView.OnQueryTextListener {
    private var mAdapter: VendorCategoryAdapter? = null
    private val apiServices by lazy { ApiUtils.apiService }
    private lateinit var list: ArrayList<CategoryModel>
    private lateinit var mContext: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.filter_dialog_frament)
        mContext=this
        list= ArrayList()

        search_views.isFocusable = false;
        recy_select.layoutManager = LinearLayoutManager(mContext)
        try {
            setupSearchView()
        } catch (e: Exception) {
        }
        nearBy()
    }

    override fun onStart() {
        super.onStart()
    }

    @SuppressLint("CheckResult")
    private fun nearBy() {
        apiServices.getCategoryList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    list = result.arrayListModel as ArrayList<CategoryModel>
                    Toast.makeText(mContext, result.message, Toast.LENGTH_SHORT).show()
                    if(list.isEmpty()){
                        noDataShow()
                    }else {
                        noDataHide()
                        mAdapter = VendorCategoryAdapter(list)
                        recy_select.adapter = mAdapter
                        Log.d("Tags", " $list")
                    }
                },
                { error ->
                    noDataShow()
                    Log.d("TAHS", "ERROR ${error.message}")
                }
            )

    }

    private fun noDataShow(){
        tv_nodata.visibility=View.VISIBLE
    }
    private fun noDataHide(){
        tv_nodata.visibility=View.GONE
    }

    /*Search For Listing  Start*/
    private fun setupSearchView() {
        search_views.setIconifiedByDefault(false)
        search_views.setOnQueryTextListener(this)
        search_views.isSubmitButtonEnabled = true
        search_views.queryHint = "Search Here";
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

    private fun filter(numbers: List<CategoryModel>, query: String): List<CategoryModel> {
        var query = query
        query = query.toLowerCase()

        val filteredCompanyList = ArrayList<CategoryModel>()
        for (client in numbers) {
            val textOne = client.name!!.toLowerCase()
            //   if (textOne.contains(query) || textTen.contains(query) || textHundred.contains(query)) {
            if (textOne.contains(query)) {
                filteredCompanyList.add(client)
            }
        }
        return filteredCompanyList
    }
    /*Search For listing END*/


    fun btn_apply(v: View){

       var d=""
        val mylist: ArrayList<HashMap<String, String>> = ArrayList()
        var map: HashMap<String, String>? = null
        for (i in 0 until list.size) {
            val m: CategoryModel = list[i]
            if (m.isSelected) {
                d= m.name!!
                map = HashMap()
                map.put("id", m.id.toString())
                map.put("name", m.name!!)
                mylist.add(map)
            }

        }
        val data = Gson().toJson(mylist)
        Log.d("TAGS", data)

        val intent = Intent()
        intent.putExtra("keyName", d)
        setResult(RESULT_OK, intent)
        finish()
    }

    fun btn_cancel(v: View){
        finish()
    }

}