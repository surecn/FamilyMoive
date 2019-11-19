package com.surecn.familymovie.ui.live;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import com.surecn.familymovie.R;
import com.surecn.familymovie.domain.Channel;
import com.surecn.familymovie.domain.ChannelProgram;
import com.surecn.familymovie.domain.LiveRoot;
import com.surecn.familymovie.ui.base.TVLinearLayoutManager;
import com.surecn.familymovie.ui.base.TvRecyclerView;
import com.surecn.moat.tools.log;

import java.util.ArrayList;
import java.util.List;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-08
 * Time: 14:29
 */
public class ChannelListView extends LinearLayout {

    private TvRecyclerView mFistListView;

    private TvRecyclerView mSecondListView;

    private TvRecyclerView mThirdListView;

    private View mLableProgram;

    private FirstChannelAdapter mFirstChannelAdapter;

    private SecondChannelAdapter mSecondChannelAdapter;

    private ThirdChannelAdapter mThirdChannelAdapter;

    public ChannelListView(Context context) {
        super(context);
    }

    public ChannelListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ChannelListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mLableProgram = findViewById(R.id.lable_program);
        mFistListView = findViewById(R.id.first_list);
        mSecondListView = findViewById(R.id.second_list);
        mThirdListView = findViewById(R.id.third_list);

        mFistListView.setLayoutManager(new TVLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mSecondListView.setLayoutManager(new TVLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mThirdListView.setLayoutManager(new TVLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        mFirstChannelAdapter = new FirstChannelAdapter(getContext(), mFistListView, new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus || v.getTag() == null) {
                    return;
                }
                FirstChannelAdapter.ItemHolder item = (FirstChannelAdapter.ItemHolder) v.getTag();
                mSecondListView.scrollToPosition(item.liveRoot.getChannelPosition());
            }
        });
        mFistListView.setAdapter(mFirstChannelAdapter);

        mSecondChannelAdapter = new SecondChannelAdapter(getContext());
        mSecondChannelAdapter.setOnSectionChangeListener(new SecondChannelAdapter.OnSectionChangeListener() {
            @Override
            public void onSectionChange(int index) {
                mFirstChannelAdapter.setSelectIndex(index);
            }
        });
        mSecondListView.setAdapter(mSecondChannelAdapter);

        mThirdChannelAdapter = new ThirdChannelAdapter(getContext());
        mThirdListView.setAdapter(mThirdChannelAdapter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (mThirdListView.getVisibility() == View.VISIBLE) {
                    hideProgramList();
                    return true;
                } else if (mSecondListView.getFocusedChild() != null) {
                    mFirstChannelAdapter.setRequestFocus(true);
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mFistListView.getFocusedChild() != null) {
                    int index = getFirstRootChildIndex();
                    mSecondChannelAdapter.setSelectIndex(index, false);
                    mSecondListView.getLayoutManager().scrollToPosition(index);
                    mSecondListView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSecondChannelAdapter.setRequestFocus(true);
                        }
                    }, 200);
                    return true;
                } else if (mSecondListView.getFocusedChild() != null) {
                    showProgramList();
                    return true;
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private int getFirstRootChildIndex() {
        LiveRoot liveRoot = mFirstChannelAdapter.getData().get(mFirstChannelAdapter.getSelectIndex());
        List<Channel> data = mSecondChannelAdapter.getData();
        for (int i = liveRoot.getChannelPosition(), len = data.size(); i < len; i++) {
            if (data.get(i).getSection() == 0) {
                return i;
            }
        }
        for (int i = 0, len = data.size(); i < len; i++) {
            if (data.get(i).getSection() == 0) {
                return i;
            }
        }
        return 0;
    }

    public Channel getCurrentChannel() {
        return mSecondChannelAdapter.getCurrentChannel();
    }

    public void show() {
        mSecondChannelAdapter.setRequestFocus(true);
        hideProgramList();
    }

    private void showProgramList() {
        mLableProgram.setVisibility(View.GONE);
        mThirdListView.setVisibility(View.VISIBLE);
        ArrayList<ChannelProgram> list = mSecondChannelAdapter.getCurrentChannel().getPrograms();
        long time = System.currentTimeMillis();
        int index = 0;
        for (int i = 0, len = list.size(); i < len; i++) {
            if (list.get(i).getStartTime().getTime() > time) {
                index = i - 1;
                break;
            }
        }
        mThirdChannelAdapter.setData(index, list);
        mThirdListView.scrollToPosition(index);
    }

    private void hideProgramList() {
        mLableProgram.setVisibility(View.VISIBLE);
        mThirdListView.setVisibility(View.GONE);
    }

    public void setSelectIndex(int root, int channel) {
        mFirstChannelAdapter.setSelectIndex(root);
        mSecondChannelAdapter.setSelectIndex(channel, true);
    }

    public void setOnChannelChangeListener(SecondChannelAdapter.OnChannelChangeListener listener) {
        mSecondChannelAdapter.setOnChannelChangeListener(listener);
    }

    public void setData(List<LiveRoot> root, List<Channel> channels) {
        mFirstChannelAdapter.setData(root);
        mSecondChannelAdapter.setData(root, channels);
        mFistListView.getAdapter().notifyDataSetChanged();
        mSecondListView.getAdapter().notifyDataSetChanged();
    }



}
