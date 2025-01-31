package com.ssafy.reper.ui.boss.adpater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.reper.data.dto.Employee
import com.ssafy.reper.databinding.ItemEmployeeBinding

class AccessAdapter (private val items: List<Employee>) : RecyclerView.Adapter<AccessAdapter.AccessViewHolder>() {

    class AccessViewHolder(val binding: ItemEmployeeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccessViewHolder {
        val binding = ItemEmployeeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AccessViewHolder(binding)
    }


    override fun onBindViewHolder(holder: AccessViewHolder, position: Int) {
        if (items[position].access){
            holder.binding.btnAccept.visibility = View.GONE
            holder.binding.btnReject.visibility = View.GONE
        }else{
            holder.binding.iconDelete.visibility = View.GONE
        }

        holder.binding.employeeItemName.text = items[position].name
    }


    override fun getItemCount(): Int {
        return items.size
    }
}
