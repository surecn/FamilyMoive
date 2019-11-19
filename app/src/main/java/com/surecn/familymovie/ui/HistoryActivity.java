package com.surecn.familymovie.ui;

import android.database.ContentObserver;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.surecn.familymovie.R;
import com.surecn.familymovie.common.GridPaddingDecoration;
import com.surecn.familymovie.data.AppProvider;
import com.surecn.familymovie.data.HistoryModel;
import com.surecn.familymovie.domain.History;
import com.surecn.familymovie.ui.base.TitleActivity;
import com.surecn.familymovie.ui.player.VideoActivity;
import com.surecn.familymovie.utils.DateUtils;
import com.surecn.familymovie.utils.UriUtil;

import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-10-22
 * Time: 18:20
 */
public class HistoryActivity extends TitleActivity implements View.OnClickListener {

    private RecyclerView mViewList;

    private List mList;

    private HistoryAdapter mAdapter;

    private int mColomnCount = 6;

    private ContentObserver mContentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            initData();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.play_history);
        setContentView(R.layout.activity_history);
        initView();
        initData();
    }

    private void initView() {
        mViewList = findViewById(R.id.list);
        mViewList.setLayoutManager(new GridLayoutManager(this, mColomnCount) {
            @Override
            public boolean requestChildRectangleOnScreen(@NonNull RecyclerView parent, @NonNull View child, @NonNull Rect rect, boolean immediate, boolean focusedChildVisible) {
                return super.requestChildRectangleOnScreen(parent, child, rect, true, focusedChildVisible);
            }
        });
        mAdapter = new HistoryAdapter();
        mViewList.setAdapter(mAdapter);
        mViewList.addItemDecoration(new GridPaddingDecoration(this, 0, 0, getResources().getDimensionPixelSize(R.dimen.video_list_space), getResources().getDimensionPixelSize(R.dimen.video_list_space)));
        getContentResolver().registerContentObserver(AppProvider.getContentUri("HISTORY"), true, mContentObserver);
    }

    private void initData() {
        List<History> list = new HistoryModel(this).all();

        mList = new ArrayList();
        String date = null;
        if (list == null) {
            return;
        }
        for (int i = 0, len = list.size(); i < len; i++) {
            History history = list.get(i);
            String d = DateUtils.toSimplyDate(history.getTime());
            if (date == null || !date.equals(d)) {
                date = d;
                if (mList.size() % (mColomnCount) != 0) {
                    for (int j = 0, lenj = mColomnCount - mList.size() % (mColomnCount); j < lenj; j++) {
                        mList.add(null);
                    }
                }
                mList.add(date);
            } else if (mList.size() % (mColomnCount) == 0) {
                mList.add(null);
            }
            mList.add(history);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(mContentObserver);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onClick(View v) {
        HistoryHolder historyHolder = (HistoryHolder) v.getTag();
        if (mList.get(historyHolder.index) instanceof History) {
            History history = (History) mList.get(historyHolder.index);
            VideoActivity.startActivity(this, history.getUrl(), history.getPosition());
        }
    }

    public class HistoryAdapter extends RecyclerView.Adapter<HistoryHolder> {

        @NonNull
        @Override
        public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            HistoryHolder historyHolder = null;
            if (viewType == 0) {
                View view = LayoutInflater.from(HistoryActivity.this).inflate(R.layout.item_history_section, parent, false);
                historyHolder = new HistoryActivity.SelectionHolder(view);
                view.setTag(historyHolder);
            } else {
                View view = LayoutInflater.from(HistoryActivity.this).inflate(R.layout.item_history_video, parent, false);
                historyHolder = new ItemHolder(view);
                view.setOnClickListener(HistoryActivity.this);
                view.setTag(historyHolder);
            }
            return historyHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull HistoryHolder holder, int position) {
            Object obj = mList.get(position);
            holder.setData(obj);
            holder.index = position;
        }

        @Override
        public int getItemViewType(int position) {
            Object obj = mList.get(position);
            return (obj != null && obj instanceof String) ? 0 : 1;
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    public static class HistoryHolder<T> extends RecyclerView.ViewHolder {
        public int index;
        public HistoryHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setData(T t) {
        }
    }

    public static class ItemHolder extends HistoryHolder<History> {

        private TextView viewProgress;
        private TextView viewTitle;
        private ViewGroup root;

        public ItemHolder(View view) {
            super(view);
            root = (ViewGroup) view;
            viewProgress = root.findViewById(R.id.progress);
            viewTitle = root.findViewById(R.id.title);
        }

        public void setData(History history) {
            if (history == null) {
                root.setVisibility(View.INVISIBLE);
                return;
            }
            root.setVisibility(View.VISIBLE);
            if (history.getPosition() == history.getLength()) {
                viewProgress.setText("已看完");
            } else {
                if (history.getLength() <= 0) {
                    viewProgress.setText("已看0%");
                } else {
                    viewProgress.setText("已看" + (history.getPosition() * 100 / history.getLength()) + "%");
                }
            }
            viewTitle.setText(UriUtil.getUriName(history.getUrl()));
        }
    }

    public static class SelectionHolder extends HistoryHolder<String> {

        private TextView viewDate;

        public SelectionHolder(@NonNull View itemView) {
            super(itemView);
            viewDate = (TextView) itemView;
        }

        public void setData(String date) {
            viewDate.setText(date);
        }
    }

}
