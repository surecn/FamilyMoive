package com.surecn.familymoive.ui.player;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.surecn.familymoive.R;
import com.surecn.familymoive.common.ListPaddingDecoration;
import com.surecn.familymoive.domain.FileItem;
import com.surecn.moat.base.BaseDialog;
import com.surecn.moat.tools.log;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-03
 * Time: 08:12
 */
public class PlayListDialog extends BaseDialog {

    private List<FileItem> mList;

    private int mSelectIndex;

    private RecyclerView mViewList;

    private TextView mViewListTitle;

    public PlayListDialog(Context context, List<FileItem> list, int selectIndex) {
        super(context);
        this.mList = list;
        this.mSelectIndex = selectIndex;
    }

    @Override
    public int layout() {
        return R.layout.dialog_playlist;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(layoutParams);

        mViewList = (RecyclerView) findViewById(R.id.list);
        mViewListTitle = (TextView) findViewById(R.id.list_title);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean requestChildRectangleOnScreen(@NonNull RecyclerView parent, @NonNull View child, @NonNull Rect rect, boolean immediate, boolean focusedChildVisible) {
                return super.requestChildRectangleOnScreen(parent, child, rect, true, focusedChildVisible);
            }
        };
        log.e("====onCreate=======");
        mViewList.setLayoutManager(linearLayoutManager);
        mViewList.addItemDecoration(new ListPaddingDecoration(getContext().getResources().getDimensionPixelSize(R.dimen.video_list_space)));
    }

//    public String getNextPath() {
//        if (mAdapter.mPosition < mList.size()) {
//            return mList.get(mAdapter.mPosition + 1).path;
//        }
//        return null;
//    }



}
