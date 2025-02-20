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
import com.ssafy.reper.data.local.HomeBannerModel

class RVHomeBannerAdapter(
    private val bannerItems: List<HomeBannerModel>,
    private val onBannerClick: (HomeBannerModel) -> Unit  // 🔹 클릭 리스너 추가
) : RecyclerView.Adapter<RVHomeBannerAdapter.ViewHolder>() {

    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (bannerItems.isNotEmpty()) {
            val actualPosition = position % bannerItems.size
            holder.bind(bannerItems[actualPosition])
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RVHomeBannerAdapter.ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.rv_homebanner_item, parent, false)
        return ViewHolder(v)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: HomeBannerModel) {
            val rv_img = itemView.findViewById<ImageView>(R.id.rvHomeBannerImg)
            val rv_title = itemView.findViewById<TextView>(R.id.rvHomeBannerTitle)
            val rv_button = itemView.findViewById<Button>(R.id.rvHomeBannerButton)

            rv_img.setImageResource(item.imageUrl)
            rv_title.text = item.homeBannerTitle
            rv_button.text = item.homeBannerButtonText

            // 리소스 ID를 실제 색상으로 변환
            val color = ContextCompat.getColor(itemView.context, item.homeBannerButtonTextColor)
            rv_button.setTextColor(color)

            // 🔹 배너 전체 클릭 이벤트 추가
            itemView.setOnClickListener {
                onBannerClick.invoke(item)  // 클릭된 배너 데이터 전달
            }

            // 🔹 버튼 클릭 이벤트 추가 (필요하면 따로 처리 가능)
            rv_button.setOnClickListener {
                onBannerClick.invoke(item)  // 버튼도 같은 이벤트 전달
            }
        }
    }
}
