package com.surecn.moat.net;

import com.surecn.moat.core.Schedule;
import com.surecn.moat.core.task.Task;
import com.surecn.moat.core.TaskSchedule;

/**
 * Created by surecn on 15/8/5.
 */
public class TaskSupport {

    /*package*/static TaskSchedule createWork(BaseProxy baseProxy, final MethodInfo methodInfo) {
        return Schedule.linear(createTask(baseProxy, methodInfo));
    }

    /*package*/static Task createTask(BaseProxy baseProxy, final MethodInfo methodInfo) {
        return new HttpTask(baseProxy, methodInfo);
    }

    private static class HttpTask implements Task {

        protected MethodInfo mMethodInfo;

        protected BaseProxy mBaseProxy;

        public HttpTask(BaseProxy baseProxy, MethodInfo methodInfo) {
            mMethodInfo = methodInfo;
            this.mBaseProxy = baseProxy;
        }

        @Override
        public void run(TaskSchedule work, Object result) {
            Object data = mBaseProxy.handler(mMethodInfo);
            work.sendNext(data);
        }
    }

}
