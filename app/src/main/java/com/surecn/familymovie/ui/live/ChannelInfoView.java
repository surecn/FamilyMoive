package com.surecn.familymovie.ui.live;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.surecn.familymovie.R;
import com.surecn.familymovie.domain.Channel;
import com.surecn.familymovie.domain.ChannelProgram;

import java.text.SimpleDateFormat;
import java.util.Map;

public class ChannelInfoView extends LinearLayout {

    private TextView mTxtChannelId;

    private TextView mTxtChannelName;

    private TextView mTxtCurrentProgram;

    private TextView mTxtNextProgramTime;

    private TextView mTxtNextProgram;

    private View mViewNextProgram;

    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("HH:mm");

    public ChannelInfoView(Context context) {
        super(context);
    }

    public ChannelInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChannelInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTxtChannelId = findViewById(R.id.channelId);
        mTxtChannelName = findViewById(R.id.channelName);
        mTxtCurrentProgram = findViewById(R.id.currentProgram);
        mTxtNextProgramTime = findViewById(R.id.nextProgramTime);
        mTxtNextProgram = findViewById(R.id.nextProgramText);
        mViewNextProgram = findViewById(R.id.nextProgram);
    }

    public void show(Channel channel) {
        mTxtChannelId.setText(String.format("%04d", channel.getId()));
        mTxtChannelName.setText(String.valueOf(channel.getTitle()));
        if (channel.getProgramMaps() == null) {
            return;
        }
        Map.Entry<Long, ChannelProgram> entry = channel.getProgramMaps().floorEntry(System.currentTimeMillis());
        if (entry != null) {
            mViewNextProgram.setVisibility(View.VISIBLE);
            mTxtCurrentProgram.setText(entry.getValue().getTitle());

            entry = channel.getProgramMaps().higherEntry(System.currentTimeMillis());
            if (entry != null) {
                mTxtNextProgramTime.setText(mSimpleDateFormat.format(entry.getValue().getStartTime()));
                mTxtNextProgram.setText("即将播放:" + entry.getValue().getTitle());
            } else {
                mTxtNextProgramTime.setText(null);
                mTxtNextProgram.setText(null);
            }
        } else {
            mViewNextProgram.setVisibility(View.GONE);
            mTxtCurrentProgram.setText(null);
        }
    }
}
