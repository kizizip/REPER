package com.ssafy.reper.ui.order.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.reper.R
import com.ssafy.reper.data.local.HomeLikeRecipeModel
import com.ssafy.reper.data.local.OrderRecipeModel
import com.ssafy.reper.ui.home.adapter.RVHomeLikeRecipeAdapter

class RVOrderRecipeAdapter (val List: MutableList<OrderRecipeModel>) : RecyclerView.Adapter<RVOrderRecipeAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RVOrderRecipeAdapter.ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.rv_order_recipe_item, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RVOrderRecipeAdapter.ViewHolder, position: Int) {

        holder.bindItems(List[position])
    }

    override fun getItemCount(): Int {
        return List.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(item: OrderRecipeModel) {

            val rv_img = itemView.findViewById<ImageView>(R.id.rv_order_recipe_img)
            val rv_title = itemView.findViewById<TextView>(R.id.rv_order_recipe_title)
            val rv_quantity = itemView.findViewById<TextView>(R.id.rv_order_recipe_quantity)
            val rv_message = itemView.findViewById<TextView>(R.id.rv_order_recipe_message)


            rv_img.setImageResource(R.drawable.americano_hot)
            rv_title.text = item.title
            rv_quantity.text = item.quantity.toString()
            rv_message.text = item.message

        }
    }
}