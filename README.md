

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

######  定义个请求接口

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

###### 最简单的get 异步请求百度

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
/com.sunnyday.noteretrofit I/MainActivity: currentThread:Thread[main,5,main]
/com.sunnyday.noteretrofit I/MainActivity: 请求成功！
/com.sunnyday.noteretrofit I/MainActivity: 获取数据：<!DOCTYPE html>....... </p> </div> </div> </div> </body> </html>
```

###### 如何进行同步请求？

> 很简单使用Call对象的execute方法。

```java
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
```



###### 注意点：导包

###### > 和oKhttp混合使用，可能导包有点迷惑。

- Call的导包：Call 为retrifit 库的包
- Call泛型值：未添加addConverterFactory时泛型值默认为okhttp3.ResponseBody类型
- CallBack：call#enqueue时使用的callBack也是retrofit库中的。

```java
//ResponseBody 为Okhttp3库的包
import okhttp3.ResponseBody
// Call 为retrifit 库的包
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
```



###### 小疑惑：定义接口时可以在Call< T >传任意类型吗？

> 不行的这个与转化器有关，可通过addConverterFactory来添加，来定义call泛型类型。
>
> 未添加时默认为okhttp3.ResponseBody类型，使用其他类则报错，如传个String。

###### 自定义ConverterFactory 直接获取Sting类型返回值

```java
interface BaiDuServices {
    /**
     * 请求百度网页的接口
     *参考： https://blog.csdn.net/a77979744/article/details/67913738
     * */
    @GET("/")
    fun getDataFromBaiDu(): Call<ResponseBody>

    /**
     * 定义接口，期望返回String数据
     * */
    @GET("/")
    fun sendHttp2BaiDu(): Call<String>
}
```

```java
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
```



###### Retrofit的扩展 

> Retrofit 提供了很多自定义的ConverterFactory 大大便利了我们的开发，如：
>
>  GsonConverterFactory ： 支持Gson解析 ，可吧网络返回json直接映射为实体类。
>
>  RxJavaCallAdapterFactory ：支持Rxjava
>
> 等等有很多，使用时需要额外添加依赖，如下GsonConverterFactory 

```java
implementation 'com.squareup.retrofit2:converter-gson:2.0.2'
```



# 注解



# 封装



待续~

参考：

[文章1](https://blog.csdn.net/carson_ho/article/details/73732076?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522164266983216780366538164%2522%252C%2522scm%2522%253A%252220140713.130102334..%2522%257D&request_id=164266983216780366538164&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~top_positive~default-1-73732076.first_rank_v2_pc_rank_v29&utm_term=Retrofit&spm=1018.2226.3001.4187)

[文章2](https://blog.csdn.net/qq_30621333/article/details/115485408)

[retrofit Github 官网](https://github.com/square/retrofit)