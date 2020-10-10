package com.surecn.familymovie.common.samba;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.surecn.familymovie.common.VideoHelper;
import com.surecn.familymovie.domain.FileItem;
import com.surecn.familymovie.utils.DateUtils;
import com.surecn.familymovie.utils.UriUtil;
import com.surecn.moat.tools.log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFilenameFilter;

public class Samba2 {
//
//    public static SMBClient client = new SMBClient();
//
//
//    public static List<FileItem> listFile(String path, int filter, String user, String pass) {
//        ArrayList<FileItem> files = new ArrayList<>();
//        ArrayList<FileItem> folders = new ArrayList<>();
//        log.e("===listFile======" + String.valueOf(path));
//
//        Uri uri = Uri.parse(path);
//        try {
//            Connection connection = client.connect(uri.getHost());
//            AuthenticationContext ac = null;
//            if (TextUtils.isEmpty(user)) {
//                ac = AuthenticationContext.anonymous();
//            } else {
//                ac = new AuthenticationContext(user, pass.toCharArray(), uri.getHost());
//            }
//            Session session = connection.authenticate(AuthenticationContext.anonymous());
//            String shareDriver = null;
//            if (uri.getPathSegments().size() > 0) {
//                shareDriver = uri.getPathSegments().get(0);
//            }
//            session.connectShare()
//            try (DiskShare share = (DiskShare) session.connectShare(shareDriver)) {
////                FileAllInformation fileAllInformation = share.getFileInformation(path);
////                FileItem fileItem = new FileItem();
////                fileItem.name = UriUtil.uriNameFormat(fileAllInformation.getNameInformation());
////                fileItem.path = path;
////                fileItem.lastModify = DateUtils.toDate(fileAllInformation.getBasicInformation().getChangeTime().getWindowsTimeStamp());
////                if (!fileAllInformation.getStandardInformation().isDirectory()) {
////                    fileItem.type = 2;
////                    fileItem.extension = UriUtil.getUriExtension(fileAllInformation.getNameInformation());
////                    if (fileItem.extension != null && VideoHelper.isVideoFile(fileItem.extension.toLowerCase())) {
////                        return fileItem;
////                    }
////                } else {
////                    fileItem.type = 1;
////                    return fileItem;
////                }
//                for (FileIdBothDirectoryInformation file : share.list(path)) {
//                    if (file.getShortName().startsWith(".")) {
//                        continue;
//                    }
//                    FileItem fileItem = new FileItem();
//                    fileItem.name = UriUtil.uriNameFormat(file.getShortName());
//                    fileItem.path = file.getFileName();
//                    fileItem.lastModify = DateUtils.toDate(file.getChangeTime().getWindowsTimeStamp());
//                    log.e("=========" + file.getFileName() + "  " + file.getFileAttributes());
//                    //if (file.()) {
//                        fileItem.type = 2;
//                        fileItem.extension = UriUtil.getUriExtension(file.getShortName());
//                        if (fileItem.extension != null && VideoHelper.isVideoFile(fileItem.extension.toLowerCase())) {
//                            files.add(fileItem);
//                        }
////                    } else {
////                        if (filter == 1) {
////                            continue;
////                        }
////                        fileItem.type = 1;
////                        folders.add(fileItem);
////                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
////        SmbFile file = new SmbFile(path, ntlmPasswordAuthentication);
////        SmbFile[] list = file.listFiles();
////        if (list == null) {
////            return null;
////        }
////        for (SmbFile f : list) {
////            if (f.getName().startsWith(".")) {
////                continue;
////            }
////            FileItem fileItem = new FileItem();
////            fileItem.name = UriUtil.uriNameFormat(f.getName());
////            fileItem.path = f.getPath();
////            fileItem.lastModify = DateUtils.toDate(f.lastModified());
////            if (f.isFile()) {
////                fileItem.type = 2;
////                fileItem.extension = UriUtil.getUriExtension(f.getName());
////                if (fileItem.extension != null && VideoHelper.isVideoFile(fileItem.extension.toLowerCase())) {
////                    files.add(fileItem);
////                }
////            } else {
////                if (filter == 1) {
////                    continue;
////                }
////                fileItem.type = 1;
////                folders.add(fileItem);
////            }
////        }
//        Collections.sort(files, new Comparator<FileItem>() {
//            @Override
//            public int compare(FileItem o1, FileItem o2) {
//                return o1.name.compareTo(o2.name);
//            }
//        });
//        Collections.sort(folders, new Comparator<FileItem>() {
//            @Override
//            public int compare(FileItem o1, FileItem o2) {
//                return o1.name.compareTo(o2.name);
//            }
//        });
//        folders.addAll(files);
//        return folders;
//    }
//
//    public static FileItem getFileItem(String path, String user, String pass) {
//        Uri uri = Uri.parse(path);
//        try {
//            Connection connection = client.connect(uri.getHost());
//            AuthenticationContext ac = null;
//            if (TextUtils.isEmpty(user)) {
//                ac = AuthenticationContext.anonymous();
//            } else {
//                ac = new AuthenticationContext(user, pass.toCharArray(), uri.getHost());
//            }
//            Session session = connection.authenticate(AuthenticationContext.anonymous());
//            try (DiskShare share = (DiskShare) session.connectShare(uri.getPathSegments().get(0))) {
//                FileAllInformation fileAllInformation = share.getFileInformation(path);
//                FileItem fileItem = new FileItem();
//                fileItem.name = UriUtil.uriNameFormat(fileAllInformation.getNameInformation());
//                fileItem.path = path;
//                fileItem.lastModify = DateUtils.toDate(fileAllInformation.getBasicInformation().getChangeTime().getWindowsTimeStamp());
//                if (!fileAllInformation.getStandardInformation().isDirectory()) {
//                    fileItem.type = 2;
//                    fileItem.extension = UriUtil.getUriExtension(fileAllInformation.getNameInformation());
//                    if (fileItem.extension != null && VideoHelper.isVideoFile(fileItem.extension.toLowerCase())) {
//                        return fileItem;
//                    }
//                } else {
//                    fileItem.type = 1;
//                    return fileItem;
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public static String searchSubtitle(String filepath) {
//        try {
//            String _name = UriUtil.getUriSimpleName(filepath);
//            String path = UriUtil.getUriParent(filepath);
//            SmbFile smbFile = new SmbFile(path);
//            SmbFile[] files = smbFile.listFiles(new SmbFilenameFilter() {
//                @Override
//                public boolean accept(SmbFile dir, String name) throws SmbException {
//                    return name.toLowerCase().startsWith(_name.toLowerCase()) &&
//                            (name.toLowerCase().endsWith("ass") ||
//                                    name.toLowerCase().endsWith("scc") ||
//                                    name.toLowerCase().endsWith("srt") ||
//                                    name.toLowerCase().endsWith("ttml") ||
//                                    name.toLowerCase().endsWith("stl"));
//                }
//            });
//            if (files.length > 0) {
//                return files[0].getPath();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
