package com.example.feedapp

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide

class UploadActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private var imageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                pickImage() // 권한 허용 시 이미지 선택 실행
            } else {
                Toast.makeText(this, "앨범 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data
                uri?.let {
                    imageUri = uri
                    displayImage(uri) // 이미지 표시
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_upload)
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

        val editText = findViewById<EditText>(R.id.editTextIntroduce)
        imageView = findViewById(R.id.imageView)
        imageView.setOnClickListener {
            checkPermissionAndPickImage()
        }

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            // SharedPreferences 객체 얻기
            val sharedPref = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            // 저장된 데이터 읽기 (기본값 제공)
            val userName = sharedPref.getString("name", "") // 기본값 설정
            val userImageUri = sharedPref.getString("image", "")
            val newFeed = Feed(
                content = editText.text.toString(),
                imageUri = imageUri.toString(),
                userName = userName.toString(),
                userImageUri = userImageUri.toString(),
                likes = 0
            )
            val sharedPreferencesHelper = SharedPreferencesHelper(this)
            val currentList = sharedPreferencesHelper.getFeedList()
            val updatedList = currentList + newFeed
            sharedPreferencesHelper.saveFeedList(updatedList)

            // 저장 후, 목록 화면으로 돌아가기
            setResult(RESULT_OK)
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
                pickImage()
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

    private fun displayImage(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .into(imageView)
    }

    private fun pickImage() {
        val pickImageIntent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"  // 이미지 파일만 선택 가능
        }
        pickImageLauncher.launch(pickImageIntent)  // 이미지 선택 창 실행
    }
}