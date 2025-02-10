package com.ssafy.reper.ui.boss

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager

import com.ssafy.reper.R
import com.ssafy.reper.databinding.FragmentRecipeManageBinding
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.ui.boss.adpater.RecipeAdapter

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream

private const val TAG = "RecipeManageFragment_싸피"
class RecipeManageFragment : Fragment() {
    private var _binding: FragmentRecipeManageBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity
    private val bossViewModel: BossViewModel by activityViewModels()
    var storeId = 1


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mainActivity = context
        }
    }

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            uri?.let {
                uploadFile(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecipeManageBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recipeFgAddTv.setOnClickListener {
            //안드로이드 파일업로드, 일단은 500에러, 진행률도 fcm에서 얼마나 되고 있는지 알려주면 좋을거 같은딩(나의 생각)
            selectPdfFile()
        }

        initAdapter()

        binding.storeFgBackIcon.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.recipeSearchBarET.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                val query = binding.recipeSearchBarET.text.toString().trim()
                if (query.isNotEmpty()) {
                    bossViewModel.searchRecipe(storeId,query)
                }
                true  // 이벤트 소비 (키보드 내림)
            } else {
                false // 기본 동작 수행
            }
        }

    }


    private fun initAdapter() {
        // MenuList를 불러오기 전에 어댑터 초기화
        bossViewModel.getMenuList(storeId)

        binding.recipeFgAddRV.layoutManager = LinearLayoutManager(requireContext())
        val recipeAdapter = RecipeAdapter(emptyList(), object : RecipeAdapter.ItemClickListener {
            override fun onItemClick(position: Int) {
                val selectedRecipe = bossViewModel.recipeList.value?.get(position)
                showDialog(selectedRecipe!!.recipeName, selectedRecipe!!.recipeId)
            }
        })
        binding.recipeFgAddRV.adapter = recipeAdapter

        bossViewModel.recipeList.observe(viewLifecycleOwner) {
            recipeAdapter.updateData(it)
            recipeAdapter.notifyDataSetChanged() // 🔹 데이터 갱신 후 어댑터 갱신
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showDialog(menuName: String, recipeId : Int,) {
        val dialog = Dialog(mainActivity)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_delete)

        // 텍스트를 변경하려는 TextView 찾기
        val textView = dialog.findViewById<TextView>(R.id.dialog_delete_bold_tv)

        // 텍스트 변경
        textView.text = "${menuName} 레시피"

        dialog.findViewById<View>(R.id.dialog_delete_cancle_btn).setOnClickListener {
            dialog.dismiss()
        }
        dialog.findViewById<View>(R.id.dialog_delete_delete_btn).setOnClickListener {
            bossViewModel.deleteRecipe(recipeId, storeId)
            Toast.makeText(requireContext(), "레시피 삭제 완료", Toast.LENGTH_SHORT).show()
            bossViewModel.getMenuList(storeId)
            dialog.dismiss()
        }
        dialog.show()
    }

//GPT가 짜준 함수입니당

    private fun selectPdfFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/pdf"  // PDF 파일만 선택
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        filePickerLauncher.launch(Intent.createChooser(intent, "Select PDF"))
    }

    private fun getFilePart(context: Context, uri: Uri): MultipartBody.Part? {
        val contentResolver = context.contentResolver
        val fileName = getFileName(context, uri) ?: return null
        val inputStream = contentResolver.openInputStream(uri) ?: return null
        val file = File(context.cacheDir, fileName)
        val outputStream = FileOutputStream(file)

        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()

        // MIME 타입을 파일 확장자에 맞게 설정
        val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"
        val requestFile = RequestBody.create(mimeType.toMediaTypeOrNull(), file)

        return MultipartBody.Part.createFormData("file", file.name, requestFile)
    }

    // 파일명 가져오기
    @SuppressLint("Range")
    private fun getFileName(context: Context, uri: Uri): String? {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
        return null
    }

    fun uploadFile(uri: Uri){

        val filePart = getFilePart(requireContext(), uri)
        filePart?.let {
            bossViewModel.uploadRecipe(storeId, it)
        }
    }


}