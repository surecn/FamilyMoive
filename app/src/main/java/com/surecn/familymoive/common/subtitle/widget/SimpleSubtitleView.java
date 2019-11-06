/*
 *                       Copyright (C) of Avery
 *
 *                              _ooOoo_
 *                             o8888888o
 *                             88" . "88
 *                             (| -_- |)
 *                             O\  =  /O
 *                          ____/`- -'\____
 *                        .'  \\|     |//  `.
 *                       /  \\|||  :  |||//  \
 *                      /  _||||| -:- |||||-  \
 *                      |   | \\\  -  /// |   |
 *                      | \_|  ''\- -/''  |   |
 *                      \  .-\__  `-`  ___/-. /
 *                    ___`. .' /- -.- -\  `. . __
 *                 ."" '<  `.___\_<|>_/___.'  >'"".
 *                | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *                \  \ `-.   \_ __\ /__ _/   .-` /  /
 *           ======`-.____`-.___\_____/___.-`____.-'======
 *                              `=- -='
 *           ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 *              Buddha bless, there will never be bug!!!
 */

package com.surecn.familymoive.common.subtitle.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.TextView;

import com.surecn.familymoive.common.subtitle.DefaultSubtitleEngine;
import com.surecn.familymoive.common.subtitle.IjkSubtitleEngine;
import com.surecn.familymoive.common.subtitle.SubtitleEngine;
import com.surecn.familymoive.common.subtitle.model.Subtitle;

import java.io.InputStream;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * @author AveryZhong.
 */

@SuppressLint("AppCompatCustomView")
public class SimpleSubtitleView extends TextView
        implements SubtitleEngine, SubtitleEngine.OnSubtitleChangeListener,
        SubtitleEngine.OnSubtitlePreparedListener {

    private static final String EMPTY_TEXT = "";

    private SubtitleEngine mSubtitleEngine;

    public SimpleSubtitleView(final Context context) {
        super(context);
    }

    public SimpleSubtitleView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleSubtitleView(final Context context, final AttributeSet attrs,
                              final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(final MediaPlayer mediaPlayer) {
        mSubtitleEngine = new DefaultSubtitleEngine(mediaPlayer);
        mSubtitleEngine.setOnSubtitlePreparedListener(this);
        mSubtitleEngine.setOnSubtitleChangeListener(this);
    }

    public void init(final tv.danmaku.ijk.media.player.IMediaPlayer mediaPlayer) {
        mSubtitleEngine = new IjkSubtitleEngine(mediaPlayer);
        mSubtitleEngine.setOnSubtitlePreparedListener(this);
        mSubtitleEngine.setOnSubtitleChangeListener(this);
    }

    @Override
    public void onSubtitlePrepared(@Nullable final List<Subtitle> subtitles) {
        start();
    }

    @Override
    public void onSubtitleChanged(@Nullable final Subtitle subtitle) {
        if (subtitle == null) {
            setText(EMPTY_TEXT);
            return;
        }
        setText(Html.fromHtml(subtitle.content));
    }

    @Override
    public void setSubtitlePath(final String path) {
        mSubtitleEngine.setSubtitlePath(path);
    }

    @Override
    public void reset() {
        if (mSubtitleEngine != null)
            mSubtitleEngine.reset();
    }

    @Override
    public void start() {
        mSubtitleEngine.start();
    }

    @Override
    public void pause() {
        mSubtitleEngine.pause();
    }

    @Override
    public void resume() {
        mSubtitleEngine.resume();
    }

    @Override
    public void stop() {
        mSubtitleEngine.stop();
    }

    @Override
    public void destroy() {
        if (mSubtitleEngine != null)
            mSubtitleEngine.destroy();
    }

    @Override
    public void setOnSubtitlePreparedListener(final OnSubtitlePreparedListener listener) {
        mSubtitleEngine.setOnSubtitlePreparedListener(listener);
    }

    @Override
    public void setOnSubtitleChangeListener(final OnSubtitleChangeListener listener) {
        mSubtitleEngine.setOnSubtitleChangeListener(listener);
    }

    @Override
    protected void onDetachedFromWindow() {
        destroy();
        super.onDetachedFromWindow();
    }
}
