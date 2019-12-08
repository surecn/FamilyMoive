package com.surecn.familymovie.ui.live;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.surecn.familymovie.R;
import com.surecn.familymovie.domain.ChannelProgram;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-08
 * Time: 15:31
 */
public class ThirdChannelAdapter extends RecyclerView.Adapter<ThirdChannelAdapter.ItemHolder>  {

    private Context mContext;

    private List<ChannelProgram> mList;

    private int mSelectIndex = -1;

    private static SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("HH:mm");

    public ThirdChannelAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(int index, ArrayList<ChannelProgram> list) {
        mSelectIndex = index;
        this.mList = list;
        notifyDataSetChanged();
    }

    public List<ChannelProgram> getData() {
        return mList;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_live_program, parent, false);
        ItemHolder fileItemHolder = new ItemHolder(view, viewType);
        return fileItemHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemHolder holder, final int position) {
        holder.setData(mList.get(position), position, mSelectIndex);
        holder.index = position;
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        private TextView viewTime;
        private TextView viewTitle;
        private View root;

        public int index;

        public ItemHolder(@NonNull View itemView, int itemType) {
            super(itemView);
            root = itemView;
            viewTime = itemView.findViewById(R.id.time);
            viewTitle = itemView.findViewById(R.id.title);
        }

        public void setData(ChannelProgram program, int index, int selectIndex) {
            root.setSelected(index == selectIndex);
            viewTitle.setText(program.getTitle());
            viewTitle.setSelected(index == selectIndex);
            viewTime.setText(mSimpleDateFormat.format(program.getStartTime()));
        }
    }

}
