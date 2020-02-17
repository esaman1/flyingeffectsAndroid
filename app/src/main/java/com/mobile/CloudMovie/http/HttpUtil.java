package com.mobile.CloudMovie.http;


import com.mobile.CloudMovie.enity.HttpResult;
import com.mobile.CloudMovie.base.ActivityLifeCycleEvent;

import rx.Observable;
import rx.functions.Action0;
import rx.subjects.PublishSubject;


/**
 * Created by zhangtongju
 * on 2016/10/10 11:32.
 */

public class HttpUtil {

    /**
     * 构造方法私有
     */
    private HttpUtil() {
        //手动创建一个OkHttpClient并设置超时时间
//        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
//        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
//        /**
//         * 对所有请求添加请求头
//         */
//        httpClientBuilder.addInterceptor(new Interceptor() {
//            @Override
//            public okhttp3.Response intercept(Chain chain) throws IOException {
//                Request request = chain.request();
//                okhttp3.Response originalResponse = chain.proceed(request);
//                return originalResponse.newBuilder().header("mobileFlag", "adfsaeefe").addHeader("type", "4").build();
//            }
//        });
//        retrofit = new Retrofit.Builder()
//                .client(httpClientBuilder.build())
//                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                .baseUrl(Url.BASE_URL)
//                .build();

    }

    /**
     * 在访问HttpMethods时创建单例
     */
    private static class SingletonHolder {
        private static final HttpUtil INSTANCE = new HttpUtil();
    }

    /**
     * 获取单例
     */
    public static HttpUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }

//    public void getTopMovie(final ProgressSubscriber<List<Subject>> subscriber, int start, int count) {
//        movieService = retrofit.create(ApiService.class);
//        Api.getDefault().getTopMovie(start, count)
//                .map(new HttpResultFunc<List<Subject>>())
//                .subscribeOn(Schedulers.io())
//                .unsubscribeOn(Schedulers.io())
//                .doOnSubscribe(new Action0() {
//                    @Override
//                    public void call() {
//                        //显示Dialog
//                        subscriber.showProgressDialog();
//                    }
//                })
//                /**
//                 * 保证doOnSubscribe是在主线程执行
//                 */
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(subscriber);
//
//        toSubscribe(Api.getDefault().getTopMovie(start, count), subscriber,"getMove");
//    }

//    public void userLogin(final ProgressSubscriber<UserEntity> subscriber, String name, String psw) {
//        Observable observer =  Api.getDefault().userLogin(name, psw, 4, "aaassdasd");
//                .map(new HttpResultFunc2<UserEntity>());
//                .subscribeOn(Schedulers.io())
//                .unsubscribeOn(Schedulers.io())
//                .doOnSubscribe(new Action0() {
//                    @Override
//                    public void call() {
//                        //显示Dialog
//                        subscriber.showProgressDialog();
//                    }
//                })
//                /**
//                 * 保证doOnSubscribe是在主线程执行
//                 */
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(subscriber);

//        toSubscribe(observer, subscriber,"login");
//    }


    /**
     * 添加线程管理并订阅
     *
     * @param ob
     * @param subscriber
     * @param cacheKey         缓存kay
     * @param event            Activity 生命周期
     * @param lifecycleSubject
     * @param isSave           是否缓存
     * @param forceRefresh     是否强制刷新
     */
    public void toSubscribe(Observable ob, final ProgressSubscriber subscriber, String cacheKey, final ActivityLifeCycleEvent event, final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject, boolean isSave, boolean forceRefresh, final boolean isShowDilog,String actTag) {
        //数据预处理
        Observable.Transformer<HttpResult<Object>, Object> result = RxHelper.handleResult(event, lifecycleSubject,actTag);
        Observable observable = ob.compose(result)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        //显示Dialog和一些其他操作
                        if(isShowDilog){
                            subscriber.showProgressDialog();
                        }
                    }
                });
       RetrofitCache.load(cacheKey, observable, isSave, forceRefresh).subscribe(subscriber); //添加缓存
    }


    /**
     * 添加线程管理并订阅
     *
     * @param ob
     * @param subscriber
     * @param cacheKey         缓存kay
     * @param event            Activity 生命周期
     * @param lifecycleSubject
     * @param isSave           是否缓存
     * @param forceRefresh     是否强制刷新
     */
    public void toSubscribe(Observable ob, final ProgressSubscriber subscriber, String cacheKey, final ActivityLifeCycleEvent event, final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject, boolean isSave, boolean forceRefresh, final boolean isShowDialog) {
        //数据预处理
        Observable.Transformer<HttpResult<Object>, Object> result = RxHelper.handleResult(event, lifecycleSubject);
        Observable observable = ob.compose(result)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        //显示Dialog和一些其他操作
                        if(isShowDialog){
                            subscriber.showProgressDialog();
                        }
                    }
                });
        RetrofitCache.load(cacheKey, observable, isSave, forceRefresh).subscribe(subscriber); //添加缓存
    }
}
