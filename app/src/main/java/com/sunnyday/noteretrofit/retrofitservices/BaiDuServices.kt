package com.sunnyday.noteretrofit.retrofitservices

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

/**
 * Create by SunnyDay on 20:49 2022/01/25
 */
interface BaiDuServices {
    /**
     * 请求百度网页的接口
     *参考： https://blog.csdn.net/a77979744/article/details/67913738
     * */
    @GET("/")
    fun getDataFromBaiDu(): Call<ResponseBody>
}