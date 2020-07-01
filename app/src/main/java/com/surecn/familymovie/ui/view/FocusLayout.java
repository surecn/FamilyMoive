package com.surecn.familymovie.ui.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.surecn.familymovie.R;
import com.surecn.moat.tools.log;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by pengyuntao on 16/3/21.
 */
public class FocusLayout extends FrameLayout implements ViewTreeObserver.OnGlobalFocusChangeListener {
    private FrameLayout.LayoutParams mFocusLayoutParams;
    private ImageView mFocusView;

    public FocusLayout(Context context) {
        super(context);
        init(context);
    }

    public FocusLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FocusLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void bindListener(View view) {
        //获取根元素
        View mContainerView = view;//.findViewById(android.R.id.content);
        //得到整个view树的viewTreeObserver
        ViewTreeObserver viewTreeObserver = mContainerView.getViewTreeObserver();
        //给观察者设置焦点变化监听
        viewTreeObserver.addOnGlobalFocusChangeListener(this);
    }

    private void init(Context context) {
        this.mFocusLayoutParams = new FrameLayout.LayoutParams(0, 0);
        this.mFocusView = new ImageView(context);
        this.mFocusView.setBackgroundResource(R.mipmap.hover);
        setFocusable(false);
        setFocusableInTouchMode(false);
        setClickable(false);
        this.mFocusView.setFocusable(false);
        this.mFocusView.setFocusableInTouchMode(false);
        this.mFocusView.setClickable(false);
        this.addView(this.mFocusView, this.mFocusLayoutParams);
    }

    @Override
    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
        log.d("focusChange >> old:" + oldFocus + "   new:" + newFocus);
        if (newFocus == null || newFocus instanceof RecyclerView) {
            setVisibility(View.GONE);
            return;
        }
        setVisibility(View.VISIBLE);
        Rect viewRect = new Rect();
        newFocus.getGlobalVisibleRect(viewRect);
        correctLocation(viewRect);

        this.setFocusLocation(
                viewRect.left - this.mFocusView.getPaddingLeft(),
                viewRect.top - this.mFocusView.getPaddingTop(),
                viewRect.right + this.mFocusView.getPaddingRight(),
                viewRect.bottom + this.mFocusView.getPaddingBottom());
    }

    /**
     * 由于getGlobalVisibleRect获取的位置是相对于全屏的,所以需要减去FocusLayout本身的左与上距离,变成相对于FocusLayout的
     * @param rect
     */
    private void correctLocation(Rect rect) {
        Rect layoutRect = new Rect();
        this.getGlobalVisibleRect(layoutRect);
        rect.left -= layoutRect.left;
        rect.right -= layoutRect.left;
        rect.top -= layoutRect.top;
        rect.bottom -= layoutRect.top;
    }

    /**
     * 设置焦点view的位置,计算焦点框的大小
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    protected void setFocusLocation(int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;

        this.mFocusLayoutParams.width = width;
        this.mFocusLayoutParams.height = height;
        this.mFocusLayoutParams.leftMargin = left;
        this.mFocusLayoutParams.topMargin = top;
        this.mFocusView.setLayoutParams(this.mFocusLayoutParams);
    }
}
