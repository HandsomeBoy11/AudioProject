package com.longfor.audioproject.utils.timer;

import com.longfor.audioproject.audio.Logger;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class Timer  {

    private Disposable mDisposable;
    private CopyOnWriteArray<CountDownTimerObserver> mCountDownTimerCallBack;

    public static Timer getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final Timer INSTANCE = new Timer();
    }

    public void addCountDownTimerObserver(CountDownTimerObserver callBack) {
        if (mCountDownTimerCallBack == null) {
            mCountDownTimerCallBack = new CopyOnWriteArray<>();
        }
        removeCountDownTimerObserver(callBack);
        mCountDownTimerCallBack.add(callBack);
        start();
    }

    public void removeCountDownTimerObserver(CountDownTimerObserver callBack) {
        if (mCountDownTimerCallBack == null || callBack == null) {
            return;
        }
        mCountDownTimerCallBack.remove(callBack);
    }

    private void start() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            return;
        }
        mDisposable = Flowable.interval(0, 1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .takeUntil(o -> mCountDownTimerCallBack == null || mCountDownTimerCallBack.size() == 0)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> dispatchObserver(), throwable -> Logger.INSTANCE.e(throwable.toString(),""));
    }

    private void dispatchObserver() {
        final CopyOnWriteArray<CountDownTimerObserver> listeners = mCountDownTimerCallBack;
        if (listeners != null && listeners.size() > 0) {
            CopyOnWriteArray.Access<CountDownTimerObserver> access = listeners.start();
            try {
                int count = access.size();
                for (int i = 0; i < count; i++) {
                    access.get(i).onNext();
                }
            } finally {
                listeners.end();
            }
        }
    }

    /**
     * APP退出时调用
     */
    public void stop() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    public interface CountDownTimerObserver {
        void onNext();
    }

}