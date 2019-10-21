package com.surecn.moat.base;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.surecn.moat.base.inject.Injector;

/**
 * Created by surecn on 15/5/21.
 */
public class BaseFragment extends Fragment implements IFindView {

    private View mContentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = super.onCreateView(inflater, container, savedInstanceState);
        Injector.injectView(this, this);
        Injector.injectClick(this, this);
        return mContentView;
    }

    @Override
    public View findViewById(int id) {
        return mContentView.findViewById(id);
    }
}
