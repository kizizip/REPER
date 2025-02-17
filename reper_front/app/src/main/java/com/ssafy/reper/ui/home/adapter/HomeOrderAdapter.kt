package com.ssafy.reper.ui.order.adapter

import android.content.res.ColorStateList
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.reper.R
import com.ssafy.reper.data.dto.Order
import com.ssafy.reper.databinding.RvHomeOrderItemBinding
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


private const val TAG = "OrderAdapter_정언"
class HomeOrderAdatper(var orderList:MutableList<Order>, var recipeNameList:MutableList<String>, val itemClickListener:ItemClickListener) : RecyclerView.Adapter<HomeOrderAdatper.OrderListHolder>() {
    inner class OrderListHolder(private val binding: RvHomeOrderItemBinding) : RecyclerView.ViewHolder(binding.root){
        @RequiresApi(Build.VERSION_CODES.O)
        fun bindInfo(position: Int){
            try {
                val item = orderList[position]
                val recipeName =  recipeNameList[position]

                val orderDetailList = item.orderDetails
                val totalQuantity = orderDetailList.sumOf { it.quantity }

                if (!item.completed){
                    binding.rvHomeOrderTitle.text = if (totalQuantity == 1) {
                        "${recipeName} ${totalQuantity}잔"
                    } else {
                        "${recipeName} 외 ${totalQuantity - 1}잔"
                    }
                    binding.rvHomeOrderTime.text = getRelativeTime(item.orderDate)
                    binding.rvHomeOrderTakeout.let{
                        if(item.takeout){
                            it.setText("포장")
                            it.setTextColor(ContextCompat.getColor(binding.root.context, R.color.red))
                        }
                        else{
                            it.setText("매장")
                            it.setTextColor(ContextCompat.getColor(binding.root.context, R.color.green))
                        }
                    }
                }

                binding.root.setOnClickListener{
                    itemClickListener.onClick(item.orderId)
                }
            } catch (e: Exception) {
                Log.e(TAG, "bindInfo error: ${e.message}")
                Log.d(TAG, "bindInfo: ${orderList}")
                Log.d(TAG, "bindInfo: ${recipeNameList}")
            }
        }
    }

    //클릭 인터페이스 정의 사용하는 곳에서 만들어준다.
    fun  interface ItemClickListener {
        fun onClick(orderId: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderListHolder {
        return  OrderListHolder(RvHomeOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: OrderListHolder, position: Int) {
        holder.bindInfo(position)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    // 시간처리 -> 1분전... 1시간전... 2025-01-27...
    @RequiresApi(Build.VERSION_CODES.O)
    fun getRelativeTime(orderDate: String): String {
        // ISO 8601 형식 처리 (공백이 아니라 'T'로 구분된 문자열을 사용해야 함)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss") // 'T' 포함
        val dateTime = LocalDateTime.parse(orderDate, formatter)

        // 현재 시간
        val now = LocalDateTime.now()

        // 두 시간의 차이 계산
        val duration = Duration.between(dateTime, now)
        val seconds = duration.seconds
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            seconds < 5 -> "방금 전"
            seconds < 60 -> "${seconds}초 전"
            minutes < 60 -> "${minutes}분 전"
            hours < 24 -> "${hours}시간 전"
            days == 1L -> "어제"
            days < 7 -> "${days}일 전"
            else -> orderDate.substring(0, 10) // yyyy-MM-dd 형식으로 반환
        }
    }
}