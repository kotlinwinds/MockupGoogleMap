package co.winds.nearByPartners


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.Toast
import co.winds.R
import co.winds.nearByPartners.adapter.FilterActivityDialog
import co.winds.nearByPartners.adapter.MyAdapter
import co.winds.restservices.ApiUtils
import co.winds.utils.toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_listview.view.*

class FragmentB : Fragment() {
    private val apiServices by lazy { ApiUtils.apiService }
    private lateinit var recy:RecyclerView

    private lateinit var mContext: Context
    private lateinit var mActivity:AppCompatActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext=(activity!! as AppCompatActivity)
        mActivity=(activity!! as AppCompatActivity)
        setHasOptionsMenu(true)

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         val v= inflater.inflate(R.layout.fragment_listview, container, false)
         recy=v.recycler_view_list
         recy.layoutManager = LinearLayoutManager(activity)
        nearBy("")
        return v!!
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_settings_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                startActivityForResult(Intent(mActivity, FilterActivityDialog::class.java),3324)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            3324->when(resultCode) {
                Activity.RESULT_OK -> {
                    val returnString = data!!.getStringExtra("keyName")
                    mActivity.toast(returnString)
                    nearBy(returnString)
                }
                Activity.RESULT_CANCELED -> {
                }
            }
        }
    }


    @SuppressLint("CheckResult")
    private fun nearBy(str:String) {
        apiServices.getNearby(str)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    val data = result.data!!.nearbyVendor
                    recy.adapter= MyAdapter(data!! as List<*>)
                    Log.d("Tags", " $data")

                },
                { error ->
                    Log.d("TAHS", "ERROR ${error.message}")
                }
            )

    }




}