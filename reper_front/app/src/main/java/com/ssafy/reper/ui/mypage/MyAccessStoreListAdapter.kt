package com.ssafy.reper.ui.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.reper.databinding.ItemEditMyAccountRvBinding

class MyAccessStoreListAdapter (var accessStoreList:MutableList<String>, val itemClickListener:ItemClickListener) : RecyclerView.Adapter<MyAccessStoreListAdapter.AccessStoreListHolder>() {
    inner class AccessStoreListHolder(private val binding: ItemEditMyAccountRvBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindInfo(position: Int){
            val item = accessStoreList[position]

            // 메뉴명 설정
            binding.editmyaccountItemTvStoreName.text = item

            binding.editmyaccountItemBtnDelte.setOnClickListener {
                itemClickListener.onClick(layoutPosition)
                accessStoreList.drop(layoutPosition)
            }
        }
    }

    //클릭 인터페이스 정의 사용하는 곳에서 만들어준다.
    fun  interface ItemClickListener {
        fun onClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccessStoreListHolder {
        return  AccessStoreListHolder (ItemEditMyAccountRvBinding.inflate(LayoutInflater.from(parent.context), parent,false))
    }

    override fun onBindViewHolder(holder: AccessStoreListHolder, position: Int) {
        holder.bindInfo(position)
    }

    override fun getItemCount(): Int {
        return accessStoreList.size
    }
}