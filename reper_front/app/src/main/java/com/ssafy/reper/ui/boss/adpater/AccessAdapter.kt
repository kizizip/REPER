//package com.ssafy.reper.ui.boss.adpater
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.ssafy.reper.databinding.ItemEmployeeBinding
//
//class AccessAdapter(
//    private val employeeList: MutableList<Employee>,
//    val itemClickListener: ItemClickListener
//) : RecyclerView.Adapter<AccessAdapter.AccessViewHolder>() {
//
//    inner class AccessViewHolder(private val binding: ItemEmployeeBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bindInfo(position: Int) {
//            val item = employeeList[position]
//
//            if (item.access) {
//                binding.btnAccept.visibility = View.GONE
//                binding.btnReject.visibility = View.GONE
//            } else {
//                binding.iconDelete.visibility = View.GONE
//            }
//
//            binding.employeeItemName.text = item.name
//
//            binding.btnAccept.setOnClickListener {
//                //수락 버튼 클릭
//            }
//
//            binding.btnReject.setOnClickListener {
//                //거절 버튼 클릭
//            }
//
//            binding.iconDelete.setOnClickListener {
//                //삭제 버튼 클릭
//                itemClickListener.onClick(absoluteAdapterPosition)
//            }
//        }
//    }
//
//    fun interface ItemClickListener {
//        fun onClick(position: Int)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccessViewHolder {
//        val binding =
//            ItemEmployeeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return AccessViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: AccessViewHolder, position: Int) {
//        holder.bindInfo(position)
//    }
//
//    override fun getItemCount(): Int {
//        return employeeList.size
//    }
//}
