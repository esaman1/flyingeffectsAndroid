package com.mobile.CloudMovie.http;


import android.support.annotation.NonNull;

import com.mobile.CloudMovie.base.ActivityLifeCycleEvent;
import com.mobile.CloudMovie.enity.HttpResult;
import com.mobile.CloudMovie.utils.LogUtil;
import com.mobile.CloudMovie.utils.StringUtil;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;


/**
 * Created by zhangtongju
 * on 2016/11/9 17:02.
 */

public class RxHelper {

    /**
     * 利用Observable.takeUntil()停止网络请求
     * @param event
     * @param lifecycleSubject
     * @param <T>
     * @return
     */
    @NonNull
    public <T> Observable.Transformer<T, T> bindUntilEvent(@NonNull final ActivityLifeCycleEvent event, final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject) {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> sourceObservable) {
                Observable<ActivityLifeCycleEvent> compareLifecycleObservable =
                        lifecycleSubject.takeFirst(new Func1<ActivityLifeCycleEvent, Boolean>() {
                            @Override
                            public Boolean call(ActivityLifeCycleEvent activityLifeCycleEvent) {
                                return activityLifeCycleEvent.equals(event);
                            }
                        });
                return sourceObservable.takeUntil(compareLifecycleObservable);
            }
        };
    }


    /**o
     * @param <T>
     * @return
     */
    public static <T> Observable.Transformer<HttpResult<T>, T> handleResult(final ActivityLifeCycleEvent event, final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject) {
        return new Observable.Transformer<HttpResult<T>, T>() {
            @Override
            public Observable<T> call(Observable<HttpResult<T>> tObservable) {
                Observable<ActivityLifeCycleEvent> compareLifecycleObservable =
                        lifecycleSubject.takeFirst(new Func1<ActivityLifeCycleEvent, Boolean>() {//takeUntil一旦当前的什么周期==输入的生命周期，那么久停止对外发射数据
                            @Override
                            public Boolean call(ActivityLifeCycleEvent activityLifeCycleEvent) {
                                return activityLifeCycleEvent.equals(event);
                            }
                        });
                return tObservable.flatMap(new Func1<HttpResult<T>, Observable<T>>() { //转换，把返回的固定值标识通过flatMap来装换成Observable
                    @Override
                    public Observable<T> call(HttpResult<T> result) {
                        String  data= StringUtil.beanToJSONString(result);
                        LogUtil.d("Observable",data);
                        if (result.getStatus()==1) {
                           return createData(result.getResult());//返回data
                        } else {
                            return Observable.error(new ApiException(result.getStatus(),result.getMsg()));//返回错误信息
                        }

                    }
                }
                ).takeUntil(compareLifecycleObservable).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }



    /**
     *添加了界面标识
     */
    public static <T> Observable.Transformer<HttpResult<T>, T> handleResult(final ActivityLifeCycleEvent event, final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject, final String actTag) {
        return new Observable.Transformer<HttpResult<T>, T>() {
            @Override
            public Observable<T> call(Observable<HttpResult<T>> tObservable) {
                Observable<ActivityLifeCycleEvent> compareLifecycleObservable =
                        lifecycleSubject.takeFirst(new Func1<ActivityLifeCycleEvent, Boolean>() {//takeUntil一旦当前的什么周期==输入的生命周期，那么久停止对外发射数据
                            @Override
                            public Boolean call(ActivityLifeCycleEvent activityLifeCycleEvent) {
                                return activityLifeCycleEvent.equals(event);
                            }
                        });
                return tObservable.flatMap(new Func1<HttpResult<T>, Observable<T>>() { //转换，把返回的固定值标识通过flatMap来装换成Observable
                                               @Override
                                               public Observable<T> call(HttpResult<T> result) {

                                                   if (result.getStatus()==1) {
                                                       return createData(result.getResult());//返回data
                                                   } else {
                                                       return Observable.error(new ApiException(result.getStatus(),result.getMsg(),actTag));//返回错误信息
                                                   }

                                               }
                                           }
                ).takeUntil(compareLifecycleObservable).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }




    /**
     * 创建成功的数据
     *
     * @param data
     * @param <T>
     * @return
     */
    private static <T> Observable<T> createData(final T data) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    subscriber.onNext(data);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });

    }
}
