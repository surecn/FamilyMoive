package com.surecn.familymovie.ui.browser;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.surecn.familymovie.R;
import com.surecn.familymovie.ui.base.TVLinearLayoutManager;
import com.surecn.moat.base.BaseDialog;

public class Setting1Dialog extends BaseDialog {

    private SettingPanel settingPanel;

    private String[] mData;

    public Setting1Dialog(@NonNull Context context) {
        super(context);
    }

    private View.OnClickListener mListener;

    @Override
    public int layout() {
        return R.layout.dialog_setting;
    }

    public void setOnItemClickListener(View.OnClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.RIGHT;
        window.setAttributes(wlp);

        settingPanel = findViewById(R.id.root);

//        mRecyclerView.setLayoutManager(new TVLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//        mRecyclerView.setAdapter(new SettingAdapter());
    }

    public void setData(String [] data) {
        mData = data;
    }

    private class SettingAdapter extends RecyclerView.Adapter<SettingViewHolder> {

        @NonNull
        @Override
        public SettingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting, parent, false);
            if (mListener != null) {
                view.setOnClickListener(mListener);
            }
            return new SettingViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SettingViewHolder holder, int position) {
            holder.setData(mData[position]);
        }

        @Override
        public int getItemCount() {
            return mData != null ? mData.length : 0;
        }
    }

    private static class SettingViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;

        public SettingViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
        }

        private void setData(String text) {
            mTextView.setText(text);
        }

    }




}
