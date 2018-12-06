package co.winds.nearByPartners.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import co.winds.R
import co.winds.model.NearbyVendor
import kotlinx.android.synthetic.main.adapter_nearbyvendor_layout.view.*

class MyAdapter(private val list: List<*>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_nearbyvendor_layout, parent, false)
        return ViewHolder(view)
    }


    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(list[position] as NearbyVendor)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),View.OnClickListener {

        val context:Context=itemView.context

        @SuppressLint("SetTextI18n")
        fun bindItem(model: NearbyVendor) {
            itemView.setOnClickListener(this)
            itemView.tv1.text = model.businessName
            itemView.tv2.text = model.ownerName
            itemView.tv3.text = ""+model.category
            itemView.tv4.text =""+model.address
        }

        override fun onClick(v: View?) {
             Toast.makeText(context,""+adapterPosition,Toast.LENGTH_SHORT).show()
        }
    }
}

