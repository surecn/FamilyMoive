package com.surecn.familymovie.ui.base;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-10
 * Time: 09:34
 */
public class TVLinearLayoutManager extends LinearLayoutManager {

    public TVLinearLayoutManager(Context context) {
        super(context);
    }

    public TVLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public TVLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean requestChildRectangleOnScreen(@NonNull RecyclerView parent, @NonNull View child, @NonNull Rect rect, boolean immediate, boolean focusedChildVisible) {
        return super.requestChildRectangleOnScreen(parent, child, rect, true, focusedChildVisible);
    }

}
