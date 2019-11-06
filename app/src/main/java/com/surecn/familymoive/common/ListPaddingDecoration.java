package com.surecn.familymoive.common;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import com.surecn.familymoive.R;

import androidx.recyclerview.widget.RecyclerView;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-02
 * Time: 13:16
 */
public class ListPaddingDecoration extends RecyclerView.ItemDecoration {

    private int dividerHeight;

    public ListPaddingDecoration(int height) {
        dividerHeight = height;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.right = dividerHeight;//类似加了一个bottom padding
    }
}