package com.ssafy.reper.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.reper.R
import com.ssafy.reper.data.local.HomeBannerModel

class RVHomeBannerAdapter (val List: MutableList<HomeBannerModel>) : RecyclerView.Adapter<RVHomeBannerAdapter.ViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RVHomeBannerAdapter.ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.rv_homebanner_item, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RVHomeBannerAdapter.ViewHolder, position: Int) {

        holder.bindItems(List[position])
    }

    override fun getItemCount(): Int {
        return List.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(item: HomeBannerModel) {
            val rv_img = itemView.findViewById<ImageView>(R.id.rvHomeBannerImg)
            val rv_title = itemView.findViewById<TextView>(R.id.rvHomeBannerTitle)
            val rv_button = itemView.findViewById<Button>(R.id.rvHomeBannerButton)

            rv_img.setImageResource(item.imageUrl)
            rv_title.text = item.homeBannerTitle
            rv_button.text = item.homeBannerButtonText

            // 리소스 ID를 실제 색상으로 변환
            val color = ContextCompat.getColor(itemView.context, item.homeBannerButtonTextColor)
            rv_button.setTextColor(color)
        }
    }

}