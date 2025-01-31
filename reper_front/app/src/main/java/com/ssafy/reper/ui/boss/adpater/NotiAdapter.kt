package com.ssafy.reper.ui.boss.adpater

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.reper.databinding.ItemNotiBinding

class NotiAdapter  :
    RecyclerView.Adapter<NotiAdapter.NotiViewHolder>() {

    class NotiViewHolder(val binding: ItemNotiBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotiViewHolder {
        val binding = ItemNotiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotiViewHolder(binding)
    }


    override fun onBindViewHolder(holder: NotiViewHolder, position: Int) {
        holder.binding.itemNotiTitle.text = "😊멋쟁이 공지가 들어갈 자리입니다😊"
        holder.binding.itemNotiTime.text = "1분전"
    }


    override fun getItemCount(): Int {
        return 20
    }
}
