package com.surecn.familymovie.ui.browser;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.surecn.familymovie.R;
import com.surecn.familymovie.Setting;
import com.surecn.familymovie.common.VideoHelper;
import com.surecn.familymovie.domain.FileItem;
import com.surecn.familymovie.ui.base.TitleActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-10-13
 * Time: 18:41
 */
public class FileActivity extends TitleActivity implements View.OnClickListener {

    private RecyclerView mViewList;

    private FileAdapter mAdapter;

    private ArrayList<FileItem> mList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        initView();
    }

    private void initView() {
        mViewList = findViewById(R.id.list);
        if (Setting.fileShowStyle == 0) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
                @Override
                public boolean requestChildRectangleOnScreen(@NonNull RecyclerView parent, @NonNull View child, @NonNull Rect rect, boolean immediate, boolean focusedChildVisible) {
                    return super.requestChildRectangleOnScreen(parent, child, rect, true, focusedChildVisible);
                }
            };
            mViewList.setLayoutManager(linearLayoutManager);
            DividerItemDecoration divider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
            divider.setDrawable(ContextCompat.getDrawable(this,R.mipmap.file_list_separator));
            mViewList.addItemDecoration(divider);
        } else {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 6) {
                @Override
                public boolean requestChildRectangleOnScreen(@NonNull RecyclerView parent, @NonNull View child, @NonNull Rect rect, boolean immediate, boolean focusedChildVisible) {
                    return super.requestChildRectangleOnScreen(parent, child, rect, true, focusedChildVisible);
                }
            };
            mViewList.setLayoutManager(gridLayoutManager);
        }
        mList = new ArrayList<>();
        mAdapter = new FileAdapter();
        mViewList.setAdapter(mAdapter);
    }

    protected ArrayList<FileItem> getData() {
        return mList;
    }

    protected void setData(ArrayList<FileItem> list) {
        mList = list;
    }

    protected void updateData(List<FileItem> list) {
        mList.clear();
        if (list != null) {
            mList.addAll(list);
        }
    }

    protected void updateList() {
        mAdapter.notifyDataSetChanged();
        mViewList.scrollToPosition(0);
    }

    @Override
    public void onClick(View v) {
        FileItemHolder fileItemHolder = (FileItemHolder) v.getTag();
        onClick(mList.get(fileItemHolder.index));
    }

    public void onClick(FileItem fileItem) {
    }

    public class FileAdapter extends RecyclerView.Adapter<FileItemHolder> {

        @NonNull
        @Override
        public FileItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(FileActivity.this).inflate(Setting.fileShowStyle == 0? R.layout.item_file_list : R.layout.item_file_grid, parent, false);
            FileItemHolder fileItemHolder = null;
            if (Setting.fileShowStyle == 0) {
                fileItemHolder = new FileListItemHolder(view);
            } else {
                fileItemHolder = new FileItemHolder(view);
            }
            view.setOnClickListener(FileActivity.this);
            return fileItemHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull FileItemHolder holder, int position) {
            holder.setData(mList.get(position));
            holder.index = position;
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    public static class FileListItemHolder extends FileItemHolder {

        private TextView viewTime;

        private FileListItemHolder(@NonNull View itemView) {
            super(itemView);
            ViewGroup viewGroup = (ViewGroup) itemView;
            viewTime = viewGroup.findViewById(R.id.date);
            viewGroup.setTag(this);
        }

        public void setData(FileItem file) {
            super.setData(file);
            if (file.type == 1) {
                viewTime.setText(file.lastModify);
            } else if (file.type == 0) {
                viewTime.setText("");
            } else {
                viewTime.setText(file.lastModify);
            }
        }
    }

    public static class FileItemHolder extends RecyclerView.ViewHolder implements View.OnFocusChangeListener {

        private TextView viewText;

        private ImageView imageView;

        public int index;

        private FileItemHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnFocusChangeListener(this);
            ViewGroup viewGroup = (ViewGroup) itemView;
            viewText = viewGroup.findViewById(R.id.text);
            imageView = viewGroup.findViewById(R.id.icon);
            viewGroup.setTag(this);
        }

        public void setData(FileItem file) {
            viewText.setText(file.name);
            if (file.type == 1) {
                imageView.setImageResource(R.mipmap.file_icon_folder);
            } else if (file.type == 0) {
                imageView.setImageResource(R.mipmap.file_icon_lanfolder);
            } else {
                Integer integer = VideoHelper.getVideoFileIcon(file.extension);
                if (integer == null) {
                    integer = R.mipmap.file_icon_default;
                }
                imageView.setImageResource(integer);
            }
        }

        @Override
        public void onFocusChange(View view, boolean b) {
            viewText.setSelected(b);
        }
    }


}
