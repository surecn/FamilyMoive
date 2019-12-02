package com.surecn.familymovie.ui.live;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.internal.LinkedTreeMap;
import com.surecn.familymovie.R;
import com.surecn.familymovie.domain.Channel;
import com.surecn.familymovie.domain.LiveRoot;
import com.surecn.moat.core.Schedule;
import com.surecn.moat.core.TaskSchedule;
import com.surecn.moat.core.task.Task;
import com.surecn.moat.core.task.UITask;
import com.surecn.moat.tools.log;

import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-08
 * Time: 15:31
 */
public class SecondChannelAdapter extends RecyclerView.Adapter<SecondChannelAdapter.ItemHolder> implements View.OnClickListener, View.OnFocusChangeListener {

    private Context mContext;

    private OnChannelChangeListener mOnChannelChangeListener;

    private OnSectionChangeListener mOnSectionChangeListener;

    private List<Channel> mList;

    private List<LiveRoot> mSection;

    private int mSelectIndex = -1;

    private int mFocusIndex = -1;

    private boolean mRequestFocus = false;

    private boolean mUpdateProgram = true;

    public static interface OnChannelChangeListener {
        void onChannelChange(Channel channel);
    }

    public static interface OnSectionChangeListener {
        void onSectionChange(int index);
    }

    public SecondChannelAdapter(Context context) {
        this.mContext = context;
    }

    public void setOnChannelChangeListener(OnChannelChangeListener listener) {
        this.mOnChannelChangeListener = listener;
    }

    public void setOnSectionChangeListener(OnSectionChangeListener listener) {
        this.mOnSectionChangeListener = listener;
    }

    public void setData(List<LiveRoot> section, List<Channel> list) {
        this.mSection = section;
        this.mList = list;
    }

    public List<Channel> getData() {
        return mList;
    }

    public void setSelectIndex(int selectIndex, boolean notify) {
        if (this.mSelectIndex >= 0) {
            this.notifyItemChanged(this.mSelectIndex);
        }
        this.mSelectIndex = selectIndex;
        this.notifyItemChanged(selectIndex);
        if (this.mOnChannelChangeListener != null && notify) {
            this.mOnChannelChangeListener.onChannelChange(mList.get(selectIndex));
        }
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_live_second, parent, false);
            ItemHolder fileItemHolder = new ItemHolder(view, viewType);
            view.setOnClickListener(this);
            view.setOnFocusChangeListener(this);
            return fileItemHolder;
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_live_channel_section, parent, false);
            ItemHolder fileItemHolder = new ItemHolder(view, viewType);
            return fileItemHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemHolder holder, final int position) {
        final Channel channel = mList.get(position);
        holder.setData(mList.get(position), position, mSelectIndex);
        holder.index = position;
        holder.itemView.setTag(position);
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
    public int getItemViewType(int position) {
        return mList.get(position).getSection();
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public void setRequestFocus(boolean requestFocus) {
        this.mRequestFocus = requestFocus;
        notifyItemChanged(mSelectIndex);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.getTag() == null) {
            return;
        }
        int index = (int) v.getTag();
        v.findViewById(R.id.program).setSelected(hasFocus);
        if (!hasFocus) {
            return;
        }
        mFocusIndex = index;
        int section = -1;
        for (int i = mSection.size() - 1; i >= 0; i--) {
            if (mSection.get(i).getChannelPosition() <= index) {
                section = i;
                break;
            }
        }
        if (section >= 0) {
            mOnSectionChangeListener.onSectionChange(section);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() == null) {
            return;
        }
        int index = (int) v.getTag();
        if (index == mSelectIndex) {
            return;
        }
        notifyItemChanged(mSelectIndex);
        mSelectIndex = index;
        notifyItemChanged(index);
        Channel channel = mList.get(index);
        if (mOnChannelChangeListener != null) {
            mOnChannelChangeListener.onChannelChange(channel);
        }
    }

    public Channel getCurrentChannel() {
        if (mList == null) {
            return null;
        }
        return mList.get(mSelectIndex);
    }

    public Channel getFocusChannel() {
        return mList.get(mSelectIndex);
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        private TextView viewId;
        private TextView viewTitle;
        private TextView viewProgram;
        private View root;

        public int index;

        public ItemHolder(@NonNull View itemView, int itemType) {
            super(itemView);
            if (itemType == 0) {
                root = itemView;
                viewId = itemView.findViewById(R.id.channel);
                viewTitle = itemView.findViewById(R.id.title);
                viewProgram = itemView.findViewById(R.id.program);
            } else {
                root = itemView;
                viewTitle = (TextView) itemView;
            }
        }

        public void setData(final Channel channel, int index, int selectIndex) {
            if (channel.getSection() == 0) {
                viewId.setText(String.format("%04d", channel.getId()));
                viewTitle.setText(channel.getTitle());

                Map.Entry entry = channel.getProgramMaps().floorEntry(System.currentTimeMillis());
                if (entry != null)
                viewProgram.setText(channel.getProgramMaps().floorEntry(System.currentTimeMillis()).getValue().getTitle());
                else
                    viewProgram.setText(null);
                root.setSelected(selectIndex == index ? true : false);

                viewProgram.setSelected(root.hasFocus() ? true : false);
            } else {
                viewTitle.setText(channel.getTitle());
            }
        }
    }

}
