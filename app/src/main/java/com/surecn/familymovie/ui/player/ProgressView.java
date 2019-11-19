package com.surecn.familymovie.ui.player;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.surecn.familymovie.R;

import androidx.annotation.Nullable;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-10-15
 * Time: 12:46
 */
public class ProgressView extends View {

    private long value = 0;

    private long maxValue = 0;

    private RectF rectLeft = new RectF();
    private RectF rectLeftClip = new RectF();

    private RectF rectRight = new RectF();
    private RectF rectRightClip = new RectF();

    private Paint mPaintCurrent = new Paint();

    private Paint mPaint = new Paint();

    private float radis;

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setValue(long value) {
        if (maxValue <= 0) {
            return;
        }
        this.value = value;

        rectLeftClip.right = getWidth() * value * 100 / maxValue / 100;
        rectLeft.right = (rectLeftClip.right < radis ? radis : rectLeftClip.right) + radis;
        rectRightClip.left = rectLeftClip.right;
        rectRight.left = (rectLeftClip.right < radis ?  radis : rectLeftClip.right) - radis;
        super.invalidate();
    }

    public long getValue() {
        return this.value;
    }

    public void setMaxValue(long maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            float contentHeight = getResources().getDimension(R.dimen.video_progress_height);
            radis = contentHeight / 2;
            int marginTop = (int) (getHeight() - contentHeight) / 2;
            rectLeft.set(0, marginTop,  0, marginTop + contentHeight);
            rectLeftClip = new RectF(rectLeft);
            rectRight.set(0, marginTop,  getWidth(), marginTop + contentHeight);
            rectRightClip = new RectF(rectRight);
            mPaintCurrent.setColor(Color.parseColor("#0A5FFE"));
            mPaint.setColor(Color.parseColor("#1A1A1A"));
            Log.e("=", "left:" + left + " right : " + right + "  " + left + "  bo:" + bottom);
        }
        setValue(value);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (maxValue <= 0) {
            return;
        }
        canvas.save();
        canvas.clipRect(rectLeftClip);
        canvas.drawRoundRect(rectLeft, radis, radis, mPaintCurrent);
        canvas.restore();
        canvas.save();
        canvas.clipRect(rectRightClip);
        canvas.drawRoundRect(rectRight, radis, radis, mPaint);
        canvas.restore();
    }
}
