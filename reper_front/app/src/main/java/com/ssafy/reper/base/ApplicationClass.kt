import android.Manifest
import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApplicationClass : Application() {
    companion object {
        // ipconfig를 통해 ip확인하기

        //집
        const val SERVER_URL = "http://192.168.1.181:8080/api/"
        //강의장
        //const val SERVER_URL = "http://192.168.100.102:8080/api/"

        // retrofit을 lazy로 변경하여 안전하게 초기화
        val retrofit: Retrofit by lazy {
            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .build()

            Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create(gson)) // gson을 여기서 사용
                .client(okHttpClient)
                .build()
        }

        // Gson 객체를 companion object 안에 선언
        val gson: Gson = GsonBuilder()
            .setLenient()
            .create()

        // 모든 퍼미션 관련 배열
        val requiredPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    }

    override fun onCreate() {
        super.onCreate()
        // onCreate()에서 초기화가 이루어지기 때문에 lazy initialization은
        // 처음 사용하는 시점에 안전하게 처리됩니다.
    }
}
