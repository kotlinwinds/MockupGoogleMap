package co.winds.nearByPartners.adapter

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import co.winds.R
import co.winds.model.NearbyVendor
import kotlinx.android.synthetic.main.row_item.view.*


class MyFilterAdapter(private val context: Context, val list: ArrayList<NearbyVendor>) : RecyclerView.Adapter<MyFilterAdapter.ViewHolder>() {

    private val filterList: MutableList<NearbyVendor>?

    init {
        this.filterList = ArrayList()
        this.filterList.addAll(this.list)
    }

   override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): ViewHolder {

        val v = LayoutInflater.from(p0.context).inflate(R.layout.row_item, p0, false)
        return ViewHolder(v)
    }

    override  fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindFun(filterList!![position])

        holder.check.setOnCheckedChangeListener(null);
        holder.check.isChecked = filterList[position].isSelected;

        holder.check.setOnCheckedChangeListener { _, isChecked ->
            filterList[holder.adapterPosition].isSelected = isChecked
        }

    }

    override fun getItemCount(): Int = filterList?.size ?:0


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var check: CheckBox = itemView.findViewById<View>(R.id.check) as CheckBox
        val context: Context = itemView.context
        fun bindFun(m: NearbyVendor) {
            itemView.tv.text = m.category
          //  Picasso.with(context).load(m.avatar).into(itemView.img);


        }
    }

    fun filter(text: String) {
        Thread(Runnable {
            filterList!!.clear()
            if (TextUtils.isEmpty(text)) {
                filterList.addAll(list)
            } else {
                for (item in list) {
                    if (item.category!!.toLowerCase().contains(text.toLowerCase())) {
                        if(filterList.isEmpty()){
                            (context as FilterDialogFragment).noDataShow()
                        }else (context as FilterDialogFragment).noDataHide()

                        filterList.add(item)
                    }
                }
            }

            (context as Activity).runOnUiThread {
                notifyDataSetChanged()
            }
        }).start()

    }

}