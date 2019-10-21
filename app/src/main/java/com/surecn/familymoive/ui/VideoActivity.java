package com.surecn.familymoive.ui;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.surecn.familymoive.R;
import com.surecn.familymoive.utils.DateUtils;
import com.surecn.familymoive.utils.WindowUtils;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;
import java.util.ArrayList;
import java.util.Date;

public class VideoActivity extends BaseActivity implements View.OnClickListener, MediaPlayer.EventListener {
    private static final boolean USE_TEXTURE_VIEW = false;
    private static final boolean ENABLE_SUBTITLES = true;

    private VLCVideoLayout mVideoLayout = null;

    private TextView mViewTitle;
    private TextView mViewTime;
    private TextView mViewProgressCurrent;
    private TextView mViewProgressTotal;

    private LibVLC mLibVLC = null;
    private MediaPlayer mMediaPlayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.hideNavigationBar(this, true);
        setContentView(R.layout.activity_video);

        final ArrayList<String> args = new ArrayList<>();
        args.add("-vvv");
        mLibVLC = new LibVLC(this, args);
        mMediaPlayer = new MediaPlayer(mLibVLC);

        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPlayer.release();
        mLibVLC.release();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mMediaPlayer.attachViews(mVideoLayout, null, ENABLE_SUBTITLES, USE_TEXTURE_VIEW);

        final Media media = new Media(mLibVLC, Uri.parse(getIntent().getStringExtra("url")));
        mMediaPlayer.setMedia(media);
        mMediaPlayer.updateViewpoint(0, 0, 90, 0,true);
        media.release();

        mMediaPlayer.play();
        mMediaPlayer.setEventListener(this);
        initData();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mMediaPlayer.stop();
        mMediaPlayer.detachViews();
    }

    private void initView() {
        mVideoLayout = findViewById(R.id.video_layout);
        mViewTitle = findViewById(R.id.title);
        mViewTime = findViewById(R.id.time);
        mViewProgressCurrent = findViewById(R.id.progress_current);
        mViewProgressTotal = findViewById(R.id.progress_total);
        findViewById(R.id.back).setOnClickListener(this);
    }

    private void initData() {
        mViewTitle.setText("正在播放:" + getIntent().getStringExtra("url"));
        mViewTime.setText(DateUtils.toTimeLength(new Date()));
        mViewProgressTotal.setText(DateUtils.toTimeLength(mMediaPlayer.getLength()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    public void onEvent(MediaPlayer.Event event) {
        mViewProgressCurrent.setText(DateUtils.toTimeLength(mMediaPlayer.getTime()));
        mViewProgressTotal.setText(DateUtils.toTimeLength(mMediaPlayer.getLength()));

        Log.e("event:", "event." + event.type + "  " + event.getTimeChanged());

    }
}
