package com.sunnyday.noteretrofit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.sunnyday.noteretrofit.retrofitservices.BaiDuServices
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {
    companion object{
        const val TAG = "MainActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sendAsyncHttpRequestByRetrofit()
    }

    /**
     * 使用retrofit进行异步网络请求栗子
     * */
    private fun sendAsyncHttpRequestByRetrofit() {
        // 在这里建议在创建baseUrl中不以”/”结尾，API中以”/”开头和结尾。
        val baseUrl = "https://www.baidu.com/"

        // 1、创建retrofit实例
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .build()
        //2、获取自定义的接口实现对象
        val baiDuServices = retrofit.create(BaiDuServices::class.java)
        //3、获取call对象
        val call = baiDuServices.getDataFromBaiDu()
        // 4、异步请求
        call.enqueue(object :  Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.i(TAG,"currentThread:${Thread.currentThread()}")
                Log.i(TAG,"请求失败！")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.i(TAG,"currentThread:${Thread.currentThread()}")
                Log.i(TAG,"请求成功！")
                Log.i(TAG,"获取数据：${response.body()?.string()}")
            }
        })
    }
}