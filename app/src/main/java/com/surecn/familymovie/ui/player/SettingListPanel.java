package com.surecn.familymovie.ui.player;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.surecn.familymovie.R;
import com.surecn.familymovie.common.ListPaddingDecoration;

import java.lang.ref.WeakReference;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-03
 * Time: 19:25
 */
public class SettingListPanel extends LinearLayout {

    public final static int TYPE_AUDIO = 0;
    public final static int TYPE_SUBTITLE = 1;

    private WeakReference<VideoActivity> mActivityRef;

    private RecyclerView mListView;
    private TextView mTextView;

    private SettingAdapter mAdapter;
    private List<TrackItem> mList;

    private int mType;

    private int mSelectIndex;

    public SettingListPanel(Context context) {
        super(context);
    }

    public SettingListPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SettingListPanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SettingListPanel(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mActivityRef = new WeakReference<>((VideoActivity)getContext());
        mListView = findViewById(R.id.list);
        mTextView = findViewById(R.id.list_title);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean requestChildRectangleOnScreen(@NonNull RecyclerView parent, @NonNull View child, @NonNull Rect rect, boolean immediate, boolean focusedChildVisible) {
                return super.requestChildRectangleOnScreen(parent, child, rect, true, focusedChildVisible);
            }
        };
        mListView.setLayoutManager(linearLayoutManager);
        mListView.addItemDecoration(new ListPaddingDecoration(getContext().getResources().getDimensionPixelSize(R.dimen.video_list_space)));
        mAdapter = new SettingAdapter();
        mListView.setAdapter(mAdapter);
    }

    public void setData(int type, String title, List<TrackItem> list, int selectIndex) {
        this.mType = type;
        mTextView.setText(title);
        mList = list;
        this.mSelectIndex = selectIndex;
        mAdapter.notifyDataSetChanged();
    }

    public class SettingAdapter extends RecyclerView.Adapter<FileItemHolder> implements View.OnClickListener {

        @NonNull
        @Override
        public FileItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_video_setting_list, parent, false);
            FileItemHolder fileItemHolder = new FileItemHolder(view);
            view.setOnClickListener(this);
            return fileItemHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull final FileItemHolder holder, final int position) {
            holder.setData(mList.get(position), position == mSelectIndex);
            holder.index = position;
            holder.itemView.setTag(position);
            if (position == mSelectIndex && SettingListPanel.this.getVisibility() == VISIBLE) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.itemView.requestFocus();
                    }
                }, 100);
            }
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }

        @Override
        public void onClick(View v) {
            VideoActivity videoActivity = mActivityRef.get();
            if (videoActivity == null) {
                return;
            }
            int index = (Integer) v.getTag();
            mSelectIndex = index;
            videoActivity.selectStream(mType, index);
            videoActivity.hideAll();
        }
    }

    public class FileItemHolder extends RecyclerView.ViewHolder {

        private ImageView viewIcon;
        private TextView viewTag;
        private TextView viewText;

        public int index;

        public FileItemHolder(@NonNull View itemView) {
            super(itemView);
            viewText = itemView.findViewById(R.id.text);
            viewIcon = itemView.findViewById(R.id.checked);
            viewTag = itemView.findViewById(R.id.tag);
        }

        public void setData(TrackItem item, boolean selected) {
            viewIcon.setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
            if (item.tag == 1) {
                viewTag.setVisibility(View.VISIBLE);
                viewTag.setText(R.string.video_setting_list_local);
            } else if (item.tag == 2) {
                viewTag.setVisibility(View.VISIBLE);
                viewTag.setText(R.string.video_setting_list_network);
            } else {
                viewTag.setVisibility(View.GONE);
            }
            if (TextUtils.isEmpty(item.text) ||  "und".equals(item.text)) {
                viewText.setText(R.string.unknow);
            } else {
                viewText.setText(item.text);
            }
        }
    }

    public static class TrackItem {
        int streamIndex;
        int tag;
        String text;
        String value;
        public TrackItem(int streamIndex, String text, String value, int tag) {
            this.streamIndex = streamIndex;
            this.text = text;
            this.tag = tag;
            this.value = value;
        }
    }
}
