

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

###### 1、如何同步/异步请求百度？ 

Retrofit采用Base域名+请求接口的形式访问网络的，首先我们需要定义个请求接口

```java
/**
 * Create by SunnyDay on 20:49 2022/01/25
 */
interface BaiDuServices {
    /**
     * get请求，请求百度网页的接口。
     * */
    @GET("/")
    fun getDataFromBaiDu(): Call<String>
      
   /**
     * post请求，请求百度网页的接口，上传key-value字符串。
     * */
    @POST("/")
    fun sendHttp2BaiDuByPost(): Call<ResponseBody>
}
```



最简单的get 异步请求百度：

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

如何进行同步请求呢？很简单使用Call对象的execute方法即可：

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

post请求如何做呢？

```java
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
//I/MainActivity: 请求成功！
//I/MainActivity: 获取数据：null
```



###### 2、导包注意点

使用retrofit导包注意下，因为和oKhttp混合使用，可能导包有点迷惑，如下：

- Call的导包：Call 为retrifit 库的包。
- Call泛型值：未添加addConverterFactory时泛型值默认为okhttp3.ResponseBody类型
- Callback：call#enqueue时使用的callback也是retrofit库中的。

###### 3、作用很大的ConvertFactory

或许刚学习这个框架时可能存在的疑惑"定义接口时可以在Call< T >传任意类型吗"？

> 答案是不行的，这个与转化器有关，可通过addConverterFactory来添加，来自定义call泛型类型。
>
> 若未添加addConverterFactory时默认泛型为okhttp3.ResponseBody类型，使用其他类则报错，如传个String。这时不想报错就需要自定convertFactory。



如何自定义convertFactory？

```java
interface BaiDuServices {
    /**
     * 定义接口，期望Response#body()返回String数据
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

###### 4、ConvertFactory 扩展类

Retrofit 提供了很多自定义的ConverterFactory 大大便利了我们的开发，如：

- GsonConverterFactory ： 支持Gson解析 ，可吧网络返回json直接映射为实体类。
- RxJavaCallAdapterFactory ：支持Rxjava。

等等有很多，使用时需要额外添加依赖，如下GsonConverterFactory 使用栗子：

```java
implementation 'com.squareup.retrofit2:converter-gson:2.0.2'
```

```java
   /**
     * 定义接口，期望返回指定的Model类型数据.
     * */
    @GET("/OkHttp/TestListJson.json")
    fun getWangZheData(): Call<WangZheModel>

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
               Log.i(TAG,"onResponse:${response.body()}")
            }
        })
    }
// log:
MainActivity: onResponse:WangZheModel(categoryId=0, categoryName=打野, name=李白)
```

# Retrofit注解

Retrofit 是基于注解的请求框架，框架提供和很多注解字段来供我们使用。其中根据作用可以大致分为三类：

- Http请求方法：注解作用于方法上，表示属于xxx类型的Http请求。
- 网络请求参数：注解一般作用于方法的参数上，可做url字段拼接、信息上传等。
- 特殊标记：注解一般作用于方法上，一般post请求上传信息时会用到。

| Http请求方法 | 简介                           |
| -------- | ---------------------------- |
| @GET     | 对应HTTP的get请求。标记为get请求。       |
| @POST    | 对应HTTP的post请求。标记为post请求。     |
| @PUT     | 对应HTTP的put请求。标记为put请求。       |
| @DELETE  | 对应HTTP的delete请求。标记为delete请求。 |
| @PATH    | 对应HTTP的path请求。标记为path请求。     |
| @HEAD    | 对应HTTP的head请求。标记为head请求。     |
| @OPTION  | 对应HTTP的option请求。标记为option请求。 |
| @HTTP    | 可扩展字段，可以替换上述7种Http请求方法。      |

| 特殊标记            | 简介            |
| --------------- | ------------- |
| @FormUrlEncoded | 表示是一个普通表单请求   |
| @Multipart      | 表示是一个文件上传请求   |
| @Streaming      | 表示返回的数以流的形式返回 |

| 参数标记      | 简介                        |
| --------- | ------------------------- |
| @Header   | 添加不固定值请求头                 |
| @Headers  | 添加固定值的请求头                 |
| @Part     | 用于表单字段（有注解参数）、文件上传（无注解参数） |
| @PartMap  |                           |
| @Url      | url设置                     |
| @Body     | 用于非表单请求体                  |
| @Path     | url省缺值                    |
| @Query    | 用于表单字段，同@Field            |
| @QueryMap |                           |
| @Filed    | 向post表单输入key值             |
| @FiledMap |                           |

# Retrofit注解使用

###### 1、Http请求方法

作用对象就是接口里的方法，标记方法为xxx请求方式。

get/post 最简单的使用已经在前面的”案例引申“举过栗子啦，实际开发中远远比这要复杂的多，比如get请求path，params的拼接。post表单、文件等上传，这些是需要结合其他类型注解来共同完成的，这个放到下文讲解。

除了get/post其他的请求方法一般不常用，我们需要时再探究即可。这里就讲解下@HHTTP的使用~~~ 其实很简单

只需要修改下接口即可：

```java
   
    /**
     * 使用@GET注解.
     * */
    @GET("/OkHttp/TestListJson.json")
    fun getWangZheData(): Call<WangZheModel>

   /**
     * 使用@HTTP来替换@GET
     * */
    @HTTP(method = "GET",path = "/OkHttp/TestListJson.json")
    fun getWangZheDataTest(): Call<WangZheModel>
```

###### 2、特殊标记

（1）FormUrlEncoded

标记作用于方法，表示这是一个普通的表单上传，可想成类似Okhttp的FormBody。一般结合post请求使用。

在进行表单上传时要使用@Filed字段，每个键值对需要用@Filed来注解键名。

```java
    /**
     *post 请求，上传表单数据，注解参数值就是表单的key，被注解作用的字段作为表单value
     * 
     */
    @FormUrlEncoded
    @POST("/UserInfo")
    fun formUpLoad(
        @Field("userName") userName: String,
        @Field("userPwd") userPwd: String
    ): Call<ResponseBody>
```

这样写后就代表这是一个表单请求了，@Field注解的参数值就是要上传的key值，注解作用的方法参数值会作为value值进行上传。

（2）Multipart

标记作用于方法，适用于文件上传,支持多文件上传。可以想象成Okhttp的RequestBody、或MultipartBody。

使用Multipart标记的方法需要结合@part字段，来上传文件。

```java
   /**
     * 注意：
     * 1、@Part字段支持两种类型RequestBody、MultipartBody.Part类型。
     * 2、RequestBody使用时需要给@Part("字段")添加注解字段参数。
     * 3、MultipartBody.Part使用时不需要给@Part添加字段参数，
     * 因为MultipartBody.Part中已经包含了表单字段信息。
     *
     * */
    @Multipart
    @POST("/UserInfo/file")
    fun upLoadFile(
        @Part("userName") userName: RequestBody,
        @Part("userPwd") userPwd: RequestBody,
        @Part file: MultipartBody.Part
    )
```



```java
    private fun MultipartDemo() {
        val baseUrl = "https://www.baidu.com/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .build()
        val baiDuServices = retrofit.create(BaiDuServices::class.java)

        // 通过RequestBody包装FormBody。模拟FormBody。
        val formMediaType = "application/x-www-form-urlencoded".toMediaType()
          //@part("") 注解参数对应key，这里是value。
        val userName = RequestBody.create(formMediaType,"Tom")
          //@part("") 注解参数对应key，这里是value。
        val userPwd = RequestBody.create(formMediaType,"123456")

        //上传文件
        val fileType = "File/*".toMediaTypeOrNull()
        val requestBody = RequestBody.create(fileType, File(cacheDir.absolutePath))
        //多文件上传的part
        val filePart = MultipartBody.Part.createFormData("file", "test.txt", requestBody)
        val call  = baiDuServices.upLoadFile(userName,userPwd,filePart)
        //.....
    }
}
```



###### 3、网络请求参数

（1）@Header/@Headers

- @Header:添加不固定值请求头
- @Headers:添加固定值请求头。

```java
    @GET("/")
    fun addHeaderTest(@Header("Authorization") authorization: String)
    
    @Headers("Authorization: authorization")
    @GET("/")
    fun addHeadersTest()
```

二者效果一致，只是作用的对象不一样。一个作用于方法，一个作用于方法参数。

（2）@Body

以post方式传递自定义数据类型给服务器。当提交的是一个map时相当于一个@Field的作用。

（3）@Field /@FieldMap

与 `@FormUrlEncoded` 注解配合使用，发送 Post请求 时提交请求的表单字段。

```java
    /**
     *post 请求，上传多对键值对表单数据。map的key就是表单的key
     * 
     */
    @FormUrlEncoded
    @POST("/UserInfo")
    fun formUpLoad(
        @FieldMap map: Map<String, String>
    ): Call<ResponseBody>
```

(4) @Part / @PartMap

发送 Post请求 时提交请求的表单字段,功能与@Field功能相同，但携带的参数类型更加丰富，包括数据流，所以适用于 有文件上传 的场景。



（5）@Query/@QueryMap

用于 `@GET` 方法的查询参数拼接。如下栗子：

```java
    @GET("user/info")
    fun getUserInfo(@Query("userName")userName:String,@Query("userPwd")userPwd:String)
```

当调用getUserInfo方法时传参”Tom“，”123456“ 这时url会被拼接为：

```java
user/info?userName=Tom&userPwd=123456
```

@queryMap 的功能类似，只是吧要拼接的字段放入了map中。



（6）@Path

```java
    /**
     * url 路径动态替换。
     * 假如userId为123则此时url为：/users/123/userInfo
     *
     *使用时使用{path注解值}来动态取值
     * */
    @GET("/users/{userId}/userInfo")
    fun pathDemo(@Path("userId") userId: String)
```

(7)@Url

使用这个字段时@GET注解不需要传递url。栗子

```java
    // 不需要传递url，否则报错。
    @GET
    fun requestBaidu(@Url url:String):Call<ResponseBody>
```



```java

        val requestUrl = "https://192.168.1.1/s?userName=Tom"
        //val requestUrl = "https://192.168.1.1/s?userName=Tom"
        //https://192.168.1.1/可以去除，没问题如.requestUrl = "s?userName=Tom"
        requestUrlTest(requestUrl)
          
 //  @Url测试        
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
```



# 封装

结合Rxjava或者封装一下吧。到时候单独抽取一篇~



# 参考：

[这是一份详细的 Retrofit使用教程](https://blog.csdn.net/carson_ho/article/details/73732076?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522164266983216780366538164%2522%252C%2522scm%2522%253A%252220140713.130102334..%2522%257D&request_id=164266983216780366538164&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~top_positive~default-1-73732076.first_rank_v2_pc_rank_v29&utm_term=Retrofit&spm=1018.2226.3001.4187)

[Retrofit 注解学习](https://blog.csdn.net/weixin_36709064/article/details/82468549)

[retrofit Github 官网](https://github.com/square/retrofit)



