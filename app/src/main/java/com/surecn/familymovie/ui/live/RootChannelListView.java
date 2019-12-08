package com.surecn.familymovie.ui.live;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.surecn.familymovie.ui.base.TvRecyclerView;
import com.surecn.moat.tools.log;

public class RootChannelListView extends TvRecyclerView {
    public RootChannelListView(@NonNull Context context) {
        super(context);
    }

    public RootChannelListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RootChannelListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        log.e("========onKeyDown========");
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        log.e("========onKeyUp========");
        return super.onKeyUp(keyCode, event);
    }
}
