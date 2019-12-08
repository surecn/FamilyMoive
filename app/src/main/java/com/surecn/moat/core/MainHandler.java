package com.surecn.moat.core;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * User: surecn(surecn@163.com)
 * Date: 2018-11-29
 * Time: 13:43
 */
class MainHandler extends Handler {

    /*package*/final static int MSG_DELAY_EXECUTE = 1000;

    /*package*/final static int MSG_DELAY_NOTIFY = 1001;

    /*package*/final static int MSG_EXECUTE = 1002;

    /*package*/final static int MSG_EXECUTE_ERROR = 1003;

    private ErrorInterceptor mErrorInterceptor;

    private static MainHandler sMainHandler;

    public static MainHandler getInstance() {
        if (sMainHandler == null) {
            sMainHandler = new MainHandler();
        }
        return sMainHandler;
    }

    private MainHandler() {
        super(Looper.getMainLooper());
    }

    public void setErrorInterceptor(ErrorInterceptor errorInterceptor) {
        this.mErrorInterceptor = errorInterceptor;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_DELAY_EXECUTE: {
                Object[] obj = (Object[]) msg.obj;
                TaskExecutor taskExecutor = (TaskExecutor) obj[0];
                TaskSchedule taskSchedule = (TaskSchedule) obj[1];
                execute(taskExecutor, taskSchedule);
                break;
            }
            case MSG_DELAY_NOTIFY: {
                Object[] obj = (Object[]) msg.obj;
                TaskExecutor taskExecutor = (TaskExecutor) obj[0];
                TaskSchedule taskSchedule = (TaskSchedule) obj[1];
                taskSchedule.current().setDelayMillis(0);
                taskExecutor.execute(taskSchedule);
                break;
            }
            case MSG_EXECUTE: {
                Object[] obj = (Object[]) msg.obj;
                TaskExecutor taskExecutor = (TaskExecutor) obj[0];
                TaskSchedule taskSchedule = (TaskSchedule) obj[1];
                execute(taskExecutor, taskSchedule);
                break;
            }
            case MSG_EXECUTE_ERROR: {
                Object[] obj = (Object[]) msg.obj;
                TaskExecutor taskExecutor = (TaskExecutor) obj[0];
                TaskSchedule taskSchedule = (TaskSchedule) obj[1];
                Throwable throwable = (Throwable) obj[2];
                execute(taskExecutor, taskSchedule, throwable);
                break;
            }
            default:
                break;
        }
    }

    private void execute(TaskExecutor taskExecutor, TaskSchedule taskSchedule) {
        try {
            TaskRecord taskRecord = taskSchedule.current();
            if (taskRecord == null) {
                return;
            }
            taskRecord.getTask().run(taskSchedule, taskRecord.mObject);
            taskSchedule.setCurrentState(taskRecord.getNextState());
            if (!taskSchedule.hasNext()) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        taskExecutor.executeNext(taskSchedule);
    }

    private void execute(TaskExecutor taskExecutor, TaskSchedule taskSchedule, Throwable throwable) {
        try {
            if (mErrorInterceptor != null) {
                mErrorInterceptor.interceptor(throwable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void performDelayExecute(TaskExecutor taskExecutor, TaskSchedule taskSchedule, long delay) {
        Message message = obtainMessage(MSG_DELAY_EXECUTE);
        message.obj = new Object[]{taskExecutor, taskSchedule};
        message.setTarget(this);
        sendMessageDelayed(message, delay);
    }

    public void performDelayNotify(TaskExecutor taskExecutor, TaskSchedule taskSchedule, long delay) {
        Message message = obtainMessage(MSG_DELAY_NOTIFY);
        message.obj = new Object[]{taskExecutor, taskSchedule};
        message.setTarget(this);
        sendMessageDelayed(message, delay);
    }

    public void performErrorTask(TaskExecutor taskExecutor, TaskSchedule taskSchedule, Throwable throwable) {
        Message message = obtainMessage(MSG_EXECUTE_ERROR);
        message.obj = new Object[]{taskExecutor, taskSchedule, throwable};
        message.setTarget(this);
        sendMessage(message);
    }



}
