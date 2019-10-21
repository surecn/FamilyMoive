package com.surecn.moat.core;

/**
 * Created by surecn on 15/8/3.
 */
public class Observer<T> implements ResultObserver<T>, ProcessObserver {

    /**
     * 第一个任务流执行前
     */
    @Override
    public void onPre() {
    }

    @Override
    public void onNext() {

    }

    /**
     * 任务流正常执行完成时调用
     * @param result
     */
    @Override
    public void onComplete(TaskPool pool, T result) {
    }

    /**
     *任务流执行出现错误时完调用
     * @param throwable
     */
    @Override
    public void onError(TaskPool pool, Throwable throwable) {
    }

    /**
     *任务流最后执行
     */
    @Override
    public void onFinally() {
    }
}
