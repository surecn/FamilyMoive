package com.surecn.moat.base;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.surecn.familymoive.R;
import com.surecn.familymoive.ui.view.FocusLayout;
import com.surecn.moat.base.inject.Injector;


/**
 * Created by surecn on 15/5/21.
 */
public class BaseDialog extends Dialog {

    private FocusLayout mFocusLayout;


    public BaseDialog(@NonNull Context context) {
        super(context, R.style.CustomDialog);
    }

    public int layout() {
        return 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout());
        mFocusLayout = new FocusLayout(getContext());
        bindListener();//绑定焦点变化事件
        addContentView(mFocusLayout,
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));//添加焦点层
    }

    private void bindListener() {
        //获取根元素
        View mContainerView = this.getWindow().getDecorView();//.findViewById(android.R.id.content);
        //得到整个view树的viewTreeObserver
        ViewTreeObserver viewTreeObserver = mContainerView.getViewTreeObserver();
        //给观察者设置焦点变化监听
        viewTreeObserver.addOnGlobalFocusChangeListener(mFocusLayout);
    }
}
