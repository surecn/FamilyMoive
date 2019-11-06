package com.surecn.familymoive.common;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import com.surecn.familymoive.R;

import androidx.recyclerview.widget.RecyclerView;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-05
 * Time: 11:30
 */
public class GridPaddingDecoration extends RecyclerView.ItemDecoration {

    private int left;
    private int right;
    private int top;
    private int bottom;

    public GridPaddingDecoration(Context context, int left, int right, int top, int bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.top = top;
        outRect.bottom = bottom;
        outRect.left = left;
        outRect.right = right;
    }
}