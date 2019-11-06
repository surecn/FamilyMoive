package com.surecn.familymoive.common;

import com.surecn.familymoive.common.samba.IConfig;
import com.surecn.familymoive.common.samba.SambaHelper;
import com.surecn.familymoive.common.samba.SambaUtil;
import com.surecn.familymoive.domain.FileItem;
import com.surecn.familymoive.utils.DateUtils;
import com.surecn.familymoive.utils.UriUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFilenameFilter;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-10-13
 * Time: 19:07
 */
public class SambaManager {

    private IConfig mConfig;

    private String curRemoteFolder;

    private FileItem curFile;

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


    public SambaManager(IConfig config) {
        this.mConfig = config;
    }

    public void updateHost(String host) {
        mConfig.updateHost(host);
        curRemoteFolder = SambaUtil.getSmbRootURL(mConfig);
    }

    public String getHost() {
        return mConfig.host;
    }

    public String getCurrentRemoteFolder() {
        return curRemoteFolder;
    }

    public List<FileItem> listWorkGroup() {
        ArrayList<FileItem> list = new ArrayList<>();
        String[] paths = null;
        try {
            paths = SambaHelper.listWorkGroupPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (paths != null) {
            for (String p : paths) {
                FileItem fileItem = new FileItem();
                fileItem.name = UriUtil.uriNameFormat(p);
                fileItem.path = p;
                fileItem.type = 0;
                list.add(fileItem);
            }
        }
        curRemoteFolder = null;
        return list;
    }

//    public List<FileItem> listServer() {
//        ArrayList<FileItem> list = new ArrayList<>();
//        String[] paths = null;
//        try {
//            paths = SambaHelper.listServerPath();
//        } catch (Exception e) {
//
//        }
//        for (String p : paths) {
//            FileItem fileItem = new FileItem();
//            fileItem.name = UriUtil.uriNameFormat(p);
//            fileItem.path = UriUtil.uriNameFormat(p);
//            fileItem.type = 0;
//            list.add(fileItem);
//        }
//        return list;
//    }

    public List<FileItem> setCurrentFolder(String path) {
        curRemoteFolder = path;
        return listAll(curRemoteFolder);
    }

    public List<FileItem> listAll() {
        return listAll(curRemoteFolder);
    }

    public ArrayList<FileItem> smbToFileItem(List<SmbFile> list, int filter) {
        ArrayList<FileItem> files = new ArrayList<>();
        ArrayList<FileItem> folders = new ArrayList<>();
        for (SmbFile file : list) {
            //隐藏文件
            if (file.getName().startsWith(".")) {
                continue;
            }
            FileItem fileItem = new FileItem();
            fileItem.name = UriUtil.uriNameFormat(file.getName());
            fileItem.path = file.getPath();
            fileItem.lastModify = DateUtils.toDate(file.getLastModified());
            try {
                if (file.isFile()) {
                    fileItem.type = 2;
                    fileItem.extension = UriUtil.getUriExtension(file.getName());
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
            } catch (SmbException e) {
                e.printStackTrace();
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

    public List<FileItem> listAll(String path) {
        try {
            return smbToFileItem(SambaHelper.listFiles(mConfig, path), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<FileItem> listFile(String path) {
        try {
            return smbToFileItem(SambaHelper.listFiles(mConfig, path), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String searchSubtitle(String filepath) {
        try {
            String _name = UriUtil.getUriSimpleName(filepath);
            String path = UriUtil.getUriParent(filepath);
            SmbFile smbFile = new SmbFile(SambaUtil.wrapSmbFullURL(mConfig, path));
            SmbFile[] files = smbFile.listFiles(new SmbFilenameFilter() {
                @Override
                public boolean accept(SmbFile dir, String name) throws SmbException {
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
