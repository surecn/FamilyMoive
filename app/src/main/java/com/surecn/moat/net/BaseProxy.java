package com.surecn.moat.net;

import android.content.Context;

import com.surecn.moat.core.TaskSchedule;
import com.surecn.moat.core.task.UITask;
import com.surecn.moat.core.task.Task;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

/**
 * Created by surecn on 15/8/5.
 */
/*package*/public abstract class BaseProxy implements InvocationHandler {

    private Context mContext;

    public BaseProxy(Context context) {
        this.mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    /*package*/<T> T create(Class<T> cls) {
        return (T) Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }
        method.setAccessible(true);

        MethodInfo info = new MethodInfo();
        info.mMethod = method;
        info.mArgs = args;
        info.mReturnType = method.getGenericReturnType();
        info.mAnnotations = method.getAnnotations();
        Type type = method.getGenericReturnType();
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            Class returnType = (Class)pType.getRawType();
            if (returnType.getName().equals(TaskSchedule.class.getName())) {
                info.mReturnType = pType.getActualTypeArguments()[0];
                return TaskSupport.createWork(this, info);
            } else if (returnType.getName().equals(Task.class.getName())) {
                info.mReturnType = pType.getActualTypeArguments()[0];
                return TaskSupport.createTask(this, info);
            } else if (returnType.getName().equals(UITask.class.getName())) {
                throw new Exception("UITask not Support");
            }
        }
        return handler(info);
    }

    public abstract Object handler(MethodInfo methodInfo);

}
