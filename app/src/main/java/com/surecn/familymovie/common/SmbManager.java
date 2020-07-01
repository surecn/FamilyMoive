package com.surecn.familymovie.common;

import android.text.TextUtils;

import com.surecn.familymovie.domain.FileItem;
import com.surecn.familymovie.utils.DateUtils;
import com.surecn.familymovie.utils.UriUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jcifs.UniAddress;
import jcifs.netbios.NbtAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFilenameFilter;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-10-26
 * Time: 13:49
 */
public class SmbManager {

    private static Method getAddressMethod;

    static {
        try {
            getAddressMethod = SmbFile.class.getDeclaredMethod("getAddress");
            getAddressMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static FileItem getServer(String ip) {
        FileItem fileItem = null;
        try {
            NbtAddress nbtAddress = NbtAddress.getByName(ip);
            nbtAddress.firstCalledName();
            String name = nbtAddress.nextCalledName();
            SmbFile smbFile = new SmbFile("smb://"+ip+"/");
            if (!smbFile.exists()) {
                return null;
            }
            fileItem = new FileItem();
            fileItem.name = name;
            fileItem.path = smbFile.getPath();
            fileItem.type = 0;
            fileItem.canAccess = false;
            fileItem.needPass = true;
            fileItem.server = ip;
            smbFile.connect();
            fileItem.canAccess = true;
            fileItem.needPass = false;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SmbException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileItem;
    }

    public static ArrayList<FileItem> listWorkGroup() {
        ArrayList<FileItem> list = new ArrayList<>();
        SmbFile f = null;
        try {
            f = new SmbFile("smb://workgroup/");
            SmbFile[] smbFiles = f.listFiles();
            if (smbFiles != null) {
                for (SmbFile p : smbFiles) {
                    FileItem fileItem = new FileItem();
                    fileItem.name = p.getName();
                    fileItem.path = p.getCanonicalPath();
                    fileItem.type = 0;
                    fileItem.canAccess = p.getAllowUserInteraction();
                    list.add(fileItem);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SmbException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList<String> listWorkGroupIp() {
        ArrayList<String> list = new ArrayList<>();
        SmbFile f = null;
        try {
            f = new SmbFile("smb://workgroup/");
            SmbFile[] smbFiles = f.listFiles();
            if (smbFiles != null) {
                for (SmbFile p : smbFiles) {
                    FileItem fileItem = new FileItem();
                    fileItem.name = p.getName();
                    fileItem.path = p.getCanonicalPath();
                    fileItem.type = 0;
                    fileItem.canAccess = p.getAllowUserInteraction();
                    try {
                        UniAddress uniAddress = (UniAddress) getAddressMethod.invoke(p);
                        list.add(uniAddress.getHostAddress());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SmbException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList<FileItem> listServer() {
        ArrayList<FileItem> list = new ArrayList<>();
        SmbFile f = null;
        try {
            f = new SmbFile("smb://");
            SmbFile[] smbFiles = f.listFiles();

            if (smbFiles != null) {
                for (SmbFile p : smbFiles) {
                    FileItem fileItem = new FileItem();
                    fileItem.name = p.getName();
                    fileItem.path = p.getCanonicalPath();
                    fileItem.type = 0;
                    fileItem.canAccess = p.getAllowUserInteraction();
                    list.add(fileItem);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SmbException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<FileItem> listFile(String path, int filter, NtlmPasswordAuthentication ntlmPasswordAuthentication) throws SmbException, MalformedURLException {
        SmbFile file = new SmbFile(path, ntlmPasswordAuthentication);
        ArrayList<FileItem> files = new ArrayList<>();
        ArrayList<FileItem> folders = new ArrayList<>();

        SmbFile[] list = file.listFiles();
        if (list == null) {
            return null;
        }
        for (SmbFile f : list) {
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
                if (fileItem.extension != null && VideoHelper.isVideoFile(fileItem.extension.toLowerCase())) {
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

    public static FileItem getFileItem(String path, NtlmPasswordAuthentication ntlmPasswordAuthentication) {
        try {
            SmbFile f = new SmbFile(path, ntlmPasswordAuthentication);
            FileItem fileItem = new FileItem();
            fileItem.name = UriUtil.uriNameFormat(f.getName());
            fileItem.path = f.getPath();
            fileItem.lastModify = DateUtils.toDate(f.lastModified());
            if (f.isFile()) {
                fileItem.type = 2;
                fileItem.extension = UriUtil.getUriExtension(f.getName());
                if (fileItem.extension != null && VideoHelper.isVideoFile(fileItem.extension.toLowerCase())) {
                    return fileItem;
                }
            } else {
                fileItem.type = 1;
                return fileItem;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String searchSubtitle(String filepath) {
        try {
            String _name = UriUtil.getUriSimpleName(filepath);
            String path = UriUtil.getUriParent(filepath);
            SmbFile smbFile = new SmbFile(path);
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

    public static SmbFile createSmbFile(String path, NtlmPasswordAuthentication ntlmPasswordAuthentication) throws MalformedURLException {
        return new SmbFile(path.replace("+", "%2B"), ntlmPasswordAuthentication);
    }

    public static List<String> getIPs() {
        List<String> list = new ArrayList<String>();
        boolean flag = false;
        int count=0;
        Runtime r = Runtime.getRuntime();
        Process p;
        try {
            p = r.exec("arp -a");
            BufferedReader br = new BufferedReader(new InputStreamReader(p
                    .getInputStream()));
            String inline;
            while ((inline = br.readLine()) != null) {
                if(inline.indexOf("at") > -1){
                    flag = !flag;
                    if(!flag){
                        //碰到下一个"接口"退出循环
                        break;
                    }
                }
                if(flag){
                    count++;
                    if(count > 2){
                        //有效IP
                        String[] str=inline.split(" {4}");
                        list.add(str[0]);
                    }
                }
                System.out.println(inline);
            }
            br.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(list);
        return list;
    }
}
