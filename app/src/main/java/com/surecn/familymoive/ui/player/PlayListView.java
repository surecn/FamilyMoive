package com.surecn.familymoive.ui.player;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.surecn.familymoive.R;
import com.surecn.familymoive.common.ListPaddingDecoration;
import com.surecn.familymoive.domain.FileItem;
import com.surecn.familymoive.utils.UriUtil;
import com.surecn.moat.tools.log;

import java.lang.ref.WeakReference;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-03
 * Time: 14:33
 */
public class PlayListView extends RecyclerView {

    private List<FileItem> mList;

    private TextView mSelectTitleView;

    private FileAdapter mFileAdapter;

    private WeakReference<VideoActivity> mActivityRef;

    private int mSelectIndex;

    public PlayListView(@NonNull Context context) {
        this(context, null);
    }

    public PlayListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public PlayListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean requestChildRectangleOnScreen(@NonNull RecyclerView parent, @NonNull View child, @NonNull Rect rect, boolean immediate, boolean focusedChildVisible) {
                return super.requestChildRectangleOnScreen(parent, child, rect, true, focusedChildVisible);
            }
        };
        setLayoutManager(linearLayoutManager);
        addItemDecoration(new ListPaddingDecoration(getContext().getResources().getDimensionPixelSize(R.dimen.video_list_space)));
        mFileAdapter = new FileAdapter();
        setAdapter(mFileAdapter);
    }

    public String getNextPath() {
        if (mList == null) {
            return null;
        }
        if (mSelectIndex + 1 >= mList.size()) {
            return null;
        }
        return mList.get(mSelectIndex + 1).path;
    }

    public int getSelectIndex() {
        return mSelectIndex;
    }

    public void setData(VideoActivity activity, List<FileItem> list, TextView selectTitleView, int selectIndex) {
        mActivityRef = new WeakReference<>(activity);
        this.mSelectTitleView = selectTitleView;
        this.mList = list;
        this.mSelectIndex = selectIndex;
        log.e("====setData====" + selectIndex);
        try {
            mFileAdapter.notifyDataSetChanged();
            scrollToPosition(selectIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class FileAdapter extends RecyclerView.Adapter<FileItemHolder> implements View.OnClickListener,View.OnFocusChangeListener {

        @NonNull
        @Override
        public FileItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_video_list, parent, false);
            FileItemHolder fileItemHolder = new FileItemHolder(view);
            view.setOnFocusChangeListener(this);
            view.setOnClickListener(this);
            return fileItemHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull final FileItemHolder holder, final int position) {
            holder.setData(mList.get(position), position, mSelectIndex);
            holder.index = position;
            holder.itemView.setTag(position);
            if (position == mSelectIndex && getVisibility() == VISIBLE) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.itemView.requestFocus();
                    }
                }, 600);
            }
        }

        @Override
        public int getItemCount() {
            log.e("=====getItemCount=======" + (mList == null ? 0 : mList.size()));
            return mList == null ? 0 : mList.size();
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                int position = (int) v.getTag();
                log.e("=======onFocusChange=========" + UriUtil.getUriName(mList.get(position).path));
                mSelectTitleView.setText(UriUtil.getUriName(mList.get(position).path));
            }
        }

        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            String url = mList.get(position).path;
            if (mActivityRef.get() != null) {
                mActivityRef.get().initVideo(url, 0);
            }
//            VideoActivity.startActivity(getContext(), , 0);
        }
    }

    public static class FileItemHolder extends RecyclerView.ViewHolder {

        private CheckedTextView viewText;

        public int index;

        public FileItemHolder(@NonNull View itemView) {
            super(itemView);
            viewText = (CheckedTextView) itemView;
            viewText.setTag(this);
        }

        public void setData(FileItem file, int index, int selectIndex) {
            viewText.setText("视频" + (index + 1));
            viewText.setChecked(index == selectIndex);
        }
    }

}
