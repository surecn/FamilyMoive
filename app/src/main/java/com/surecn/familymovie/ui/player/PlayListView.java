package com.surecn.familymovie.ui.player;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.surecn.familymovie.R;
import com.surecn.familymovie.common.ListPaddingDecoration;
import com.surecn.familymovie.domain.FileItem;
import com.surecn.familymovie.utils.UriUtil;
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

    private ListAdapter mAdapter;

    private WeakReference<VideoActivity> mActivityRef;

    private LinearLayoutManager mLinearLayoutManager;

    private int mSelectIndex;

    public PlayListView(@NonNull Context context) {
        this(context, null);
    }

    public PlayListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public PlayListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean requestChildRectangleOnScreen(@NonNull RecyclerView parent, @NonNull View child, @NonNull Rect rect, boolean immediate, boolean focusedChildVisible) {
                super.requestChildRectangleOnScreen(parent, child, rect, true, focusedChildVisible);
                return false;
            }

            @Override
            public boolean onRequestChildFocus(@NonNull RecyclerView parent, @NonNull State state, @NonNull View child, @Nullable View focused) {
                return super.onRequestChildFocus(parent, state, child, focused);
            }
        };
        setLayoutManager(mLinearLayoutManager);
//        addItemDecoration(new ListPaddingDecoration(getContext().getResources().getDimensionPixelSize(R.dimen.video_list_space)));
        mAdapter = new ListAdapter();
        setAdapter(mAdapter);
        setItemAnimator(null);
        setPreserveFocusAfterLayout(false);
    }

    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        return false;
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == GONE) {
            clearFocus();
        }
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
        try {
            mAdapter.notifyDataSetChanged();
            mLinearLayoutManager.scrollToPosition(mSelectIndex);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        mLinearLayoutManager.scrollToPosition(mSelectIndex);
        mAdapter.notifyItemChanged(mSelectIndex);
    }

    public class ListAdapter extends RecyclerView.Adapter<ItemHolder> implements View.OnClickListener,View.OnFocusChangeListener {

        @NonNull
        @Override
        public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_video_list, parent, false);
            ItemHolder itemHolder = new ItemHolder(view);
            view.setOnFocusChangeListener(this);
            view.setOnClickListener(this);
            return itemHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull final ItemHolder holder, final int position) {
            holder.setData(mList.get(position), position, mSelectIndex);
            holder.index = position;
            holder.itemView.setTag(position);
            if (position == mSelectIndex) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.itemView.requestFocus();
                    }
                }, 0);
            }
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                int position = (int) v.getTag();
                mSelectTitleView.setText(UriUtil.getUriName(mList.get(position).path));
            }
        }

        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            String url = mList.get(position).path;
            if (mActivityRef.get() != null) {
                mActivityRef.get().startVideo(url, 0);
            }
        }
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        private CheckedTextView viewText;

        public int index;

        public ItemHolder(@NonNull View itemView) {
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
