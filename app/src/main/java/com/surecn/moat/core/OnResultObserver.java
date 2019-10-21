package com.surecn.moat.core;

/**
 * User: surecn(surecn@163.com)
 * Date: 2015-08-30
 * Time: 14:02
 */
public interface OnResultObserver<T> {

    /**
     * 任务流正常执行完成时调用
     * @param result
     */
    public void onComplete(TaskPool pool, T result);

    /**
     *任务流执行出现错误时完调用
     * @param throwable
     */
    public void onError(TaskPool pool, Throwable throwable);
}
