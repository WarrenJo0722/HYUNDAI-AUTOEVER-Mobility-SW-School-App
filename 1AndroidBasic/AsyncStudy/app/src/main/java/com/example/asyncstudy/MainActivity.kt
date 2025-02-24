package com.example.asyncstudy

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var textView: TextView
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textView)

        // 코루틴을 사용하여 데이터를 가져오기
        GlobalScope.launch(Dispatchers.Main) {
            val data = fetchDataFromApi()
//            textView.text = "ㅁㄴㅇㄹ"
            delay(timeMillis = 2000L)
            textView.text = data
        }

        textView.text = "ㅁㄴㅇㄹ1213123123"

        /*// 잘못된 방식으로 UI 업데이트를 시도
        GlobalScope.launch {
            // 네트워크 작업이나 오래 걸리는 작업
            delay(2000)  // 2초 대기 (가정: 네트워크 작업)
            textView.text = "작업 완료"  // UI 업데이트 시도
        }*/

        /*// 올바른 방식으로 UI 업데이트
        GlobalScope.launch {
            // 네트워크 작업이나 오래 걸리는 작업
            delay(2000)  // 2초 대기 (가정: 네트워크 작업)

            // UI 업데이트를 메인 스레드에서 처리
            withContext(Dispatchers.Main) {
                textView.text = "작업 완료"
            }
        }*/
    }

    // Retrofit을 사용하여 API에서 JSON 데이터를 문자열로 가져오기
    private suspend fun fetchDataFromApi(): String {
        return try {
            val response = RetrofitInstance.api.getLines()  // API 호출
            response.toString()  // 받은 데이터 그대로 String으로 변환하여 반환

        } catch (e: HttpException) {
            "HTTP Exception: ${e.message}"
        } catch (e: IOException) {
            "Network Error: ${e.message}"
        }
    }
}

// 데이터 모델 정의 (JSON 응답에 맞춰)
data class Line(val id: String, val name: String)

// API 인터페이스
interface ApiService {
    @GET("lines")
    suspend fun getLines(): List<Line>  // 데이터를 받아올 메서드
}

// Retrofit 객체 생성하기
object RetrofitInstance {
    private const val BASE_URL = "https://67209621cf285f60d77a5ff6.mockapi.io/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())  // Gson을 사용하여 JSON을 객체로 변환
            .build()
            .create(ApiService::class.java)
    }
}