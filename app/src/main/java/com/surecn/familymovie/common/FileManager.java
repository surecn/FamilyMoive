package com.surecn.familymovie.common;

import com.surecn.familymovie.domain.FileItem;
import com.surecn.familymovie.utils.DateUtils;
import com.surecn.familymovie.utils.UriUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFilenameFilter;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-10-26
 * Time: 13:49
 */
public class FileManager {

    public static List<FileItem> listFile(String path, int filter) {
        File file = new File(path);
        ArrayList<FileItem> files = new ArrayList<>();
        ArrayList<FileItem> folders = new ArrayList<>();
        if (file.listFiles() == null) {
            return null;
        }
        for (File f : file.listFiles()) {
            if (f.getName().startsWith(".")) {
                continue;
            }
            FileItem fileItem = new FileItem();
            fileItem.name = UriUtil.uriNameFormat(f.getName());
            fileItem.path = f.getPath();
            fileItem.lastModify = DateUtils.toDate(f.lastModified());
            if (f.isFile()) {
                fileItem.type = 2;
                fileItem.extension = UriUtil.getUriExtension(f.getName());
                if (fileItem.extension != null && VideoHelper.isVideoFile(fileItem.extension)) {
                    files.add(fileItem);
                }
            } else {
                if (filter == 1) {
                    continue;
                }
                fileItem.type = 1;
                folders.add(fileItem);
            }
        }
        Collections.sort(files, new Comparator<FileItem>() {
            @Override
            public int compare(FileItem o1, FileItem o2) {
                return o1.name.compareTo(o2.name);
            }
        });
        Collections.sort(folders, new Comparator<FileItem>() {
            @Override
            public int compare(FileItem o1, FileItem o2) {
                return o1.name.compareTo(o2.name);
            }
        });
        folders.addAll(files);
        return folders;
    }

    public static FileItem getFileItem(String path) {
        File f = new File(path);
        FileItem fileItem = new FileItem();
        fileItem.name = UriUtil.uriNameFormat(f.getName());
        fileItem.path = f.getPath();
        fileItem.lastModify = DateUtils.toDate(f.lastModified());
        if (f.isFile()) {
            fileItem.type = 2;
            fileItem.extension = UriUtil.getUriExtension(f.getName());
//            if (fileItem.extension != null && VideoHelper.isVideoFile(fileItem.extension)) {
//                files.add(fileItem);
//            }
        } else {
            fileItem.type = 1;
        }
        return fileItem;
    }

    public static String searchSubtitle(String filepath) {
        try {
            String _name = UriUtil.getUriSimpleName(filepath);
            String path = UriUtil.getUriParent(filepath);
            File file = new File(path);
            File[] files = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    String name = pathname.getName();
                    return name.toLowerCase().startsWith(_name.toLowerCase()) &&
                            (name.toLowerCase().endsWith("ass") ||
                                    name.toLowerCase().endsWith("scc") ||
                                    name.toLowerCase().endsWith("srt") ||
                                    name.toLowerCase().endsWith("ttml") ||
                                    name.toLowerCase().endsWith("stl"));
                }
            });
            if (files.length > 0) {
                return files[0].getPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
