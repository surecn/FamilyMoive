package com.surecn.familymoive.common;

import android.os.Environment;

import com.surecn.familymoive.domain.FileItem;
import com.surecn.familymoive.utils.DateUtils;
import com.surecn.familymoive.utils.UriUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jcifs.smb.SmbException;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-10-26
 * Time: 13:49
 */
public class FileManager {

    public static ArrayList<String> extensions = new ArrayList<>(); {
        extensions.add("avi");
        extensions.add("wmv");
        extensions.add("rm");
        extensions.add("rmvb");
        extensions.add("mpeg1");
        extensions.add("mpeg2");
        extensions.add("mp4");//MPEG4
        extensions.add("3gp");
        extensions.add("asf");
        extensions.add("swf");
        extensions.add("vob");
        extensions.add("dat");
        extensions.add("mov");
        extensions.add("m4v");
        extensions.add("flv");
        extensions.add("f4v");
        extensions.add("mkv");
        extensions.add("mts");
        extensions.add("ts");
        //AVI、WMV、RM、RMVB、MPEG1、MPEG2、MPEG4(MP4)、3GP、ASF、SWF、VOB、DAT、MOV、M4V、FLV、F4V、MKV、MTS、TS
    }

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
                if (fileItem.extension != null && extensions.contains(fileItem.extension.toLowerCase())) {
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
}
