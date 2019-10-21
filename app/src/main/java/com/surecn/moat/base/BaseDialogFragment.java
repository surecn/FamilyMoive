package com.surecn.moat.base;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.surecn.moat.base.inject.Injector;


/**
 * Created by surecn on 15/5/21.
 */
public class BaseDialogFragment extends DialogFragment implements IFindView {

    protected View mContentView;

    public int layout() {
        return 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = super.onCreateView(inflater, container, savedInstanceState);
        int layoutRes = layout();
        if (layoutRes > 0) {
            mContentView = inflater.inflate(layoutRes, container, false);
        }
        return mContentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mContentView = view;
        super.onViewCreated(view, savedInstanceState);
        Injector.injectView(this, this);
        Injector.injectClick(this, this);
    }

    @Override
    public View findViewById(int id) {
        return mContentView.findViewById(id);
    }
}
