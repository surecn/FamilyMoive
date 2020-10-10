package com.surecn.familymovie.ui.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import com.surecn.familymovie.BuildConfig;
import com.surecn.familymovie.R;
import com.surecn.familymovie.Setting;
import com.surecn.familymovie.UserTrack;
import com.surecn.familymovie.common.FileManager;
import com.surecn.familymovie.common.SmbManager;
import com.surecn.familymovie.common.subtitle.SubTitle;
import com.surecn.familymovie.common.player.media.IRenderView;
import com.surecn.familymovie.common.player.media.IjkVideoView;
import com.surecn.familymovie.data.HistoryModel;
import com.surecn.familymovie.data.HttpAdapter;
import com.surecn.familymovie.data.ServerModel;
import com.surecn.familymovie.domain.FileItem;
import com.surecn.familymovie.domain.SubDetailItem;
import com.surecn.familymovie.domain.SubTitleItem;
import com.surecn.familymovie.ui.base.BaseActivity;
import com.surecn.familymovie.utils.DateUtils;
import com.surecn.familymovie.utils.UriUtil;
import com.surecn.moat.core.Schedule;
import com.surecn.moat.core.TaskSchedule;
import com.surecn.moat.core.task.Task;
import com.surecn.moat.core.task.UITask;
import com.surecn.moat.tools.log;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import androidx.constraintlayout.widget.ConstraintLayout;

import jcifs.CIFSContext;
import jcifs.context.SingletonContext;
import jcifs.smb.NtlmPasswordAuthenticator;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaMeta;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;

public class VideoActivity extends BaseActivity implements View.OnClickListener {

    private static final int MSG_HIDE_ALL_LAYER = 1000;
    private static final int MSG_UPDATE_TIME = 1001;

    private final static int STATUS_ERROR = -2;
    private final static int STATUS_LOADING = -1;
    private final static int STATUS_NORMAL = 0;
    private final static int STATUS_SHOW_PLAYCONTROLLER = 1;
    private final static int STATUS_SHOW_PLAYLIST = 2;
    private final static int STATUS_SHOW_SETTING = 3;
    private final static int STATUS_LIST_SUBTITLE = 4;
    private final static int STATUS_LIST_AUDIO = 5;

    private final static String KEY_AUDIO = "audio";
    private final static String KEY_SUBTITLE = "subtitle";
    private final static String KEY_SCALE= "scale";

    private IjkVideoView mVideoView;

    private TextView mViewSubTitle;
    private TextView mViewTitle;
    private TextView mViewTime;
    private TextView mViewProgressCurrent;
    private TextView mViewProgressTotal;
    private ProgressView mViewProgress;

    private String mRoot;
    private String mUrl;
    private FileItem mFileItem;
    private HistoryModel mHistoryModel;

    private TextView mSelectTitleView;

    private PlayListView mPlayListView;
    private PlaySettingPanel mPlaySettingPanel;
    private SettingListPanel mSettingListPanel;
    private FrameLayout mViewPlayLoading;
    private ConstraintLayout mViewPlayController;
    private TextView mViewListSelect;

    private int mStatus;

    private TableLayout mHudView;

    private boolean mBackPressed;

    private int mShortPress = 0;

    private long mLastPress = 0;

    private long mInitPosition = 0;

    private int mSelectSubtitleIndex = -1;
    private int mSelectAudioIndex;

    private List<FileItem> mList;

    private List<SettingListPanel.TrackItem> mAudioTrack;

    private List<SettingListPanel.TrackItem> mSubTitleTrack;

    private SubTitle mSubTitle;

    private long mSeekPosition = -1;

    private int mSeekStep = 30000;

    private static final int FLING_MIN_DISTANCE = 20;// 移动最小距离
    private static final int FLING_MIN_VELOCITY = 200;// 移动最大速度

    //广播的注册，其中Intent.ACTION_TIME_CHANGED代表时间设置变化的时候会发出该广播
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.ACTION_TIME_TICK.equals(intent.getAction())){
                if (mViewTime != null) {
                    mViewTime.setText(DateUtils.toTime(new Date()));
                    saveHistory();
                }
            }
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_HIDE_ALL_LAYER:
                    hideAll();
                    break;
                case MSG_UPDATE_TIME:
                    updateTime();
                    sendEmptyMessageDelayed(MSG_UPDATE_TIME, 1000);
                    break;
            }
        }
    };

    private GestureDetector mygesture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        initView();

        mUrl = getIntent().getStringExtra("url");
        mRoot = getIntent().getStringExtra("root");
        mInitPosition = getIntent().getLongExtra("position", 0);
        mHistoryModel = new HistoryModel(this);
        if (savedInstanceState != null) {
            mUrl = savedInstanceState.getString("url");
            mRoot = getIntent().getStringExtra("root");
            mInitPosition = savedInstanceState.getLong("position");
        }

        mFileItem = new ServerModel(this).getByPath(mRoot);

        UserTrack.mark(UserTrack.VIDEO_PLAY);

    }

    @Override
    protected void onStart() {
        super.onStart();
        startVideo(mUrl, mInitPosition);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        mHandler = null;
    }

    @Override
    public void onBackPressed() {
        if (mStatus != STATUS_NORMAL) {
            hideAll();
            return;
        }
        mBackPressed = true;
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mBackPressed || !mVideoView.isBackgroundPlayEnabled()) {
            mVideoView.stopPlayback();
            mVideoView.release(true);
            mVideoView.stopBackgroundPlay();
        } else {
            mVideoView.enterBackground();
        }
        IjkMediaPlayer.native_profileEnd();
        saveHistory();
    }

    public void saveHistory() {
        if (mVideoView.getDuration() > 0) {
            mHistoryModel.save(mUrl, mVideoView.getCurrentPosition(), mVideoView.getDuration(), mFileItem != null ? mFileItem.path : "");
        }
    }

    private void initView() {
        mViewListSelect = findViewById(R.id.play_list_select);
        mPlayListView = findViewById(R.id.play_list);
        mViewPlayController = findViewById(R.id.play_controller);
        mViewPlayLoading = findViewById(R.id.play_loading);
        mPlaySettingPanel = findViewById(R.id.setting);
        mSettingListPanel = findViewById(R.id.setting_list_panel);
        mSelectTitleView = findViewById(R.id.play_list_select);
        mHudView = findViewById(R.id.hud_view);
        mViewSubTitle = findViewById(R.id.subtitle_view);
        mVideoView = findViewById(R.id.video_view);
        mViewTitle = findViewById(R.id.title);
        mViewTime = findViewById(R.id.time);
        mViewProgressCurrent = findViewById(R.id.progress_current);
        mViewProgressTotal = findViewById(R.id.progress_total);
        mViewProgress = findViewById(R.id.progress_ui);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.video_view).setOnClickListener(this);
        findViewById(R.id.video_view).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showSetting();
                return false;
            }
        });

        mSubTitle = new SubTitle(mViewSubTitle);

        if (BuildConfig.DEBUG) {
            mHudView = (TableLayout) LayoutInflater.from(this).inflate(R.layout.ijkplayer_debug_panel, null);
            mVideoView.setHudView(mHudView);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(broadcastReceiver, filter);

        mygesture = new GestureDetector(this, new GestureDetector.OnGestureListener() {

            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1.getY() - e2.getY() > FLING_MIN_DISTANCE){
//                     && Math.abs(velocityX) > FLING_MIN_VELOCITY) {

                } else if (e2.getY() - e1.getY() > FLING_MIN_DISTANCE
                        && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
                    if (mStatus == STATUS_NORMAL
                            || mStatus == STATUS_SHOW_PLAYCONTROLLER) {
                        showPlayList();
                    }
                } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE) {
                    if (canSeekVideo()) {
                        longEventHandler();
                        if (mShortPress > 1) {
                            mViewProgress.setValue(mViewProgress.getValue() - mSeekStep * 2);
                            mSeekPosition = -1;
                        } else {
                            int step = mSeekStep;
                            if (mLastPress > 0 && (System.currentTimeMillis() - mLastPress < 1000)) {
                                step = mSeekStep * 2;
                            }
                            mSeekPosition = mVideoView.getCurrentPosition() - step;
                            mViewProgress.setValue(mSeekPosition);
                        }
                        mLastPress = System.currentTimeMillis();
                        showPlayController();
                    }
                } else if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE) {
                    if (canSeekVideo()) {
                        longEventHandler();
                        if (mShortPress > 1) {
                            mViewProgress.setValue(mViewProgress.getValue() + mSeekStep * 2);
                            mSeekPosition = -1;
                        } else {
                            int step = mSeekStep;
                            if (mLastPress > 0 && (System.currentTimeMillis() - mLastPress < 1000)) {
                                step = mSeekStep * 2;
                            }
                            mSeekPosition = mVideoView.getCurrentPosition() + step;
                            mViewProgress.setValue(mSeekPosition);
                        }
                        mLastPress = System.currentTimeMillis();
                        showPlayController();
                    }
                }
                return false;
            }
        });
    }

    public void setPlayModel(int model) {
        mVideoView.setPlayerModel(model);
        long position = mInitPosition;
        if (mVideoView != null) {
            position = mVideoView.getCurrentPosition();
        }
        startVideo(mUrl, position);
    }

    public int getPlayModel() {
        return mVideoView.getPlayerModel();
    }

    public void startVideo(String url, long initPosition) {
        showPlayLoading();
        this.mUrl = url;
        this.mInitPosition = initPosition;
        mVideoView.setAspectRatio(IRenderView.AR_ASPECT_FIT_PARENT);
        try {
            if (mUrl.startsWith("smb://")) {
                String auth = "";
                if (mFileItem != null && !TextUtils.isEmpty(mFileItem.server) && !TextUtils.isEmpty(mFileItem.user)) {
                    auth = "?server=" + mFileItem.server + "&user=" + mFileItem.user + "&pass=" + mFileItem.pass;
                }
                mVideoView.setVideoPath("http://127.0.0.1:12315/smb" + URLEncoder.encode(mUrl.substring(5)) + auth);
            } else {
                mVideoView.setVideoPath(mUrl);
            }
        } catch (Exception e) {
            showToast("出错了");
            finish();
        }
        mVideoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                initData(iMediaPlayer);
            }
        });
        mVideoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                showPlayError();
                return false;
            }
        });
        mVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                String path = mUrl;
                if(getSharedPreferences("video_setting", Context.MODE_PRIVATE).getInt("order", 0) == 0) {
                    path = mPlayListView.getNextPath();
                }
                if (path != null) {
                    startVideo(path, 0);
                }
            }
        });
        mVideoView.start();
        if (mSubTitle != null) {
            mSubTitle.reset();
            mViewSubTitle.setText("");
        }
    }

    private void initData(IMediaPlayer iMediaPlayer) {
        hidePlayStatus();
        showPlayController();
        mViewTitle.setText("正在播放:" + mUrl);
        mViewTime.setText(DateUtils.toTime(new Date()));
        mViewProgressTotal.setText(DateUtils.toTimeLength(mVideoView.getDuration()));
        mViewProgress.setMaxValue(mVideoView.getDuration());
        mSeekStep = mVideoView.getDuration() / 120;
        if (mInitPosition > 0) {
            mVideoView.seekTo(mInitPosition);
        }
        mSubTitle.bind(iMediaPlayer);
        initSettingListData();

        saveHistory();

        Schedule.linear(new Task() {
            @Override
            public void run(TaskSchedule work, Object result) {
                if (mUrl.startsWith("smb://")) {
                    SmbFile smbFile = null;
                    try {
                        NtlmPasswordAuthenticator ntlmPasswordAuthenticator = null;
                        CIFSContext context = SingletonContext.getInstance().withAnonymousCredentials();
                        if (mFileItem != null && !TextUtils.isEmpty(mFileItem.server) && !TextUtils.isEmpty(mFileItem.user)) {
                            ntlmPasswordAuthenticator = new NtlmPasswordAuthenticator(mFileItem.server, mFileItem.user, mFileItem.pass);
                            context = SingletonContext.getInstance().withCredentials(ntlmPasswordAuthenticator);
                        }
                        smbFile = new SmbFile(mUrl, context);
                        mList = SmbManager.listFile(smbFile.getParent(), 1, ntlmPasswordAuthenticator);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (SmbException e) {
                        e.printStackTrace();
                    }
                } else {
                    File file = new File(mUrl);
                    mList = FileManager.listFile(mUrl, 1);
                }
            }
        }).next(new UITask() {
            @Override
            public void run(TaskSchedule taskSchedule, Object result) {
                int index = 0;
                for (int i = 0, len = mList.size(); i < len; i++) {
                    FileItem file = mList.get(i);
                    if (file.path.equals(mUrl)) {
                        index = i;
                    }
                }
                mPlayListView.setData(VideoActivity.this, mList, mSelectTitleView, index);
            }
        }).start();

        iMediaPlayer.setOnTimedTextListener(new IMediaPlayer.OnTimedTextListener() {
            @Override
            public void onTimedText(IMediaPlayer iMediaPlayer, IjkTimedText ijkTimedText) {
                SettingListPanel.TrackItem trackItem = null;
                if (mSubTitleTrack != null)
                    trackItem = mSubTitleTrack.get(mSelectSubtitleIndex);
                if (ijkTimedText != null && trackItem != null && trackItem.tag == 0) {
                    log.d(ijkTimedText.getText());
                    mViewSubTitle.setText(ijkTimedText.getText());
                }
            }
        });

        iMediaPlayer.setOnSeekCompleteListener(new IMediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(IMediaPlayer iMediaPlayer) {
                mSubTitle.update();
                synchronized (VideoActivity.this) {
                    if (mSeekPosition >= 0) {
                        mVideoView.seekTo(mSeekPosition);
                        mSeekPosition = -1;
                    }
                }
            }
        });
        iMediaPlayer.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
                if (i == IMediaPlayer.MEDIA_INFO_AUDIO_DECODED_START) {
                    mVideoView.seekTo(mVideoView.getCurrentPosition() + 1);
                }
                return false;
            }
        });
        mHandler.removeMessages(MSG_UPDATE_TIME);
        mHandler.sendEmptyMessage(MSG_UPDATE_TIME);
    }

    private void initSettingListData() {
        mAudioTrack = new ArrayList<>();
        mSubTitleTrack = new ArrayList<>();
        mSubTitleTrack.add(new SettingListPanel.TrackItem(-1, getString(R.string.video_subtitle_shutdown), "", -1));
        int audioStreamIndex = mVideoView.getSelectedTrack(ITrackInfo.MEDIA_TRACK_TYPE_AUDIO);
        int subtitleStreamIndex = mVideoView.getSelectedTrack(ITrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT);

        mSelectSubtitleIndex = -1;
        mSelectAudioIndex = 0;

        IjkMediaMeta mediaMeta = mVideoView.getMediaPlayer().getMediaInfo().mMeta;
        if (mediaMeta != null && Setting.playModel == 0) {
            ArrayList<IjkMediaMeta.IjkStreamMeta> streams = mediaMeta.mStreams;
            for (IjkMediaMeta.IjkStreamMeta streamMeta : streams) {
                if (IjkMediaMeta.IJKM_KEY_AUDIO_STREAM.equals(streamMeta.mType)) {
                    mAudioTrack.add(new SettingListPanel.TrackItem(streamMeta.mIndex, streamMeta.mLanguage, streamMeta.mLanguage, 0));
                    if (streamMeta.mIndex == audioStreamIndex) {
                        mSelectAudioIndex = mAudioTrack.size() - 1;
                    }
                } else if (IjkMediaMeta.IJKM_KEY_TIMEDTEXT_STREAM.equals(streamMeta.mType)
                    //&& subtitleStreamIndex == streamMeta.mIndex
                ) {
                    mSubTitleTrack.add(new SettingListPanel.TrackItem(streamMeta.mIndex, streamMeta.mLanguage, streamMeta.mLanguage, 0));
                    if (streamMeta.mIndex == subtitleStreamIndex) {
                        mSelectSubtitleIndex = mSubTitleTrack.size() - 1;
                    }
                }
            }
        }
        Schedule.linear(new Task() {
            private String getFileExtension(String url) {
                int index = url.lastIndexOf("?");
                if (index > 0) {
                    url = url.substring(0, index);
                }
                return url.substring(url.lastIndexOf(".") + 1);
            }
            @Override
            public void run(TaskSchedule work, Object result) {
                String subtitle = null;
                if (mUrl.startsWith("smb://")) {
                    subtitle = SmbManager.searchSubtitle(mUrl);
                } else{
                    subtitle = FileManager.searchSubtitle(mUrl);
                }
                if (subtitle != null) {
                    mSubTitleTrack.add(new SettingListPanel.TrackItem(-2, UriUtil.getUriName(subtitle), subtitle, 1));
                    return;
                }
                String cache = mSubTitle.getByVideoPath(mUrl);
                if (cache != null) {
                    mSubTitleTrack.add(new SettingListPanel.TrackItem(-2, UriUtil.getUriName(cache), cache, 1));
                    return;
                }
                List<SubTitleItem> list = HttpAdapter.getSubTitleService().search(UriUtil.getUriSimpleName(mUrl), 1, 1, 0, 1);
                if (list != null) {
                    int vote_score = -1;
                    SubTitleItem item = null;
                    for (SubTitleItem str : list) {
                        if (str.getVote_score() > vote_score) {
                            item = str;
                        }
                    }
                    if (item != null) {
                        List<SubDetailItem> details = HttpAdapter.getSubTitleService().detail(item.getId());
                        if (details != null) {
                            if (details.size() > 3) {
                                details = details.subList(0, 3);
                            }
                            for (SubDetailItem subDetailItem : details) {
                                for (SubDetailItem.SubUrl url : subDetailItem.getFilelist()) {
                                    String extension = getFileExtension(url.getUrl());
                                    if (extension.equalsIgnoreCase("srt") || extension.equalsIgnoreCase("ass") || extension.equalsIgnoreCase("ssa")) {
                                        mSubTitleTrack.add(new SettingListPanel.TrackItem(subDetailItem.getId(), subDetailItem.getNative_name(), url.getUrl(), 2));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }).next(new UITask() {
            @Override
            public void run(TaskSchedule taskSchedule, Object result) {
                if (mSelectSubtitleIndex >= 0) {
                    return;
                }
                for (int i = 0; i < mSubTitleTrack.size(); i++) {
                    SettingListPanel.TrackItem item = mSubTitleTrack.get(i);
                    if (item.tag == 1) {
                        mSelectSubtitleIndex = i;
                        selectStream(1, mSelectSubtitleIndex);
                        return;
                    } else if (item.tag == 2) {
                        mSelectSubtitleIndex = i;
                        selectStream(1, mSelectSubtitleIndex);
                        return;
                    }
                }
            }

        }).start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("url", mUrl);
        outState.putString("root", mRoot);
        if (mVideoView != null) {
            outState.putLong("position", mVideoView.getCurrentPosition());
        } else {
            outState.putLong("position", mInitPosition);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.video_view:
                showPlayStatus();
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                showPlayController();
                break;
        }
        return super.onTouchEvent(event) || mygesture.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (canSeekVideo()) {
                    longEventHandler();
                    if (mShortPress > 1) {
                        mViewProgress.setValue(mViewProgress.getValue() - mSeekStep * 2);
                        mSeekPosition = -1;
                    } else {
                        int step = mSeekStep;
                        if (mLastPress > 0 && (System.currentTimeMillis() - mLastPress < 1000)) {
                            step = mSeekStep * 2;
                        }
                        mSeekPosition = mVideoView.getCurrentPosition() - step;
                        mViewProgress.setValue(mSeekPosition);
                    }
                    mLastPress = System.currentTimeMillis();
                    showPlayController();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (canSeekVideo()) {
                    longEventHandler();
                    if (mShortPress > 1) {
                        mViewProgress.setValue(mViewProgress.getValue() + mSeekStep * 2);
                        mSeekPosition = -1;
                    } else {
                        int step = mSeekStep;
                        if (mLastPress > 0 && (System.currentTimeMillis() - mLastPress < 1000)) {
                            step = mSeekStep * 2;
                        }
                        mSeekPosition = mVideoView.getCurrentPosition() + step;
                        mViewProgress.setValue(mSeekPosition);
                    }
                    mLastPress = System.currentTimeMillis();
                    showPlayController();
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (mStatus == STATUS_NORMAL
                || mStatus == STATUS_SHOW_PLAYCONTROLLER) {
                    showPlayList();
                }
                break;
            case KeyEvent.KEYCODE_MENU:
                showSetting();
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (canSeekVideo()) {
                    if (mShortPress > 1) {
                        mVideoView.seekTo(mViewProgress.getValue());
                    } else {
                        if (mVideoView.isPlaying() && mSeekPosition >= 0) {
                            mVideoView.seekTo(mSeekPosition);
                        }
                    }
                    mShortPress = 0;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                showPlayStatus();
                break;
        }
        delayHide();
        return super.onKeyUp(keyCode, event);
    }

    private void showPlayStatus() {
        if (mStatus == STATUS_SHOW_PLAYLIST) {
            //VideoActivity.startActivity(VideoActivity.this, mList.get(mAdapter.mPosition).path, 0);
        } else if (mStatus == STATUS_SHOW_PLAYCONTROLLER) {
            if (mVideoView.isPlaying()) {
                mVideoView.pause();
            } else {
                mVideoView.start();
            }
        } else {
            showPlayController();
        }
    }

    private void longEventHandler() {
        mShortPress++;
    }

    public boolean canSeekVideo() {
        return mStatus == STATUS_NORMAL || mStatus == STATUS_SHOW_PLAYCONTROLLER;
    }

    private void seek(long position) {
        mVideoView.seekTo(position);
    }

    public void updateTime() {
        if (mViewProgressCurrent == null || mShortPress > 1 || mSeekPosition >= 0) {
            return;
        }
        mViewProgress.setValue(mVideoView.getCurrentPosition());
        mViewProgressCurrent.setText(DateUtils.toTimeLength(mVideoView.getCurrentPosition()));
    }

    public void setting(String key, int index) {
        if ((KEY_AUDIO).equals(key)){
            mVideoView.selectTrack(index);
        } else if ((KEY_SUBTITLE).equals(key)){
            mVideoView.selectTrack(index);
        } else if ((KEY_SCALE).equals(key)){
            mVideoView.setAspectRatio(index);
        }
    }

    public void showPlayController() {
        mStatus = STATUS_SHOW_PLAYCONTROLLER;
        mPlayListView.setVisibility(View.GONE);
        mViewListSelect.setVisibility(View.GONE);
        mPlaySettingPanel.setVisibility(View.GONE);
        mViewPlayController.setVisibility(View.VISIBLE);
        mSettingListPanel.setVisibility(View.GONE);
        delayHide();
    }

    public void showPlayList() {
        mStatus = STATUS_SHOW_PLAYLIST;
        mViewPlayController.setVisibility(View.GONE);
        mPlaySettingPanel.setVisibility(View.GONE);
        mPlayListView.setVisibility(View.VISIBLE);
        mViewListSelect.setVisibility(View.VISIBLE);
        mSettingListPanel.setVisibility(View.GONE);
        mPlayListView.update();
        delayHide();
    }

    public void showSetting() {
        mStatus = STATUS_SHOW_SETTING;
        mViewPlayController.setVisibility(View.GONE);
        mPlayListView.setVisibility(View.GONE);
        mViewListSelect.setVisibility(View.GONE);
        mPlaySettingPanel.setVisibility(View.VISIBLE);
        mSettingListPanel.setVisibility(View.GONE);
        SettingListPanel.TrackItem audio = null, subtitl = null;
        if (mAudioTrack != null && mAudioTrack.size() > 0 && mSelectAudioIndex >= 0) {
            audio = mAudioTrack.get(mSelectAudioIndex);
        }
        if (mSubTitleTrack != null && mAudioTrack.size() > 0 && mSelectSubtitleIndex >= 0) {
            subtitl = mSubTitleTrack.get(mSelectSubtitleIndex);
        }
        mPlaySettingPanel.update(audio, subtitl);
        Schedule.linear(new UITask() {
            @Override
            public void run(TaskSchedule taskSchedule, Object result) {
                mPlaySettingPanel.getChildAt(0).requestFocus();
            }
        }, 300).start();
    }

    public void showSubtitleList() {
        mStatus = STATUS_LIST_SUBTITLE;
        mViewPlayController.setVisibility(View.GONE);
        mPlayListView.setVisibility(View.GONE);
        mViewListSelect.setVisibility(View.GONE);
        mPlaySettingPanel.setVisibility(View.GONE);
        mSettingListPanel.setVisibility(View.VISIBLE);
        mSettingListPanel.setData(SettingListPanel.TYPE_SUBTITLE,"字幕选择", mSubTitleTrack, mSelectSubtitleIndex);
        delayHide();
    }

    public void showAudioList() {
        mStatus = STATUS_LIST_AUDIO;
        mViewPlayController.setVisibility(View.GONE);
        mPlayListView.setVisibility(View.GONE);
        mViewListSelect.setVisibility(View.GONE);
        mPlaySettingPanel.setVisibility(View.GONE);
        mSettingListPanel.setVisibility(View.VISIBLE);
        mSettingListPanel.setData(SettingListPanel.TYPE_AUDIO,"音频选择", mAudioTrack, mSelectAudioIndex);
        delayHide();
    }

    public void showPlayLoading() {
        mStatus = STATUS_LOADING;
        ImageView imageView = (ImageView) mViewPlayLoading.getChildAt(0);
        imageView.setImageResource(R.mipmap.video_play_start_bg);
    }

    public void showPlayError() {
        mStatus = STATUS_ERROR;
        ImageView imageView = (ImageView) mViewPlayLoading.getChildAt(0);
        imageView.setImageResource(R.mipmap.video_play_start_bg);
    }

    public void hidePlayStatus() {
        mStatus = STATUS_NORMAL;
        mViewPlayLoading.setVisibility(View.GONE);
    }

    public void setSpeed(float speed) {
        if (mVideoView == null) {
            return;
        }
        mVideoView.setSpeed(speed);
    }

    public float getSpeed() {
        if (mVideoView == null) {
            return 1f;
        }
        return mVideoView.getSpeed();
    }

    public void hideAll() {
        mStatus = STATUS_NORMAL;
        mViewPlayController.setVisibility(View.GONE);
        mPlayListView.setVisibility(View.GONE);
        mViewListSelect.setVisibility(View.GONE);
        mPlaySettingPanel.setVisibility(View.GONE);
        mSettingListPanel.setVisibility(View.GONE);
        mPlayListView.clearFocus();
    }

    public void selectStream(int type, int index) {
        if (type == SettingListPanel.TYPE_AUDIO) {
            mSelectAudioIndex = index;
            SettingListPanel.TrackItem trackItem = mAudioTrack.get(index);
            mVideoView.selectTrack(trackItem.streamIndex);
        } else {
            mSelectSubtitleIndex = index;
            SettingListPanel.TrackItem trackItem = mSubTitleTrack.get(index);
            if (trackItem.tag == -1) {
                mSubTitle.reset();
                mViewSubTitle.setText("");
            } else if (trackItem.tag == 2 || trackItem.tag == 1) {
                if (trackItem.value.startsWith("smb://")) {
                    Schedule.linear(new Task() {
                        @Override
                        public void run(TaskSchedule taskSchedule, Object result) {
                            try {
                                mSubTitle.setSubTitle(trackItem.value, new SmbFileInputStream(new SmbFile(trackItem.value)));
                            } catch (SmbException e) {
                                e.printStackTrace();
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else if (URLUtil.isHttpUrl(trackItem.value) || URLUtil.isHttpsUrl(trackItem.value)) {
                    mSubTitle.download(trackItem.value, mUrl);
                } else {
                    mSubTitle.setSubTitle(trackItem.value);
                }
            } else {
                mVideoView.selectTrack(trackItem.streamIndex);
            }
        }
    }

    public void delayHide() {
        mHandler.removeMessages(1000);
        mHandler.sendEmptyMessageDelayed(1000, 10000);
    }

    public static void startActivity(Context context, String url, long position, String root) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("position", position);
        intent.putExtra("root", root);
        context.startActivity(intent);
    }


}
