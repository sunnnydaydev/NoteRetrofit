package com.sunnyday.noteretrofit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.sunnyday.noteretrofit.retrofitservices.BaiDuServices
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
// 注意ResponseBody也是okHttp3库下的
import okhttp3.ResponseBody
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.Url
import java.io.File
import java.lang.reflect.Type
import java.util.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // sendAsyncHttpRequestByRetrofit()
        // sendSyncHttpRequestByRetrofit()
        //  customConvertFactory()
        // useGsonConverterFactory()
       // sendAsyncHttpPOSTRequestByRetrofit()

        val requestUrl = "https://192.168.1.1/s?userName=Tom"
        //val requestUrl = "https://192.168.1.1/s?userName=Tom"
        //https://192.168.1.1/可以去除，没问题如.requestUrl = "s?userName=Tom"
        requestUrlTest(requestUrl)
    }

    /**
     * 使用retrofit进行"异步"网络请求栗子
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
        // 4、异步请求：使用的call.enqueue(Callback<T> callback)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.i(TAG, "currentThread:${Thread.currentThread()}")
                Log.i(TAG, "请求失败！")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.i(TAG, "currentThread:${Thread.currentThread()}")
                Log.i(TAG, "请求成功！")
                Log.i(TAG, "获取数据：${response.body()?.string()}")
            }
        })
    }

    /**
     * “同步”方式访问网络
     *需要自己开线程，子线程中请求网络。
     * */
    private fun sendSyncHttpRequestByRetrofit() {
        thread {
            val baseUrl = "https://www.baidu.com/"
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .build()
            val baiDuServices = retrofit.create(BaiDuServices::class.java)
            val call = baiDuServices.getDataFromBaiDu()
            // 同步请求：使用的call.execute()
            val response = call.execute()
            val str = response.body()?.string()
            Log.i(TAG, "同步请求结果：$str")
        }
    }

    /**
     * 自定义ConvertFactory 使Response#body()返回值直接为String。
     * */
    private fun customConvertFactory() {
        val baseUrl = "https://www.baidu.com/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(object : Converter.Factory() {
                override fun responseBodyConverter(
                    type: Type,
                    annotations: Array<Annotation>,
                    retrofit: Retrofit
                ): Converter<ResponseBody, *>? {
                    return Converter<ResponseBody, String> {
                        // 1、注意这里：自定义Response#body()返回值
                        // 直接返回String数据
                        it.string()
                    }
                }
            })
            .build()
        val baiDuServices = retrofit.create(BaiDuServices::class.java)
        val call = baiDuServices.sendHttp2BaiDu()

        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {

            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                //2、使用自定义的结果
                Log.i(TAG, "获取数据：${response.body()}")
            }
        })
    }


    /**
     * 使用提供的GsonConverterFactory 直接获取实体类。
     * */
    private fun useGsonConverterFactory() {
        val baseUrl = "http://192.168.2.112:8080"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val baiDuServices = retrofit.create(BaiDuServices::class.java)
        val call = baiDuServices.getWangZheData()
        call.enqueue(object : Callback<WangZheModel> {
            override fun onFailure(call: Call<WangZheModel>, t: Throwable) {
                Log.i(TAG, "请求失败！")
                t.printStackTrace()
            }

            override fun onResponse(call: Call<WangZheModel>, response: Response<WangZheModel>) {
                Log.i(TAG, "onResponse:${response.body()}")
            }
        })
    }


    /**
     * 使用retrofit进行 post "异步"网络请求栗子,上传简单的键值对。
     * */
    private fun sendAsyncHttpPOSTRequestByRetrofit() {
        val baseUrl = "https://www.baidu.com/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .build()
        val baiDuServices = retrofit.create(BaiDuServices::class.java)
        val call = baiDuServices.sendHttp2BaiDuByPost()
        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.i(TAG, "currentThread:${Thread.currentThread()}")
                Log.i(TAG, "请求失败！")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.i(TAG, "currentThread:${Thread.currentThread()}")
                Log.i(TAG, "请求成功！")
                Log.i(TAG, "获取数据：${response.body()?.string()}")
            }
        })
    }


    private fun MultipartDemo() {
        val baseUrl = "https://www.baidu.com/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .build()
        val baiDuServices = retrofit.create(BaiDuServices::class.java)

        // 通过RequestBody包装FormBody。模拟FormBody。
        val formMediaType = "application/x-www-form-urlencoded".toMediaType()
        val userName = RequestBody.create(formMediaType,"Tom")//@part("") 注解参数对应key，这里是value。
        val userPwd = RequestBody.create(formMediaType,"123456")//@part("") 注解参数对应key，这里是value。

        //上传文件
        val fileType = "File/*".toMediaTypeOrNull()
        val requestBody = RequestBody.create(fileType, File(cacheDir.absolutePath))
        //多文件上传的part
        val filePart = MultipartBody.Part.createFormData("file", "test.txt", requestBody)
        val call  = baiDuServices.upLoadFile(userName,userPwd,filePart)
        //.....
    }

    private fun requestUrlTest(requestUrl:String){
        val baseUrl = "https://192.168.1.1/" // 这里不能省略
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .build()

        val baiDuServices = retrofit.create(BaiDuServices::class.java)

        val call = baiDuServices.requestBaidu(requestUrl)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.i(TAG, "请求失败！")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.i(TAG, "请求成功！")
                Log.i(TAG, "获取数据：${response.body()?.string()}")
            }
        })
    }
}