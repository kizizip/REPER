package com.ssafy.reper.ui.boss

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.reper.R
import com.ssafy.reper.base.ApplicationClass
import com.ssafy.reper.data.local.SharedPreferencesUtil
import com.ssafy.reper.databinding.FragmentRecipeManageBinding
import com.ssafy.reper.ui.FcmViewModel
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.ui.boss.adpater.RecipeAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    private val fcmViewModel: FcmViewModel by activityViewModels()

    val sharedPreferencesUtil: SharedPreferencesUtil by lazy {
        SharedPreferencesUtil(requireContext().applicationContext)
    }

    var sharedStoreId = 0
    var pdfName = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mainActivity = context
        }
        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // 화면 회전 잠금
    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNavigation()
    }

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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
        sharedStoreId = sharedPreferencesUtil.getStoreId()
        Log.d(TAG, "onViewCreated: 뷰모델은 이거입니다! ${bossViewModel.recipeLoad.value}")

           initUi()
           observerRecipe()


        binding.uploadBar.visibility = View.GONE

        binding.recipeFgAddTv.setOnClickListener {
            selectPdfFile()
        }

        initAdapter()

        binding.storeFgBackIcon.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        var searchJob: Job? = null

        binding.recipeSearchBarET.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(300)
                    val searchText = s.toString()

                    if (searchText.isEmpty()) {
                        bossViewModel.getMenuList(ApplicationClass.sharedPreferencesUtil.getStoreId())
                        return@launch
                    } else {
                        bossViewModel.searchRecipe(sharedStoreId, searchText)
                    }

                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })


        if (bossViewModel.recipeList.value == null || bossViewModel.recipeList.value!!.isEmpty()) {
            binding.recipeFgAddRV.visibility = View.GONE
            binding.nothingRecipe.visibility = View.VISIBLE

        } else {
            binding.recipeFgAddRV.visibility = View.VISIBLE
            binding.nothingRecipe.visibility = View.GONE
        }

    }

    fun initUi() {
        val result = bossViewModel.recipeLoad.value
        updateUI(result) // LiveData 값 기반으로 UI 초기 설정
        Log.d(TAG, "initUi: 원래저장된 값 ${result}")
    }

    fun observerRecipe() {
        bossViewModel.recipeLoad.observe(viewLifecycleOwner) { result ->
            Log.d(TAG, "onViewCreated: 받아오는 값 -> $result")
            updateUI(result) // 값이 변경될 때마다 UI 업데이트
        }
    }

    private fun updateUI(result: String?) {
        Log.d(TAG, "updateUI: ${result}")
        when (result) {
            null -> {
                binding.uploadBar.visibility = View.GONE
            }

            "success" -> {
                binding.uploadBar.visibility = View.VISIBLE
                if (bossViewModel.uploadNum > 0) {
                    binding.uploadState.text = "${bossViewModel.uploadNum}개의 레시피 업로드 성공!"
                } else {
                    binding.uploadState.text = "레시피 업로드 실패! 올바른 파일인지 확인해 주세요"
                }

                binding.successText.visibility = View.VISIBLE
                binding.fileName.text = bossViewModel.fileName
                binding.successText.text = "확인"
                binding.successText.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.mainorange
                    )
                )
                bossViewModel.getMenuList(sharedStoreId)
                binding.successText.setOnClickListener {
                    binding.uploadBar.visibility = View.GONE
                    bossViewModel.setRecipeLoad(null)
                }
            }

            "failure" -> {
                binding.uploadBar.visibility = View.VISIBLE
                binding.uploadState.text = "레시피 업로드 실패"
                binding.successText.visibility = View.VISIBLE
                binding.fileName.text = bossViewModel.fileName
                binding.successText.text = "확인"
                binding.successText.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.darkgray
                    )
                )
                binding.successText.setOnClickListener {
                    binding.uploadBar.visibility = View.GONE
                    bossViewModel.setRecipeLoad(null)
                }
            }

            "loading" -> {
                binding.uploadBar.visibility = View.VISIBLE
                binding.successText.visibility = View.GONE
                binding.fileName.text = bossViewModel.fileName
                binding.successText.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.darkgray
                    )
                )
            }
        }
    }


    private fun initAdapter() {
        bossViewModel.getMenuList(sharedStoreId)

        binding.recipeFgAddRV.layoutManager = LinearLayoutManager(requireContext())
        val recipeAdapter = RecipeAdapter(mutableListOf(), object : RecipeAdapter.ItemClickListener {
            override fun onItemClick(position: Int) {
                val selectedRecipe = bossViewModel.recipeList.value?.get(position)
                selectedRecipe?.let {
                    showDialog(it.recipeName, it.recipeId)
                }
            }
        })
        binding.recipeFgAddRV.adapter = recipeAdapter

        bossViewModel.recipeList.observe(viewLifecycleOwner) { recipes ->
            Log.d(TAG, "Recipe list updated: size=${recipes?.size}")
            if (recipes.isNullOrEmpty()) {
                binding.recipeFgAddRV.visibility = View.GONE
                binding.nothingRecipe.visibility = View.VISIBLE
            } else {
                binding.recipeFgAddRV.visibility = View.VISIBLE
                binding.nothingRecipe.visibility = View.GONE
                recipeAdapter.updateData(recipes) // 리스트를 역순으로 정렬
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showDialog(menuName: String, recipeId: Int) {
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
            bossViewModel.deleteRecipe(recipeId, sharedStoreId)
            Toast.makeText(requireContext(), "레시피 삭제 완료", Toast.LENGTH_SHORT).show()
            bossViewModel.getMenuList(sharedStoreId)
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

    fun uploadFile(uri: Uri) {

        val filePart = getFilePart(requireContext(), uri)
        filePart?.let {

            bossViewModel.uploadRecipe(sharedStoreId, it)
            binding.uploadBar.visibility = View.VISIBLE
            val contentDisposition = it.headers?.get("Content-Disposition")
            pdfName = contentDisposition?.substringAfter("filename=")?.replace("\"", "") ?: "알 수 없는 파일"
            binding.fileName.text = pdfName
            bossViewModel.setRecipeLoad("loading")
            bossViewModel.fileName = pdfName
        }


    }
}