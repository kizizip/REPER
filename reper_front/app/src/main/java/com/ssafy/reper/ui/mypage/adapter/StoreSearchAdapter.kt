package com.ssafy.reper.ui.mypage.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.reper.R
import com.ssafy.reper.data.dto.Store
import androidx.core.content.res.ResourcesCompat
import com.ssafy.reper.data.dto.SearchedStore

class StoreSearchAdapter(private val storeList: List<SearchedStore>) :
    RecyclerView.Adapter<StoreSearchAdapter.StoreViewHolder>() {

    private var selectedPosition = -1
    private var onItemClickListener: ((SearchedStore) -> Unit)? = null

    fun setOnItemClickListener(listener: (SearchedStore) -> Unit) {
        onItemClickListener = listener
    }

    inner class StoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val storeName: TextView = itemView.findViewById(R.id.store_item_tv_name)

        fun bind(store: SearchedStore, position: Int) {
            storeName.text = store.storeName
            
            // 선택된 아이템의 텍스트 색상과 스타일 변경
            if (position == selectedPosition) {
                storeName.setTextColor(Color.parseColor("#1C4700"))
                storeName.typeface = ResourcesCompat.getFont(itemView.context, R.font.pretendard_bold)
                storeName.setTextSize(16f)
            } else {
                storeName.setTextColor(Color.BLACK)
                storeName.typeface = ResourcesCompat.getFont(itemView.context, R.font.pretendard_regular)
                storeName.setTextSize(14f)
            }

            itemView.setOnClickListener {
                val previousSelected = selectedPosition
                selectedPosition = position
                
                // 애니메이션 없이 즉시 변경되도록 notifyDataSetChanged 사용
                notifyDataSetChanged()
                
                onItemClickListener?.invoke(store)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_store_search, parent, false)
        return StoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        holder.bind(storeList[position], position)
    }

    override fun getItemCount(): Int = storeList.size

    // 선택된 가게 정보 반환
    fun getSelectedStore(): SearchedStore? {
        return if (selectedPosition != -1) storeList[selectedPosition] else null
    }
}