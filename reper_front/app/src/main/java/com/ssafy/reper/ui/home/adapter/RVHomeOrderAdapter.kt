package com.ssafy.reper.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.reper.R
import com.ssafy.reper.data.local.HomeAnnouncementModel
import com.ssafy.reper.data.local.HomeBannerModel
import com.ssafy.reper.data.local.HomeLikeRecipeModel
import com.ssafy.reper.data.local.HomeOrderModel

class RVHomeOrderAdapter (val List: MutableList<HomeOrderModel>) : RecyclerView.Adapter<RVHomeOrderAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RVHomeOrderAdapter.ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.rv_home_order_item, parent, false)

        return ViewHolder(v)
    }


    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    var itemClick : ItemClick? = null


    override fun onBindViewHolder(holder: RVHomeOrderAdapter.ViewHolder, position: Int) {

        if (itemClick != null) {
            holder?.itemView?.setOnClickListener { v->
                itemClick!!.onClick(v, position)
            }
        }

        holder.bindItems(List[position])
    }

    override fun getItemCount(): Int {
        return List.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(item: HomeOrderModel) {
            val rv_order = itemView.findViewById<TextView>(R.id.rv_home_order_title)
            val rv_time = itemView.findViewById<TextView>(R.id.rv_home_order_time)

            rv_order.text = item.order
            rv_time.text = item.time

        }
    }

}