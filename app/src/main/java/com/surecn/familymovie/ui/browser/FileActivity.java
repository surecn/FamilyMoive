package com.surecn.familymovie.ui.browser;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.surecn.familymovie.R;
import com.surecn.familymovie.domain.FileItem;
import com.surecn.familymovie.ui.base.TitleActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
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
        mList = new ArrayList<>();
        mAdapter = new FileAdapter();
        mViewList.setAdapter(mAdapter);
    }

    protected ArrayList<FileItem> getData() {
        return mList;
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
            View view = LayoutInflater.from(FileActivity.this).inflate(R.layout.item_file_list, parent, false);
            FileItemHolder fileItemHolder = new FileItemHolder(view);
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

    public static class FileItemHolder extends RecyclerView.ViewHolder {

        private static HashMap<String, Integer> extensionIcons = new HashMap<>(); {
            extensionIcons.put("aac", R.mipmap.file_icon_aac);
            extensionIcons.put("apk", R.mipmap.file_icon_apk);
            extensionIcons.put("avi", R.mipmap.file_icon_avi);
            extensionIcons.put("bt", R.mipmap.file_icon_bt);
            extensionIcons.put("excel", R.mipmap.file_icon_excel);
            extensionIcons.put("flac", R.mipmap.file_icon_flac);
            extensionIcons.put("flv", R.mipmap.file_icon_flv);
            extensionIcons.put("gif", R.mipmap.file_icon_gif);
            extensionIcons.put("gpk", R.mipmap.file_icon_gpk);
            extensionIcons.put("jpg", R.mipmap.file_icon_jpg);
            extensionIcons.put("mid", R.mipmap.file_icon_mid);
            extensionIcons.put("mkv", R.mipmap.file_icon_mkv);
            extensionIcons.put("mp3", R.mipmap.file_icon_mp3);
            extensionIcons.put("mp4", R.mipmap.file_icon_mp4);
            extensionIcons.put("pdf", R.mipmap.file_icon_pdf);
            extensionIcons.put("png", R.mipmap.file_icon_png);
            extensionIcons.put("ppt", R.mipmap.file_icon_ppt);
            extensionIcons.put("rar", R.mipmap.file_icon_rar);
            extensionIcons.put("rmvb", R.mipmap.file_icon_rmvb);
            extensionIcons.put("rm", R.mipmap.file_icon_rmvb);
            extensionIcons.put("txt", R.mipmap.file_icon_txt);
            extensionIcons.put("wav", R.mipmap.file_icon_wav);
            extensionIcons.put("wma", R.mipmap.file_icon_wma);
            extensionIcons.put("wmw", R.mipmap.file_icon_wmw);
            extensionIcons.put("zip", R.mipmap.file_icon_zip);

        }

        private TextView viewText;

        private TextView viewTime;

        private ImageView imageView;

        public int index;

        public FileItemHolder(@NonNull View itemView) {
            super(itemView);
            ViewGroup viewGroup = (ViewGroup) itemView;
            viewText = viewGroup.findViewById(R.id.text);
            imageView = viewGroup.findViewById(R.id.icon);
            viewTime = viewGroup.findViewById(R.id.date);
            viewGroup.setTag(this);
        }

        public void setData(FileItem file) {
            viewText.setText(file.name);
            if (file.type == 1) {
                imageView.setImageResource(R.mipmap.file_icon_folder);
                viewTime.setText(file.lastModify);
            } else if (file.type == 0) {
                imageView.setImageResource(R.mipmap.file_icon_lanfolder);
                viewTime.setText("");
            } else {
                Integer integer = extensionIcons.get(file.extension);
                if (integer == null) {
                    integer = R.mipmap.file_icon_default;
                }
                imageView.setImageResource(integer);
                viewTime.setText(file.lastModify);
            }
        }
    }


}