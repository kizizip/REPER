package com.ssafy.reper.ui.boss.adpater

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.reper.databinding.ItemNotiBinding
import com.ssafy.reper.ui.mypage.MyAccessStoreListAdapter.ItemClickListener

class NotiAdapter(val itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<NotiAdapter.NotiViewHolder>() {

    // ViewHolder
    inner class NotiViewHolder(
        val binding: ItemNotiBinding,
        val itemClickListener: ItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            // 아이템 클릭 리스너 설정
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotiViewHolder {
        val binding = ItemNotiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotiViewHolder(binding, itemClickListener)
    }

    override fun onBindViewHolder(holder: NotiViewHolder, position: Int) {
        // 여기서 실제 데이터 바인딩을 진행하면 됩니다.
        holder.binding.itemNotiTitle.text = "😊멋쟁이 공지가 들어갈 자리입니다😊"
        holder.binding.itemNotiTime.text = "1분전"
    }

    override fun getItemCount(): Int {
        return 20
    }

    // 클릭 리스너 인터페이스
    interface ItemClickListener {
        fun onClick(position: Int)
    }
}