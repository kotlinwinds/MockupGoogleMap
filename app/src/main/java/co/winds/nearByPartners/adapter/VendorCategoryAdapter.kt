package co.winds.nearByPartners.adapter

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.winds.R
import co.winds.model.NearbyVendor
import kotlinx.android.synthetic.main.row_item.view.*


class VendorCategoryAdapter(list : ArrayList<NearbyVendor>) : RecyclerView.Adapter<VendorCategoryAdapter.ViewHolder>(){

    private var list: ArrayList<NearbyVendor> = ArrayList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(list[position],list)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    fun animateTo(models: List<NearbyVendor>) {
        applyAndAnimateRemovals(models)
        applyAndAnimateAdditions(models)
        applyAndAnimateMovedItems(models)
    }

    private fun applyAndAnimateRemovals(newModels: List<NearbyVendor>) {
        for (i in list.indices.reversed()) {
            val model = list[i]
            if (!newModels.contains(model)) {
                removeItem(i)
            }
        }
    }

    private fun applyAndAnimateAdditions(newModels: List<NearbyVendor>) {
        var i = 0
        val count = newModels.size
        while (i < count) {
            val model = newModels[i]
            if (!list.contains(model)) {
                addItem(i, model)
            }
            i++
        }
    }

    private fun applyAndAnimateMovedItems(newModels: List<NearbyVendor>) {
        for (toPosition in newModels.indices.reversed()) {
            val model = newModels[toPosition]
            val fromPosition = list.indexOf(model)
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition)
            }
        }
    }

    fun removeItem(position: Int): NearbyVendor {
        val model = list.removeAt(position)
        notifyItemRemoved(position)
        return model
    }

    fun addItem(position: Int, model: NearbyVendor) {
        list.add(position, model)
        notifyItemInserted(position)
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        val model = list.removeAt(fromPosition)
        list.add(toPosition, model)
        notifyItemMoved(fromPosition, toPosition)
    }


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bindItems(model: NearbyVendor,li:ArrayList<NearbyVendor>){

            itemView.check.setOnCheckedChangeListener(null)
            itemView.check.isChecked =model.isSelected
            itemView.check.setOnCheckedChangeListener { _, isChecked ->
                li[adapterPosition].isSelected = isChecked
            }

            itemView.tv.text=model.category

        }

    }

}

