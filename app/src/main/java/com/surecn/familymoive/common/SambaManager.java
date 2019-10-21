package com.surecn.familymoive.common;

import android.content.ContentUris;
import android.content.Context;
import android.content.UriMatcher;
import android.net.Uri;

import com.surecn.familymoive.common.samba.IConfig;
import com.surecn.familymoive.common.samba.SambaHelper;
import com.surecn.familymoive.common.samba.SambaUtil;
import com.surecn.familymoive.ui.FileActivity;
import com.surecn.familymoive.utils.DateUtils;
import com.surecn.familymoive.utils.UriUtil;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-10-13
 * Time: 19:07
 */
public class SambaManager {

    private IConfig mConfig;

    private String curRemoteFolder;

    public SambaManager(Context context, IConfig config) {
        this.mConfig = config;
        this.mConfig = config;
    }

    public void listRoot() {
        curRemoteFolder = SambaUtil.getSmbRootURL(mConfig);
    }

    public String getHost() {
        return mConfig.host;
    }

    public String getCurrentRemoteFolder() {
        return curRemoteFolder;
    }

    public List<FileActivity.FileItem> listWorkGroup() {
        ArrayList<FileActivity.FileItem> list = new ArrayList<>();
        String[] paths = null;
        try {
            paths = SambaHelper.listWorkGroupPath();
        } catch (Exception e) {

        }
        for (String p : paths) {
            FileActivity.FileItem fileItem = new FileActivity.FileItem();
            fileItem.name = p;
            fileItem.path = p;
            fileItem.type = 0;
            list.add(fileItem);
        }
        return list;
    }

    public List<FileActivity.FileItem> listServer() {
        ArrayList<FileActivity.FileItem> list = new ArrayList<>();
        String[] paths = null;
        try {
            paths = SambaHelper.listServerPath();
        } catch (Exception e) {

        }
        for (String p : paths) {
            FileActivity.FileItem fileItem = new FileActivity.FileItem();
            fileItem.name = p;
            fileItem.path = p;
            fileItem.type = 0;
            list.add(fileItem);
        }
        return list;
    }

    public List<FileActivity.FileItem> setCurrentFolder(String path) {
        curRemoteFolder = path;
        return listFile(curRemoteFolder);
    }

    public List<FileActivity.FileItem> listFile() {
        return listFile(curRemoteFolder);
    }

    public ArrayList<FileActivity.FileItem> smbToFileItem(List<SmbFile> list) {
        ArrayList<FileActivity.FileItem> res = new ArrayList<>();
        for (SmbFile file : list) {
            if (file.getName().startsWith(".")) {
                continue;
            }
            FileActivity.FileItem fileItem = new FileActivity.FileItem();
            fileItem.name = file.getName();
            fileItem.path = file.getPath();
            fileItem.lastModify = DateUtils.toDate(file.getLastModified());
            try {
                if (file.isFile()) {
                    fileItem.type = 2;
                    fileItem.extension = UriUtil.getUriExtension(file.getName());
                } else {
                    fileItem.type = 1;
                }
            } catch (SmbException e) {
                e.printStackTrace();
            }
            res.add(fileItem);
        }
        return res;
    }

    public List<FileActivity.FileItem> listFile(String path) {
        try {
            return smbToFileItem(SambaHelper.listFiles(mConfig, path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
