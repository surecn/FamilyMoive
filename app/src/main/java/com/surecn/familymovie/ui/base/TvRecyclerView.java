package com.surecn.familymovie.ui.base;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-10
 * Time: 14:37
 */
public class TvRecyclerView extends RecyclerView {


    public TvRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public TvRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public TvRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //setPreserveFocusAfterLayout(false);
    }

    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        //super.onRequestFocusInDescendants(direction, previouslyFocusedRect);
        return false;
    }

    @Override
    public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
        return super.requestChildRectangleOnScreen(child, rect, true);
    }

}
