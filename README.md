

![Retrofit](https://github.com/sunnnydaydev/NoteRetrofit/blob/master/screenshot/Retrofit基础.png) 

# 简介

Retrofit 是什么呢？ Retrofit 是一个遵循 RESTful 风格的 HTTP 网络请求框架。这个框架和OkHttp一样也是Square公司出品。

那么已经有了OkHttp了为啥还需要Retrofit？其实Retrofit这个框架是基于OkHttp进行的封装。网络的请求、数据的接收本质还是由Okhttp来完成的。

Retrofit封装后简化了用户的操作，使用户进行网络交互更加方便。如线程的切换、json实体类的映射、RESTful接口规范这些统统由Retrofit封装好了。

# 准备工作

使用这个框架之前肯定是有些准备工作的，还是和OkHttp一样网络权限声明、依赖库添加。

```java
    <uses-permission android:name="android.permission.INTERNET"/>
```



```java
    implementation "com.squareup.okhttp3:okhttp:4.9.3"//okHttp
    implementation 'com.squareup.retrofit2:retrofit:2.5.0'//retrofit
```

#  案例引申

######  最简单的get 异步请求百度

```java
/**
 * Create by SunnyDay on 20:49 2022/01/25
 */
interface BaiDuServices {
    /**
     * 请求百度网页的接口
     * */
    @GET("/")
    fun getDataFromBaiDu(): Call<String>
}
```



```java
    /**
     * 使用retrofit进行异步网络请求栗子
     * */
    private fun sendAsyncHttpRequestByRetrofit() {
        // 在这里建议在创建baseUrl中不以”/”结尾，API中以”/”开头和结尾。
        val baseUrl = "https://www.baidu.com"
        
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
log：
2022-01-25 21:28:01.100 8515-8515/com.sunnyday.noteretrofit I/MainActivity: currentThread:Thread[main,5,main]
2022-01-25 21:28:01.100 8515-8515/com.sunnyday.noteretrofit I/MainActivity: 请求成功！
2022-01-25 21:28:01.101 8515-8515/com.sunnyday.noteretrofit I/MainActivity: 获取数据：<!DOCTYPE html>....... </p> </div> </div> </div> </body> </html>
```

###### 小总结

- 如何进行同步请求
- 注意点：Call的导包、Call泛型值

- retrofit的扩展：rxjava、Gson 结合。



###### addConverterFactory的使用

- 自定义（简书Demo）
- 三方提供（Gson的为例子）

# 注解



# 封装



待续~

参考：

[文章1](https://blog.csdn.net/carson_ho/article/details/73732076?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522164266983216780366538164%2522%252C%2522scm%2522%253A%252220140713.130102334..%2522%257D&request_id=164266983216780366538164&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~top_positive~default-1-73732076.first_rank_v2_pc_rank_v29&utm_term=Retrofit&spm=1018.2226.3001.4187)

[文章2](https://blog.csdn.net/qq_30621333/article/details/115485408)

[retrofit Github 官网](https://github.com/square/retrofit)