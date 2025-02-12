import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.reper.data.dto.Employee
import com.ssafy.reper.databinding.ItemEmployeeBinding

class AccessAdapter(
    val employeeList: MutableList<Employee>,
    val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<AccessAdapter.AccessViewHolder>() {

    inner class AccessViewHolder(private val binding: ItemEmployeeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindInfo(position: Int) {
            val item = employeeList[position]

            if (item.employed) {
                binding.btnAccept.visibility = View.GONE
                binding.btnReject.visibility = View.GONE
            } else {
                binding.iconDelete.visibility = View.GONE
            }

            binding.employeeItemName.text = item.userName

            binding.btnAccept.setOnClickListener {
                //수락 버튼 클릭
                 itemClickListener.onAcceptClick(absoluteAdapterPosition)

            }

            binding.btnReject.setOnClickListener {
                //거절 버튼 클릭
                itemClickListener.onDeleteClick(absoluteAdapterPosition)

            }

            binding.iconDelete.setOnClickListener {
                //삭제 버튼 클릭
                itemClickListener.onDeleteClick(absoluteAdapterPosition)
            }
        }
    }

     interface ItemClickListener {
        fun onDeleteClick(position: Int)
        fun onAcceptClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccessViewHolder {
        val binding =
            ItemEmployeeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AccessViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccessViewHolder, position: Int) {
        holder.bindInfo(position)
    }

    override fun getItemCount(): Int {
        return employeeList.size
    }

    // 리스트 갱신 함수
    fun updateList(newList: List<Employee>) {
        employeeList.clear()  // 기존 리스트 비우기
        employeeList.addAll(newList)  // 새로운 리스트 추가
        notifyDataSetChanged()  // 리사이클러뷰 갱신
    }
}
