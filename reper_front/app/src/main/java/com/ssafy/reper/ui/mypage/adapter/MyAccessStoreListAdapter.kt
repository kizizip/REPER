import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.reper.data.dto.StoreResponseUser
import com.ssafy.reper.databinding.ItemEditMyAccountRvBinding

class MyAccessStoreListAdapter(
    private var accessStoreList: List<StoreResponseUser>, // 변경: MutableList -> List
    private val onDeleteClick: (StoreResponseUser, Int) -> Unit // 변경: 인터페이스 → 람다 함수
) : RecyclerView.Adapter<MyAccessStoreListAdapter.AccessStoreListHolder>() {

    inner class AccessStoreListHolder(private val binding: ItemEditMyAccountRvBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindInfo(store: StoreResponseUser) {
            store.name?.let { name ->
                binding.editmyaccountItemTvStoreName.text = name
                Log.d("MyAccessStoreListAdapter", "매장명 설정: $name")
            }

            binding.editmyaccountItemBtnDelte.setOnClickListener {
                onDeleteClick(store, adapterPosition) // 람다 함수 호출
            }
        }
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

    fun updateData(newList: List<StoreResponseUser>) {
        accessStoreList = newList // 변경: 직접 새로운 리스트 할당
        notifyDataSetChanged()
    }
}
