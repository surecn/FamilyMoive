package com.surecn.moat.core;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-03
 * Time: 09:48
 */
public class LinearExecutor extends TaskExecutor {

    private static LinearExecutor sTaskExecutor;

    public static LinearExecutor getTaskExecutor() {
        if (sTaskExecutor == null) {
            sTaskExecutor = new LinearExecutor();
        }
        return sTaskExecutor;
    }

    public void executeOnMain() {

    }

    public void execute() {

    }

}
