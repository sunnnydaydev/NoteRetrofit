package com.sunnyday.noteretrofit.retrofitservices

import com.sunnyday.noteretrofit.WangZheModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import java.io.File

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
    @HTTP(method = "GET", path = "/OkHttp/TestListJson.json")
    fun getWangZheDataTest(): Call<WangZheModel>

    /**
     * 定义请求百度网页的接口，上传key-value字符串
     * */
    @POST("/")
    fun sendHttp2BaiDuByPost(): Call<ResponseBody>

    /**
     *post 请求，上传表单数据，注解参数值就是表单的key，被注解作用的字段作为表单value
     * */
    @FormUrlEncoded
    @POST("/UserInfo")
    fun formUpLoad(
        @Field("userName") userName: String,
        @Field("userPwd") userPwd: String
    ): Call<ResponseBody>

    /**
     *post 请求，上传多对键值对表单数据。map的key就是表单的key
     * */
    @FormUrlEncoded
    @POST("/UserInfo")
    fun formUpLoad(
        @FieldMap map: Map<String, String>
    ): Call<ResponseBody>

    /**
     * 注意：
     * 1、@Part字段支持两种类型RequestBody、MultipartBody.Part类型。
     * 2、RequestBody使用时需要给@Part("字段")添加注解字段参数。
     * 3、MultipartBody.Part使用时不需要给@Part添加字段参数，因为MultipartBody.Part中已经包含了表单字段信息。
     *
     * */
    @Multipart
    @POST("/UserInfo/file")
    fun upLoadFile(
        @Part("userName") userName: RequestBody,
        @Part("userPwd") userPwd: RequestBody,
        @Part file: MultipartBody.Part
    )

    @GET("/")
    fun addHeaderTest(@Header("Authorization") authorization: String)

    @Headers("Authorization: authorization")
    @GET("/")
    fun addHeadersTest()

    @GET("user/info")
    fun getUserInfo(@Query("userName") userName: String, @Query("userPwd") userPwd: String)

    /**
     * url 路径动态替换。
     * 假如userId为123则此时url为：/users/123/userInfo
     *
     *使用时使用{path注解值}来动态取值
     * */
    @GET("/users/{userId}/userInfo")
    fun pathDemo(@Path("userId") userId: String)

    @GET
    fun requestBaidu(@Url url:String):Call<ResponseBody>
}