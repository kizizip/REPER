package com.ssafy.reper.ui.mypage

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.reper.data.dto.Store
import com.ssafy.reper.databinding.ItemEditMyAccountRvBinding

class MyAccessStoreListAdapter(
    var accessStoreList: MutableList<Store>,  // String 대신 Store 객체 리스트로 변경
    val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<MyAccessStoreListAdapter.AccessStoreListHolder>() {

    inner class AccessStoreListHolder(private val binding: ItemEditMyAccountRvBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindInfo(store: Store) {
            // 매장명이 null이 아닌지 확인하고 설정
            store.name?.let { name ->
                binding.editmyaccountItemTvStoreName.text = name
                // 디버그를 위한 로그 추가
                Log.d("MyAccessStoreListAdapter", "매장명 설정: $name")
            }

            // 삭제 버튼 클릭 리스너만 유지
            binding.editmyaccountItemBtnDelte.setOnClickListener {
                itemClickListener.onDeleteClick(store, layoutPosition)
            }

            // 뷰의 가시성 확인
            binding.editmyaccountItemTvStoreName.visibility = View.VISIBLE
        }
    }

    // 클릭 인터페이스에서 onStoreClick 제거
    interface ItemClickListener {
        fun onDeleteClick(store: Store, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccessStoreListHolder {
        return AccessStoreListHolder(
            ItemEditMyAccountRvBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AccessStoreListHolder, position: Int) {
        holder.bindInfo(accessStoreList[position])
    }

    override fun getItemCount(): Int = accessStoreList.size
}