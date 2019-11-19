package com.surecn.familymovie.ui.live;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import com.surecn.familymovie.R;
import com.surecn.familymovie.domain.LiveRoot;
import com.surecn.moat.tools.log;

import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-08
 * Time: 15:31
 */
public class FirstChannelAdapter extends RecyclerView.Adapter<FirstChannelAdapter.ItemHolder> implements View.OnFocusChangeListener {

    private Context mContext;

    private View.OnFocusChangeListener mOnFocusChangeListener;

    private List<LiveRoot> mList;

    private int mSelectIndex = -1;

    private RecyclerView recyclerView;

    private boolean mRequestFocus = false;


    public FirstChannelAdapter(Context context, RecyclerView recyclerView, View.OnFocusChangeListener onFocusChangeListener) {
        this.mContext = context;
        this.recyclerView = recyclerView;
        this.mOnFocusChangeListener = onFocusChangeListener;
    }

    public void setSelectIndex(int selectIndex) {
        if (selectIndex == mSelectIndex) {
            return;
        }
        if (this.mSelectIndex >= 0) {
            this.notifyItemChanged(this.mSelectIndex);
        }
        this.mSelectIndex = selectIndex;
        this.notifyItemChanged(selectIndex);
    }

    public void setData(List<LiveRoot> list) {
        mList = list;
    }

    public void setRequestFocus(boolean requestFocus) {
        this.mRequestFocus = requestFocus;
        notifyItemChanged(mSelectIndex);
    }

    public List<LiveRoot> getData() {
        return mList;
    }

    public int getSelectIndex() {
        return mSelectIndex;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_live_first, parent, false);
        ItemHolder fileItemHolder = new ItemHolder(view);
        view.setOnFocusChangeListener(this);
        return fileItemHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemHolder holder, final int position) {
        holder.setData(mList.get(position), position, mSelectIndex);
        holder.index = position;
        holder.itemView.setTag(holder);
        if (mRequestFocus && position == mSelectIndex) {
            holder.itemView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    holder.itemView.requestFocus();
                    mRequestFocus = false;
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
            final int oldSelect = mSelectIndex;
            ItemHolder itemHolder = (ItemHolder) v.getTag();
            mSelectIndex = itemHolder.index;
            v.post(new Runnable() {
                @Override
                public void run() {
                    notifyItemChanged(oldSelect);
                    notifyItemChanged(mSelectIndex);
                }
            });
        }
        if (mOnFocusChangeListener != null) {
            mOnFocusChangeListener.onFocusChange(v, hasFocus);
        }
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        private CheckedTextView viewText;

        public int index;

        public LiveRoot liveRoot;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            viewText = (CheckedTextView) itemView;
        }

        public void setData(LiveRoot item, int index, int selectIndex) {
            this.liveRoot = item;
            this.index = index;
            viewText.setText(item.getTitle());
            viewText.setSelected(index == selectIndex);
        }
    }
}
