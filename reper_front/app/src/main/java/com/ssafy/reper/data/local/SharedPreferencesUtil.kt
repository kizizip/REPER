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

class SharedPreferencesUtil(context: Context) {
    private var preferences: SharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun getUserCookie(): String? {
        return preferences.getString(KEY_USER_COOKIE, null)
    }

    fun saveUserCookie(cookie: String) {
        preferences.edit().putString(KEY_USER_COOKIE, cookie).apply()
    }

    companion object {
        private const val SHARED_PREFERENCES_NAME = "reper_preference"
        private const val KEY_USER_COOKIE = "user_cookie"
    }
}