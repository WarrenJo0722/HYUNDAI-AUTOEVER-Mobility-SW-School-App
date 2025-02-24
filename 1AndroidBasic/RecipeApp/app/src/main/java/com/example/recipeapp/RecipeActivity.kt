package com.example.recipeapp

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecipeActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeStepEditAdapter
    private var recipeItemList = mutableListOf("")
    private var imageUri: Uri? = null

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGallery() // 권한 허용 시 이미지 선택 실행
            } else {
                Toast.makeText(this, "앨범 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

    // ActivityResultContracts.StartActivityForResult 객체 등록
    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // 파일을 선택했을 때
            val uri: Uri? = result.data?.data  // 선택한 파일의 URI를 가져옴
            uri?.let {
                imageUri = uri
                println("imageUri: $imageUri")
                displayImage(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recipe)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime()) // 키보드 높이 가져오기

            val bottomInset = if (insets.isVisible(WindowInsetsCompat.Type.ime())) {
                imeInsets.bottom // 키보드가 올라오면 키보드 높이만큼 패딩 설정
            } else {
                systemBars.bottom // 키보드가 없으면 기본 시스템 바 패딩 유지
            }

            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomInset)
            insets
        }

        // SharedPreferencesHelper 인스턴스 초기화
        sharedPreferencesHelper = SharedPreferencesHelper(this)

        imageView = findViewById(R.id.imageView)

        // 이미지 선택 버튼 클릭 시 앨범에서 이미지 선택
        imageView.setOnClickListener {
            checkPermissionAndPickImage()
        }

        val editTextRecipe = findViewById<EditText>(R.id.editTextRecipe)
        val editTextDescription = findViewById<EditText>(R.id.editTextDescription)

        val textViewTitle = findViewById<TextView>(R.id.textViewTitle)
        val registerButton: Button = findViewById(R.id.registerButton)

        // 'recipe'라는 key로 전달된 객체를 Recipe 클래스 타입으로 가져오기
        val recipe: Recipe? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // "getParcelable("recipe", Recipe::class.java)"는 API 33 이상에서만 사용 가능
            intent.getParcelableExtra("recipe", Recipe::class.java)
        } else {
            @Suppress("DEPRECATION") // "getParcelableExtra("recipe")"는 API 32 이하에서 사용가능하지만 Deprecated이므로 "@Suppress("DEPRECATION")" 추가
            intent.getParcelableExtra("recipe")
        }
        recipe?.let {
            textViewTitle.text = "레시피 수정"
            registerButton.text = "수정"

            editTextRecipe.setText(it.name)
            editTextDescription.setText(it.description)
            imageUri = Uri.parse(it.imageUri)
            Glide.with(this)
                .load(Uri.parse(it.imageUri))  // content:// URI로 이미지 로드
                .into(imageView)
            recipeItemList = it.steps.toMutableList()
        }

        recyclerView = findViewById(R.id.recyclerView)
        adapter = RecipeStepEditAdapter(recipeItemList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 등록, 수정 버튼 클릭 시
        registerButton.setOnClickListener {
            val newRecipe = Recipe(
                name = editTextRecipe.text.toString(),
                imageUri = imageUri.toString(),
                description = editTextDescription.text.toString(),
                steps = recipeItemList
            )

            println("newRecipe: $newRecipe")

            // 기존 레시피 리스트 가져오기
            val currentList = sharedPreferencesHelper.getRecipeList()

            val resultIntent = Intent()

            // 새 레시피 추가 또는 수정
            val updatedList = if (recipe != null) {
                resultIntent.putExtra("isEditing", true)
                // 수정 모드에서는 기존 레시피를 수정
                val updatedList = currentList.toMutableList()
                val index = updatedList.indexOfFirst { it.name == recipe.name }
                if (index != -1) {
                    updatedList[index] = newRecipe
                }
                updatedList
            } else {
                // 등록 모드에서는 새 레시피 추가
                currentList + newRecipe
            }

            // 레시피 리스트를 쉐어드프리퍼런스에 저장
            sharedPreferencesHelper.saveRecipeList(updatedList)

            // 저장 후, 목록 화면으로 돌아가기
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun checkPermissionAndPickImage() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                openGallery()
            }
            shouldShowRequestPermissionRationale(permission) -> {
                // 사용자에게 권한 필요 이유 설명 후 요청
                AlertDialog.Builder(this)
                    .setTitle("권한 필요")
                    .setMessage("이미지를 선택하려면 저장소 접근 권한이 필요합니다.")
                    .setPositiveButton("허용") { _, _ ->
                        requestPermissionLauncher.launch(permission)
                    }
                    .setNegativeButton("취소", null)
                    .show()
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun openGallery() {
        // 이미지 선택을 위한 Intent 생성
        val pickImageIntent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"  // 이미지 파일만 선택
        }
        pickImage.launch(pickImageIntent)  // 파일 선택 화면 시작
    }

    // 이미지를 표시할 메소드
    private fun displayImage(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .into(imageView)
    }
}

class RecipeStepEditAdapter(val recipeItemList: MutableList<String>) : RecyclerView.Adapter<RecipeStepEditAdapter.RecipeStepEditViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeStepEditViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe_step_edit, parent, false)
        return RecipeStepEditViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeStepEditViewHolder, position: Int) {
        val recipe = recipeItemList[position]
        holder.editText.setText(recipe)

        // 기존 리스너 제거 (기존 리스너가 남아 있으면 불필요한 호출이 발생할 수 있음)
        holder.editText.removeTextChangedListener(holder.textWatcher)

        // 새로운 리스너 추가
        holder.textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                recipeItemList[holder.adapterPosition] = s.toString()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        holder.editText.addTextChangedListener(holder.textWatcher)

        holder.imageButton.setOnClickListener {
            val newItemPosition = holder.adapterPosition + 1
            recipeItemList.add(newItemPosition, "")
            notifyItemInserted(newItemPosition)
        }
    }

    override fun getItemCount(): Int = recipeItemList.size

    class RecipeStepEditViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val editText: EditText = itemView.findViewById(R.id.editText)
        val imageButton: ImageButton = itemView.findViewById(R.id.imageButton)
        var textWatcher: TextWatcher? = null
    }
}
