package com.surecn.moat.rest;

import com.surecn.moat.core.task.UITask;
import com.surecn.moat.core.task.Task;
import com.surecn.moat.core.TaskQueue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

/**
 * Created by surecn on 15/8/5.
 */
/*package*/class RestProxy implements InvocationHandler {

    private RestAdapter mAdapter;

    private String mUrl;

    /*package*/RestProxy(RestAdapter adapter, String url) {
        mAdapter = adapter;
        mUrl = url;
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
        info.mUrl = mUrl;
        info.mAnnotations = method.getAnnotations();
        Type type = method.getGenericReturnType();
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            Class returnType = (Class)pType.getRawType();
            if (returnType.getName().equals(TaskQueue.class.getName())) {
                info.mReturnType = pType.getActualTypeArguments()[0];
                return TaskSupport.createWork(mAdapter, info);
            } else if (returnType.getName().equals(Task.class.getName())) {
                info.mReturnType = pType.getActualTypeArguments()[0];
                return TaskSupport.createTask(mAdapter, info);
            } else if (returnType.getName().equals(UITask.class.getName())) {
                throw new Exception("UITask not Support");
            }
        }
        return HttpBehavior.behavior(mAdapter, info);
    }

    /*package*/String getUrl() {
        return mUrl;
    }

    /*package*/void setUrl(String url) {
        this.mUrl = url;
    }

}
