package com.sunnyday.noteretrofit.retrofitservices

import com.sunnyday.noteretrofit.WangZheModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.HTTP

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

    /**
     * 定义请求百度网页的接口，期望返回String数据
     * */
    @GET("/")
    fun sendHttp2BaiDu(): Call<String>

    /**
     * 定义接口，期望返回指定的Model类型数据.
     * */
    @GET("/OkHttp/TestListJson.json")
    fun getWangZheData(): Call<WangZheModel>

    /**
     * 使用@HTTP来替换@GET
     * */
    @HTTP(method = "GET",path = "/OkHttp/TestListJson.json")
    fun getWangZheDataTest(): Call<WangZheModel>
}