package com.surecn.familymoive.ui.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import com.surecn.familymoive.BuildConfig;
import com.surecn.familymoive.DefaultConfig;
import com.surecn.familymoive.R;
import com.surecn.familymoive.common.SambaManager;
import com.surecn.familymoive.common.player.media.IRenderView;
import com.surecn.familymoive.common.player.media.IjkVideoView;
import com.surecn.familymoive.common.subtitle.model.Subtitle;
import com.surecn.familymoive.common.subtitle.widget.SimpleSubtitleView;
import com.surecn.familymoive.data.HistoryModel;
import com.surecn.familymoive.data.HttpAdapter;
import com.surecn.familymoive.domain.FileItem;
import com.surecn.familymoive.domain.SubDetailItem;
import com.surecn.familymoive.domain.SubTitleItem;
import com.surecn.familymoive.ui.BaseActivity;
import com.surecn.familymoive.utils.DateUtils;
import com.surecn.familymoive.utils.SubtitleUtils;
import com.surecn.familymoive.utils.UriUtil;
import com.surecn.familymoive.utils.WindowUtils;
import com.surecn.moat.core.Schedule;
import com.surecn.moat.core.TaskSchedule;
import com.surecn.moat.core.task.Task;
import com.surecn.moat.core.task.UITask;
import com.surecn.moat.tools.log;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
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

    private SimpleSubtitleView mViewSubTitle;
    private TextView mViewTitle;
    private TextView mViewTime;
    private TextView mViewProgressCurrent;
    private TextView mViewProgressTotal;
    private ProgressView mViewProgress;

    private PlayListView mPlayListView;
    private String mUrl;
    private HistoryModel mHistoryModel;

    private TextView mSelectTitleView;

    private PlaySettingPanel mPlaySettingPanel;
    private SettingListPanel mSettingListPanel;
    private FrameLayout mViewPlayLoading;
    private ConstraintLayout mViewPlayController;
    private RecyclerView mViewPlayList;
    private TextView mViewListSelect;

    private int mStatus;

    private TableLayout mHudView;

    private boolean mBackPressed;

    private int mShortPress = 0;

    private long mInitPosition = 0;

    private int mSelectSubtitleIndex;
    private int mSelectAudioIndex;

    private List<FileItem> mList;

    private List<SettingListPanel.TrackItem> mAudioTrack;

    private List<SettingListPanel.TrackItem> mSubTitleTrack;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.hideNavigationBar(this, true);
        setContentView(R.layout.activity_video);

        initView();

        String url = getIntent().getStringExtra("url");
        long initPosition = getIntent().getLongExtra("position", 0);
        mHistoryModel = new HistoryModel(this);
        if (savedInstanceState != null) {
            url = savedInstanceState.getString("url");
            url = savedInstanceState.getString("position");
        }

        initVideo(url, initPosition);
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
    }

    @Override
    public void onBackPressed() {
        if (mStatus != STATUS_NORMAL) {
            hideAll();
            return;
        }
        super.onBackPressed();
        mBackPressed = true;
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
            mHistoryModel.save(mUrl, mVideoView.getCurrentPosition(), mVideoView.getDuration());
        }
    }

    private void initView() {
        mViewListSelect = findViewById(R.id.play_list_select);
        mViewPlayList = findViewById(R.id.play_list);
        mViewPlayController = findViewById(R.id.play_controller);
        mViewPlayLoading = findViewById(R.id.play_loading);
        mPlaySettingPanel = findViewById(R.id.setting);
        mSettingListPanel = findViewById(R.id.setting_list_panel);
        mSelectTitleView = findViewById(R.id.play_list_select);
        mPlayListView = findViewById(R.id.play_list);
        mHudView = (TableLayout) findViewById(R.id.hud_view);
        mViewSubTitle = findViewById(R.id.subtitle_view);
        mVideoView = findViewById(R.id.video_view);
        mViewTitle = findViewById(R.id.title);
        mViewTime = findViewById(R.id.time);
        mViewProgressCurrent = findViewById(R.id.progress_current);
        mViewProgressTotal = findViewById(R.id.progress_total);
        mViewProgress = findViewById(R.id.progress_ui);
        findViewById(R.id.back).setOnClickListener(this);

        if (BuildConfig.DEBUG) {
            mHudView = (TableLayout) LayoutInflater.from(this).inflate(R.layout.ijkplayer_debug_panel, null);
            mVideoView.setHudView(mHudView);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(broadcastReceiver, filter);
    }

    public void initVideo(String url, long initPosition) {
        showPlayLoading();
        this.mUrl = url;
        this.mInitPosition = initPosition;
        mVideoView.setAspectRatio(IRenderView.AR_ASPECT_FIT_PARENT);
        mVideoView.setVideoPath("http://127.0.0.1:12315/smb" + URLEncoder.encode(mUrl.substring(5)));
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
                    initVideo(path, 0);
                }
            }
        });
        mVideoView.start();
        if (mViewSubTitle != null) {
            mViewSubTitle.reset();
        }
    }

    private void initData(IMediaPlayer iMediaPlayer) {
        hidePlayStatus();
        showPlayController();
        mViewTitle.setText("正在播放:" + mUrl);
        mViewTime.setText(DateUtils.toTime(new Date()));
        mViewProgressTotal.setText(DateUtils.toTimeLength(mVideoView.getDuration()));
        mViewProgress.setMaxValue(mVideoView.getDuration());
        if (mInitPosition > 0) {
            mVideoView.seekTo(mInitPosition);
        }

        initSettingListData();

        saveHistory();

        Schedule.linear(new Task() {
            @Override
            public void run(TaskSchedule work, Object result) {
                if (mUrl.startsWith("smb://")) {
                    DefaultConfig iconfig = new DefaultConfig();
                    SambaManager sambaManager = new SambaManager(iconfig);
                    sambaManager.updateHost(UriUtil.getUriHost(mUrl) + "/");
                    mList = sambaManager.listFile(UriUtil.getUriParent(mUrl));
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
                    log.e("----------onTimedText-----------" + ijkTimedText.getText());
                    Subtitle subtitle = new Subtitle();
                    subtitle.content = SubtitleUtils.formatText(ijkTimedText.getText());
                    mViewSubTitle.onSubtitleChanged(subtitle);
                }
            }
        });
        mHandler.removeMessages(MSG_UPDATE_TIME);
        mHandler.sendEmptyMessage(MSG_UPDATE_TIME);
    }

    private void initSettingListData() {
        mAudioTrack = new ArrayList<>();
        mSubTitleTrack = new ArrayList<>();
        mSubTitleTrack.add(new SettingListPanel.TrackItem(-1, "关闭字幕", "", 0));
        ArrayList<IjkMediaMeta.IjkStreamMeta> streams = mVideoView.getMediaPlayer().getMediaInfo().mMeta.mStreams;
        int audioStreamIndex = mVideoView.getSelectedTrack(ITrackInfo.MEDIA_TRACK_TYPE_AUDIO);
        int subtitleStreamIndex = mVideoView.getSelectedTrack(ITrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT);
        for (IjkMediaMeta.IjkStreamMeta streamMeta : streams) {
//            for (String key : streamMeta.mMeta.keySet()) {
//                log.e("===initSettingListData======" + key + "  " + streamMeta.mMeta.getString(key));
//            }
            if (IjkMediaMeta.IJKM_KEY_AUDIO_STREAM.equals(streamMeta.mType)) {
                mAudioTrack.add(new SettingListPanel.TrackItem(streamMeta.mIndex, streamMeta.mLanguage, streamMeta.mLanguage, 0));
                if (streamMeta.mIndex == audioStreamIndex) {
                    mSelectAudioIndex = mAudioTrack.size() - 1;
                }
            } else if (IjkMediaMeta.IJKM_KEY_TIMEDTEXT_STREAM.equals(streamMeta.mType) && subtitleStreamIndex == streamMeta.mIndex) {
                mSubTitleTrack.add(new SettingListPanel.TrackItem(streamMeta.mIndex, streamMeta.mLanguage, streamMeta.mLanguage, 0));
            }
        }
        Schedule.linear(new Task() {
            @Override
            public void run(TaskSchedule work, Object result) {
                DefaultConfig iconfig = new DefaultConfig();
                SambaManager sambaManager = new SambaManager(iconfig);
                String subtitle = sambaManager.searchSubtitle(mUrl);
                if (subtitle != null) {
                    mSubTitleTrack.add(new SettingListPanel.TrackItem(-2, UriUtil.getUriName(subtitle), subtitle, 1));
                    return;
                }
                List<SubTitleItem> list = HttpAdapter.getSubTitleService().search(UriUtil.getUriName(mUrl), 1, 1, 1, 0);
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
                                mSubTitleTrack.add(new SettingListPanel.TrackItem(subDetailItem.getId(), subDetailItem.getNative_name(), subDetailItem.getUrl(), 2));
                            }
                        }
                    }
                }
            }
        }).next(new UITask() {
            @Override
            public void run(TaskSchedule taskSchedule, Object result) {
                int streamIndex = mVideoView.getSelectedTrack(ITrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT);
                for (int i = 0; i < mSubTitleTrack.size(); i++) {
                    SettingListPanel.TrackItem item = mSubTitleTrack.get(i);
                    if (streamIndex >= 0) {
                        if (item.streamIndex == streamIndex) {
                            mSelectSubtitleIndex = i;
                            return;
                        }
                        continue;
                    } else {
                        if (item.tag == 1) {
                            mViewSubTitle.init(mVideoView.getMediaPlayer());
                            mViewSubTitle.setSubtitlePath(item.value);
                            mSelectSubtitleIndex = i;
                            return;
                        } else if (item.tag == 2) {
                            mViewSubTitle.init(mVideoView.getMediaPlayer());
                            mViewSubTitle.setSubtitlePath(item.value);
                            mSelectSubtitleIndex = i;
                            return;
                        }
                    }
                }
            }

        }).start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("url", mUrl);
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
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                showPlayController();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (canSeekVideo()) {
                    longEventHandler();
                    if (mShortPress > 1) {
                        mViewProgress.setValue(mViewProgress.getValue() - 60000);
                    } else {
                        mVideoView.seekTo(mVideoView.getCurrentPosition() - 30000);
                    }
                    showPlayController();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (canSeekVideo()) {
                    longEventHandler();
                    if (mShortPress > 1) {
                        mViewProgress.setValue(mViewProgress.getValue() + 60000);
                    } else {
                        mVideoView.seekTo(mVideoView.getCurrentPosition() + 30000);
                    }
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
                if (mStatus == STATUS_NORMAL) {
                    showPlayList();
                }
                break;
            case KeyEvent.KEYCODE_MENU:
                showSetting();
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (canSeekVideo()) {
                    if (mShortPress > 1) {
                        mVideoView.seekTo(mViewProgress.getValue());
                    }
                    mShortPress = 0;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (canSeekVideo()) {
                    if (mShortPress > 1) {
                        mVideoView.seekTo(mViewProgress.getValue());
                    }
                    mShortPress = 0;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                if (mStatus == STATUS_SHOW_PLAYLIST) {
                    log.e("=====STATUE_SHOW_PLAYLIST====");
                    //VideoActivity.startActivity(VideoActivity.this, mList.get(mAdapter.mPosition).path, 0);
                } else {
                    showPlayController();
                }
                break;
        }
        delayHide();
        return super.onKeyUp(keyCode, event);
    }

    private void longEventHandler() {
        mShortPress++;
    }

    public boolean canSeekVideo() {
        return mStatus == STATUS_NORMAL || mStatus == STATUS_SHOW_PLAYCONTROLLER;
    }

    public void updateTime() {
        if (mViewProgressCurrent == null || mShortPress > 1) {
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
        mViewPlayList.setVisibility(View.GONE);
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
        mViewPlayList.setVisibility(View.VISIBLE);
        mViewListSelect.setVisibility(View.VISIBLE);
        mSettingListPanel.setVisibility(View.GONE);
        delayHide();
    }

    public void showSetting() {
        mStatus = STATUS_SHOW_SETTING;
        mViewPlayController.setVisibility(View.GONE);
        mViewPlayList.setVisibility(View.GONE);
        mViewListSelect.setVisibility(View.GONE);
        mPlaySettingPanel.setVisibility(View.VISIBLE);
        mSettingListPanel.setVisibility(View.GONE);
        SettingListPanel.TrackItem audio = null, subtitl = null;
        if (mAudioTrack != null) {
            audio = mAudioTrack.get(mSelectAudioIndex);
        }
        if (mSubTitleTrack != null) {
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
        mViewPlayList.setVisibility(View.GONE);
        mViewListSelect.setVisibility(View.GONE);
        mPlaySettingPanel.setVisibility(View.GONE);
        mSettingListPanel.setVisibility(View.VISIBLE);
        mSettingListPanel.setData(SettingListPanel.TYPE_SUBTITLE,"字幕选择", mSubTitleTrack, mSelectSubtitleIndex);
        delayHide();
    }

    public void showAudioList() {
        mStatus = STATUS_LIST_AUDIO;
        mViewPlayController.setVisibility(View.GONE);
        mViewPlayList.setVisibility(View.GONE);
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

    public void hideAll() {
        mStatus = STATUS_NORMAL;
        mViewPlayController.setVisibility(View.GONE);
        mViewPlayList.setVisibility(View.GONE);
        mViewListSelect.setVisibility(View.GONE);
        mPlaySettingPanel.setVisibility(View.GONE);
        mSettingListPanel.setVisibility(View.GONE);
    }

    public void selectStream(int type, int index) {
        if (type == SettingListPanel.TYPE_AUDIO) {
            mSelectAudioIndex = index;
            SettingListPanel.TrackItem trackItem = mAudioTrack.get(index);
            mVideoView.selectTrack(trackItem.streamIndex);
        } else {
            mSelectSubtitleIndex = index;
            SettingListPanel.TrackItem trackItem = mSubTitleTrack.get(index);
            if (trackItem.streamIndex == -1) {
                mViewSubTitle.reset();
                mViewSubTitle.setText(null);
            } else if (trackItem.streamIndex == -2) {
                mViewSubTitle.init(mVideoView.getMediaPlayer());
                mViewSubTitle.setSubtitlePath(trackItem.value);
            } else {
                mVideoView.selectTrack(trackItem.streamIndex);

            }
        }
    }

    public void delayHide() {
        mHandler.removeMessages(1000);
        mHandler.sendEmptyMessageDelayed(1000, 10000);
    }

    public static void startActivity(Context context, String url, long position) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }


}
