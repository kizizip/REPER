package com.ssafy.reper.ui.mypage

import android.view.LayoutInflater
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
        fun bindInfo(store: Store) {  // position 대신 Store 객체를 직접 받도록 수정
            // 매장명 설정
            binding.editmyaccountItemTvStoreName.text = store.storeName

            // 매장명 클릭 시 삭제 다이얼로그 표시
            binding.editmyaccountItemTvStoreName.setOnClickListener {
                itemClickListener.onStoreClick(store, layoutPosition)
            }

            // 삭제 버튼 클릭 시
            binding.editmyaccountItemBtnDelte.setOnClickListener {
                itemClickListener.onDeleteClick(store, layoutPosition)
            }
        }
    }

    // 클릭 인터페이스 수정
    interface ItemClickListener {
        fun onStoreClick(store: Store, position: Int)
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