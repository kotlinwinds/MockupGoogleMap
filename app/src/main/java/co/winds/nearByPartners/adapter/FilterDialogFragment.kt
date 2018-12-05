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
import android.widget.Toast
import co.winds.R
import co.winds.model.NearbyVendor
import co.winds.restservices.ApiUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.filter_dialog_frament.view.*
import android.support.v4.content.ContextCompat.getSystemService
import android.view.inputmethod.InputMethodManager


class FilterDialogFragment : DialogFragment(), SearchView.OnQueryTextListener {
    private var myAdapter: MyFilterAdapter?=null
    private val apiServices by lazy { ApiUtils.apiService }
    private lateinit var list: ArrayList<NearbyVendor>
    private lateinit var recyFilter: RecyclerView
    private lateinit var search_view: SearchView
    private lateinit var mContext: Context
    private lateinit var mActivity:AppCompatActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext=(activity!! as AppCompatActivity)
        mActivity=(activity!! as AppCompatActivity)

       // setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
       // val mArgs = arguments!!
       // url= mArgs.getString("message")!!
    }

    @SuppressLint("CheckResult", "SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.setCanceledOnTouchOutside(false);
        val rootView = inflater.inflate(R.layout.filter_dialog_frament, container, false)
        recyFilter=rootView.recy_select

        rootView.button_apply.setOnClickListener {
            this.dismiss()
        }

        rootView.btn_cancel.setOnClickListener {
            this.dismiss()
        }


        search_view=rootView.search_views
        search_view.isFocusable = false;
        recyFilter.layoutManager = LinearLayoutManager(activity)
        nearBy()
        setupSearchView()
        return rootView
    }


    private fun setupSearchView() {
         search_view.setIconifiedByDefault(false)
         search_view.setOnQueryTextListener(this)
         search_view.isSubmitButtonEnabled = true
         search_view.queryHint = "Search Here";
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        search_view.clearFocus();
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        try {
            myAdapter!!.filter(newText!!);
        } catch (e: Exception) {
        }
        return true
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
                    recyFilter.adapter= MyFilterAdapter(mContext,list)

                    Log.d("Tags", " $list")

                },
                { error ->
                    Log.d("TAHS", "ERROR ${error.message}")
                }
            )

    }

}