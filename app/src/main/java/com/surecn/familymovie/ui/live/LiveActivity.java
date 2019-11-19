package com.surecn.familymovie.ui.live;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import com.surecn.familymovie.R;
import com.surecn.familymovie.Setting;
import com.surecn.familymovie.common.player.media.IRenderView;
import com.surecn.familymovie.common.player.media.IjkVideoView;
import com.surecn.familymovie.data.AppProvider;
import com.surecn.familymovie.data.FavoriteModel;
import com.surecn.familymovie.data.FileCache;
import com.surecn.familymovie.data.HttpAdapter;
import com.surecn.familymovie.domain.Channel;
import com.surecn.familymovie.domain.ChannelProgram;
import com.surecn.familymovie.domain.Favorite;
import com.surecn.familymovie.domain.LiveRoot;
import com.surecn.familymovie.ui.base.BaseActivity;
import com.surecn.familymovie.utils.WindowUtils;
import com.surecn.moat.core.Schedule;
import com.surecn.moat.core.TaskSchedule;
import com.surecn.moat.core.task.Task;
import com.surecn.moat.core.task.UITask;
import com.surecn.moat.tools.log;
import com.surecn.moat.tools.setting.SettingManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import androidx.annotation.Nullable;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-08
 * Time: 11:21
 */
public class LiveActivity extends BaseActivity {

    private static final int MSG_HIDE_ALL_LAYER = 1000;

    private IjkVideoView mVideoView;

    private ChannelListView mChannelListView;

    private LiveSettingPanel mLiveSettingPanel;

    private String mUrl;

    private boolean mBackPressed;

    private boolean mShowChannelList;

    private boolean mShowSetting;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_HIDE_ALL_LAYER:
                    hideAll();
                    break;
            }
        }
    };

    private ContentObserver mContentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            initData(false);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.activity_live);
        initView();
        getContentResolver().registerContentObserver(AppProvider.getContentUri("FAVORITE"), true, mContentObserver);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initData(false);
    }

    public void initView() {
        mLiveSettingPanel = findViewById(R.id.setting);
        mVideoView = findViewById(R.id.video_view);
        mChannelListView = findViewById(R.id.channel_list);
        mChannelListView.setOnChannelChangeListener(new SecondChannelAdapter.OnChannelChangeListener() {
            @Override
            public void onChannelChange(Channel channel) {
                startVideo(channel.getSrcs().get(0).getUrl());
                Setting.liveSelectId = channel.getId();
                SettingManager.getInstance(LiveActivity.this).save(Setting.class);
            }
        });
    }

    public void initData(boolean foreUpdate) {
        showLoading();
        Schedule.linear(new Task() {
            @Override
            public void run(TaskSchedule taskSchedule, Object result) {
                List<LiveRoot> channelList = null;
                Calendar day = Calendar.getInstance();
                day.setTime(new Date(Setting.updateChannelTime));
                Calendar toady = Calendar.getInstance();
                /*优先去缓存中的*/
                if (!foreUpdate && day.get(Calendar.DAY_OF_MONTH) == toady.get(Calendar.DAY_OF_MONTH)
                        && day.get(Calendar.MONTH) == toady.get(Calendar.MONTH)
                        && day.get(Calendar.YEAR) == toady.get(Calendar.YEAR)) {
                    channelList = (List<LiveRoot>) FileCache.getInstance().read(LiveActivity.this, FileCache.KEY_CHANNELDATA);
                }
                if (channelList == null) {
                    channelList = HttpAdapter.getPlayerService().getChannels();
                    FileCache.getInstance().save(LiveActivity.this, FileCache.KEY_CHANNELDATA, channelList);
                    String date = calculateLastDate(channelList);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Setting.updateChannelTime = simpleDateFormat.parse(date).getTime();
                        SettingManager.getInstance(LiveActivity.this).save(Setting.class);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (channelList != null) {
                    final LiveRoot liveRoot = new LiveRoot();
                    liveRoot.setId(-1);
                    liveRoot.setTitle(getString(R.string.live_favorite));
                    liveRoot.setSubs(getFavoriteChannel(channelList));
                    channelList.add(0, liveRoot);
                    taskSchedule.sendNext(channelList);
                    final List<LiveRoot> temp = channelList;
                }
            }
        }).next(new UITask<List<LiveRoot>>() {
            @Override
            public void run(TaskSchedule taskSchedule, List<LiveRoot> result) {
                channelProces(result);
                hideLoading();
            }
        }).start();
    }

    public void update() {
        initData(true);
    }

    private List<Channel> getFavoriteChannel(List<LiveRoot> channelList) {
        ArrayList<Channel> list = new ArrayList<>();
        List<Favorite> favorites = new FavoriteModel(LiveActivity.this).getFavoriteLives();
        if (favorites == null) {
            return list;
        }
        for (Favorite favorite : favorites) {
            int id = Integer.parseInt(favorite.getValue());
            for (LiveRoot root : channelList) {
                for (Channel channel : root.getSubs()) {
                    if (channel.getId() == id) {
                        list.add(channel);
                    }
                }
            }
        }
        return list;
    }

    private String calculateLastDate(List<LiveRoot> result) {
        String lastDate = "";
        boolean hasLastDate = false;
        for (LiveRoot liveRoot : result) {
            for (Channel channel : liveRoot.getSubs()) {
                TreeMap<Long, ChannelProgram> programTreeMap = new TreeMap<>();
                for (ChannelProgram program : channel.getPrograms()) {
                    programTreeMap.put(program.getStartTime().getTime(), program);
                    if (!hasLastDate && program.getDate().compareTo(lastDate) > 0) {
                        lastDate = program.getDate();
                    }
                }
                channel.setProgramMaps(programTreeMap);
                channel.setLastDate(lastDate);
                hasLastDate = true;
            }
        }
        return lastDate;
    }

    private void channelProces(List<LiveRoot> result) {
        ArrayList<Channel> channels = new ArrayList<>();
        int rootSelect = 0;
        int channelSelect = 1;
        int firstRootSelect = -1;
        int firstChannelSelect = 0;

        try {
            for (int i = 0; i < result.size(); i++) {
                LiveRoot liveRoot = result.get(i);
                liveRoot.setChannelPosition(channels.size());
                if (liveRoot.getSubs() != null && liveRoot.getSubs().size() > 0) {
                    Channel channel = new Channel(liveRoot.getTitle(), 1, i);
                    channels.add(channel);
                    channels.addAll(liveRoot.getSubs());
                }
            }
            int index = 0;
            for (int i = 0, len = channels.size(); i < len; i++) {
                Channel channel = channels.get(i);
                if (channel.getSection() == 1) {
                    index = channel.getIndex();
                    continue;
                }
                if (channel.getId() == Setting.liveSelectId) {
                    rootSelect = index;
                    channelSelect = i;
                    return;
                }
                if (firstRootSelect < 0 && channel.getSrcs() != null && channel.getSrcs().size() > 0) {
                    firstRootSelect = index;
                    firstChannelSelect = i;
                }
            }
            rootSelect = firstRootSelect;
            channelSelect = firstChannelSelect;
        } finally {
            showChannelListView();
            mChannelListView.setData(result, channels);
            log.d("rootIndex:" + rootSelect);
            log.d("channelIndex:" + channelSelect);
            mChannelListView.setSelectIndex(rootSelect, channelSelect);
        }
    }

    public void startVideo(String url) {
        this.mUrl = url;
        mVideoView.setAspectRatio(IRenderView.AR_ASPECT_FIT_PARENT);
        mVideoView.setVideoURI(Uri.parse(url));
        mVideoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {

            }
        });
        mVideoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {

                return false;
            }
        });
//        mVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(IMediaPlayer iMediaPlayer) {
//                String path = mUrl;
//                if(getSharedPreferences("video_setting", Context.MODE_PRIVATE).getInt("order", 0) == 0) {
//                    path = mPlayListView.getNextPath();
//                }
//                if (path != null) {
//                    initVideo(path, 0);
//                }
//            }
//        });
        mVideoView.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(mContentObserver);
        mHandler = null;
    }

    @Override
    public void onBackPressed() {
        if (mShowChannelList || mShowSetting) {
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
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (mChannelListView.getVisibility() == View.VISIBLE) {
                    return mChannelListView.onKeyDown(keyCode, event);
                }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                showSetting();
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                showChannelListView();
                break;
        }
        delayHide();
        return super.onKeyUp(keyCode, event);
    }

    public void favoriteCurrentChannel() {
        FavoriteModel favoriteModel = new FavoriteModel(this);
        if(favoriteModel.addLive(mChannelListView.getCurrentChannel().getId())) {
            showToast(R.string.msg_favorite_success);
        } else {
            showToast(R.string.msg_favorite_live_error);
        }
    }

    private void showSetting() {
        mShowSetting = true;
        mLiveSettingPanel.setVisibility(View.VISIBLE);
        mLiveSettingPanel.postDelayed(new Runnable() {
            @Override
            public void run() {
                mLiveSettingPanel.getChildAt(0).requestFocus();
            }
        }, 200);
        delayHide();
    }

    private void showChannelListView() {
        mShowChannelList = true;
        mLiveSettingPanel.setVisibility(View.GONE);
        mChannelListView.setVisibility(View.VISIBLE);
        mChannelListView.show();
        delayHide();
    }

    private void showProgramList() {

    }

    private void hideAll() {
        mShowChannelList = false;
        mShowSetting = false;
        mLiveSettingPanel.setVisibility(View.GONE);
        mChannelListView.setVisibility(View.INVISIBLE);
    }

    public void delayHide() {
        mHandler.removeMessages(MSG_HIDE_ALL_LAYER);
        mHandler.sendEmptyMessageDelayed(MSG_HIDE_ALL_LAYER, 10000);
    }
}