package com.ssafy.reper.ui.boss.adpater

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.reper.data.dto.SearchedStore
import com.ssafy.reper.data.dto.Store
import com.ssafy.reper.databinding.ItemEditMyStoreRvBinding

private const val TAG = "StoreAdapter_싸피"

class StoreAdapter (var storeList: List<SearchedStore>, val itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<StoreAdapter.StoreViewHolder>() {

    // ViewHolder
    inner class StoreViewHolder(
        val binding: ItemEditMyStoreRvBinding,
        val itemClickListener: ItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            // 아이템 클릭 리스너 설정
            binding.delete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val binding = ItemEditMyStoreRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoreViewHolder(binding, itemClickListener)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        holder.binding.itemStoreName.text = storeList[position].storeName
    }

    override fun getItemCount(): Int {
        return storeList.size
    }

    // 클릭 리스너 인터페이스
    interface ItemClickListener {
        fun onClick(position: Int)
    }

    fun updateData(newList: List<SearchedStore>) {
        storeList = newList  // 그냥 새로운 리스트를 할당
        Log.d(TAG, "storeUpdateData: $storeList")
        notifyDataSetChanged() // 데이터 변경 시 UI 갱신
    }

}