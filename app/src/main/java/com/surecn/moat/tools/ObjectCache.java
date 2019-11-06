package com.surecn.moat.tools;

import java.util.ArrayList;

/**
 * User: surecn(surecn@163.com)
 * Date: 2016-01-13
 * Time: 16:11
 */
public abstract class ObjectCache<T> {

    private int mModCount;

    private ArrayList<T> mListObjects = new ArrayList<T>();

    public ObjectCache(int modCount) {
        mModCount = modCount;
    }

    public T obtainObject() {
        if (mListObjects.size() > 0) {
            return mListObjects.remove(0);
        } else {
            return newInstance();
        }
    }

    public void back(T object) {
        if (mListObjects.size() < mModCount) {
            mListObjects.add(object);
        }
    }

    public abstract T newInstance();
}
