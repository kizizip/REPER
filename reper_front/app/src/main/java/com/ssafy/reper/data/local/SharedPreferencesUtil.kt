//package com.ssafy.smartstore_jetpack.data.local
//
//import android.content.Context
//import android.content.SharedPreferences
//import android.util.Log
//import com.google.gson.Gson
//import com.ssafy.smartstore_jetpack.data.model.dto.UserTodayeat
//
//private const val TAG = "SharedPreferencesUtil_싸피"
//class SharedPreferencesUtil (context: Context) {
//    val SHARED_PREFERENCES_NAME = "smartstore_preference"
//    val COOKIES_KEY_NAME = "cookies"
//
//    var preferences: SharedPreferences =
//        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
//
//    //사용자 정보 저장
//    fun addUser(user: UserTodayeat){
//        val editor = preferences.edit()
//        editor.putString("id", user.id)
//        editor.putString("name", user.name)
//        editor.putString("address", user.address)
//        editor.putString("phone", user.phone)
//
//        Log.d(TAG, "addUser: ${user.address}, ${user.phone}")
//        editor.apply()
//    }
//
//    fun getUser(): UserTodayeat {
//        val id = preferences.getString("id", "")
//        val name = preferences.getString("name", "")
//        val pass = preferences.getString("pass", "")
//        val phone = preferences.getString("phone", "")
//        val address = preferences.getString("address", "")
//
//        if (id != ""){
//            return UserTodayeat(id!! ,name!!, pass!!, phone!!, address!!)
//        }else{
//            return UserTodayeat("","","","","")
//        }
//    }
//
//    fun deleteUser(){
//        //preference 지우기
//        val editor = preferences.edit()
//        editor.clear()
//        editor.apply()
//    }
//
//    fun addUserCookie(cookies: HashSet<String>) {
//        val editor = preferences.edit()
//        editor.putStringSet(COOKIES_KEY_NAME, cookies)
//        editor.apply()
//    }
//
//    fun getUserCookie(): MutableSet<String>? {
//        return preferences.getStringSet(COOKIES_KEY_NAME, HashSet())
//    }
//
//    fun deleteUserCookie() {
//        preferences.edit().remove(COOKIES_KEY_NAME).apply()
//    }
//
//
//}


package com.ssafy.reper.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.kakao.sdk.user.model.User
import com.ssafy.reper.data.dto.LoginResponse
import com.ssafy.reper.data.dto.UserInfo

class SharedPreferencesUtil(context: Context) {
    private var preferences: SharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val SHARED_PREFERENCES_NAME = "reper_preference"
        private const val KEY_USER_COOKIE = "user_cookie"
        private const val KEY_LOGIN_TIME = "login_time"
        private const val SESSION_TIMEOUT = 10 * 60 * 1000 // 10분 (밀리초)
    }

    // 로그인 시간 저장
    fun saveLoginTime() {
        preferences.edit().putLong(KEY_LOGIN_TIME, System.currentTimeMillis()).apply()
    }

    // 세션이 유효한지 확인
    fun isSessionValid(): Boolean {
        val loginTime = preferences.getLong(KEY_LOGIN_TIME, 0)
        return System.currentTimeMillis() - loginTime < SESSION_TIMEOUT
    }

    fun getUserCookie(): String? {
        return preferences.getString(KEY_USER_COOKIE, null)
    }

    fun saveUserCookie(cookie: String) {
        preferences.edit().putString(KEY_USER_COOKIE, cookie).apply()
    }

    //사용자 정보 저장
    fun addUser(userinfo: UserInfo){
        val editor = preferences.edit()
        editor.putLong("userId", userinfo.userId)
        editor.putString("username", userinfo.username)
        editor.putString("role", userinfo.role)
        editor.apply()
        saveLoginTime() // 사용자 정보 저장시 로그인 시간도 저장
    }

    // 사용자 정보 가져오기 (수정 필요)
    fun getUser(): LoginResponse {
        return LoginResponse(
            userId = preferences.getLong("userId", -1L),
            username = preferences.getString("username", ""),
            role = preferences.getString("role", ""),
        )
    }

    // 모든 사용자 데이터 삭제
    fun clearUserData() {
        preferences.edit().apply {
            remove(KEY_USER_COOKIE)
            remove("userId")
            remove("username")
            remove("role")
            remove(KEY_LOGIN_TIME)
            apply()
        }
    }
}