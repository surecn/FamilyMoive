package com.surecn.familymovie.common.subtitle;

import android.os.Handler;
import android.os.Message;
import android.webkit.URLUtil;
import android.widget.TextView;
import com.surecn.familymovie.common.subtitle.interpreter.ContextExpression;
import com.surecn.familymovie.common.subtitle.interpreter.SubTitleContext;
import com.surecn.familymovie.common.subtitle.interpreter.TimedText;
import com.surecn.moat.tools.log;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-11
 * Time: 13:37
 */
public class SubTitlePlug implements SubTitleLoader.OnDownloadListener, ContextExpression.OnSubTitleListener {

    private final static int MSG_UPDATE_TIMEDTEXT = 1000;
    private final static int MSG_Hide_TIMEDTEXT = 1001;

    private TextView mTextView;

    private IMediaPlayer mMediaPlayer;

    private SubTitleContext mSubTitleContext;

    private TreeMap<Long, TimedText> mTimedTextData;

    private SubTitleCache mSubtitleCache;

    private SubTitleCache.SubTitleWriter mSubTitleWriter;

    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE_TIMEDTEXT:
                    updateTimeText();
                    break;
                case MSG_Hide_TIMEDTEXT:
                    hideTimedText();
                    break;
            }
        }
    };

    public SubTitlePlug(TextView textView) {
        this.mTextView = textView;
        mTimedTextData = new TreeMap<>();
        mSubtitleCache = new SubTitleCache(textView.getContext());
    }

    public void bind(IMediaPlayer iMediaPlayer) {
        mMediaPlayer = iMediaPlayer;
        mMediaPlayer.setOnSeekCompleteListener(new IMediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(IMediaPlayer iMediaPlayer) {
                sendUpdateMessage(0);
            }
        });
    }

    public String getByVideoPath(String videoPath) {
        File file = mSubtitleCache.getCache(videoPath);
        if (file != null) {
            return file.getPath();
        }
        return null;
    }

    public void download(String url, String videoPath) {
        SubTitleLoader subTitleLoader = SubTitleLoader.getInstance();
        subTitleLoader.setOnReadLineListener(this);
        subTitleLoader.download(url);
        mSubTitleWriter = mSubtitleCache.getWriter(videoPath);
    }

    public void setSubTitle(String path) {
        SubTitleLoader subTitleLoader = SubTitleLoader.getInstance();
        subTitleLoader.setOnReadLineListener(this);
        subTitleLoader.load(new File(path));
    }

    public void setSubTitle(String url, InputStream inputStream) {
        SubTitleLoader subTitleLoader = SubTitleLoader.getInstance();
        subTitleLoader.setOnReadLineListener(this);
        subTitleLoader.load(url, inputStream);
    }

    private String getFileExtension(String url) {
        int index = url.lastIndexOf("?");
        if (index > 0) {
            url = url.substring(0, index);
        }
        return url.substring(url.lastIndexOf(".") + 1);
    }

    @Override
    public void onStart(String url) {
        mSubTitleContext = new SubTitleContext(getFileExtension(url));
        mSubTitleContext.setOnSubTitleListener(this);
    }

    @Override
    public void onReadLine(String line) {
        log.d("onReadLine:" + line);
        mSubTitleContext.putLine(line);
        if (mSubTitleWriter != null)
            mSubTitleWriter.writeLine(line);
    }

    @Override
    public void onComplete() {
        log.d("subtitle load over");
        if (mHander.hasMessages(MSG_UPDATE_TIMEDTEXT) && mHander.hasMessages(MSG_Hide_TIMEDTEXT)) {
            return;
        }
        sendUpdateMessage(0);
    }

    @Override
    public void onScriptInfo(HashMap<String, String> map) {

    }

    @Override
    public void onNewTimeText(TimedText timedText) {
        mTimedTextData.put(timedText.getStart(), timedText);
        if (mHander.hasMessages(MSG_UPDATE_TIMEDTEXT) && mHander.hasMessages(MSG_Hide_TIMEDTEXT)) {
            return;
        }
        sendUpdateMessage(0);
    }

    public void reset() {
        if (mSubTitleContext != null)
            mSubTitleContext.setOnSubTitleListener(null);
        SubTitleLoader subTitleLoader = SubTitleLoader.getInstance();
        subTitleLoader.setOnReadLineListener(null);
        mSubTitleWriter = null;
        mHander.removeMessages(MSG_Hide_TIMEDTEXT);
        mHander.removeMessages(MSG_UPDATE_TIMEDTEXT);
    }

    private void hideTimedText() {
        mTextView.setText("");
        updateTimeText();
    }

    private void updateTimeText() {
        long currentTime = mMediaPlayer.getCurrentPosition();
        Map.Entry<Long,TimedText> entry = mTimedTextData.floorEntry(currentTime);
        if (entry == null) {
            return;
        }
        TimedText timedText = entry.getValue();
        if (currentTime >= timedText.getStart()) {
            showTimedText(timedText);
            sendHideMessage(timedText.getEnd() - currentTime);
        } else {
            sendUpdateMessage(timedText.getStart() - currentTime);
        }
    }

    private void sendUpdateMessage(long delay) {
        if (mTimedTextData.size() <= 0) {
            return;
        }
        mHander.removeMessages(MSG_UPDATE_TIMEDTEXT);
        Message message = mHander.obtainMessage(MSG_UPDATE_TIMEDTEXT);
        mHander.sendMessageDelayed(message, delay);
    }

    private void sendHideMessage(long delay) {
        mHander.removeMessages(MSG_Hide_TIMEDTEXT);
        Message message = mHander.obtainMessage(MSG_Hide_TIMEDTEXT);
        mHander.sendMessageDelayed(message, delay);
    }

    private void showTimedText(TimedText timedText) {
        mTextView.setText(timedText.getText());
    }
}