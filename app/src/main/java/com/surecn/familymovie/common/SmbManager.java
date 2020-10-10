package com.surecn.familymovie.common;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.surecn.familymovie.common.samba.Samba2;
import com.surecn.familymovie.domain.FileItem;
import com.surecn.familymovie.utils.DateUtils;
import com.surecn.familymovie.utils.UriUtil;
import com.surecn.moat.tools.log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jcifs.Address;
import jcifs.CIFSContext;
import jcifs.NetbiosAddress;
import jcifs.SmbResource;
import jcifs.context.SingletonContext;
import jcifs.smb.NtlmPasswordAuthenticator;
import jcifs.smb.SmbAuthException;
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
//        try {
//            getAddressMethod = SmbFile.class.getDeclaredMethod("getAddress");
//            getAddressMethod.setAccessible(true);
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        }
    }

    public static FileItem getServer(String ip) {
        log.e("========getServer=======" + ip);
        FileItem fileItem = null;
        try {
            SmbFile smbFile = new SmbFile("smb://" + ip + "/", SingletonContext.getInstance().withAnonymousCredentials());
            smbFile.setConnectTimeout(200);
            smbFile.setReadTimeout(100);
            if (!smbFile.exists()) {
                return null;
            }
            fileItem = new FileItem();
            fileItem.path = smbFile.getPath();
            fileItem.type = 0;
            fileItem.server = ip;
            smbFile.connect();
            fileItem.needPass = 0;
        } catch (SmbAuthException e) {
            fileItem.needPass = 1;
        } catch (Exception e) {
            fileItem = null;
        }
        if (fileItem != null) {
            Address nbtAddress = null;
            try {
                nbtAddress = SingletonContext.getInstance().getNameServiceClient().getByName(ip);
                nbtAddress.firstCalledName();
                String name = nbtAddress.nextCalledName(SingletonContext.getInstance());
                fileItem.name = name;
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        return fileItem;
    }

    public static List<String> listLanIp() {
        List<String> list = new ArrayList<String>();
        boolean flag = false;
        int count=0;
        Runtime r = Runtime.getRuntime();
        Process p;
        try {
            p = r.exec("cat proc/net/arp");
            BufferedReader br = new BufferedReader(new InputStreamReader(p
                    .getInputStream()));
            String inline;
            String ip = "((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}";
            Pattern pattern = Pattern.compile(ip);
            while ((inline = br.readLine()) != null) {
                Matcher matcher = pattern.matcher(inline);
                if (matcher.find()) {
                    list.add(matcher.group());
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<String> listWorkspaces() {
        ArrayList<String> list = new ArrayList<>();
        SmbFile f = null;
        try {
            f = new SmbFile("smb://workgroup/", SingletonContext.getInstance().withAnonymousCredentials());
            SmbFile[] smbFiles = f.listFiles();
            if (smbFiles != null) {
                for (SmbFile p : smbFiles) {
                    FileItem fileItem = new FileItem();
                    fileItem.name = p.getName();
                    fileItem.path = p.getCanonicalPath();
                    fileItem.type = 0;
//                    try {
//                        p.connect();
//                        fileItem.canAccess = true;
//                    } catch (Exception e) {
//                        fileItem.canAccess = false;
//                    }
                    list.add(p.getServer());
                    log.e("===" + p.getServer() + "===" + p.getServerWithDfs());
//                    try {
//                        UniAddress uniAddress = (UniAddress) getAddressMethod.invoke(p);
//                        list.add(uniAddress.getHostAddress());
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    } catch (InvocationTargetException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SmbException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<FileItem> listFile(String path, int filter, NtlmPasswordAuthenticator ntlmPasswordAuthenticator) throws SmbException, MalformedURLException {
        CIFSContext cifsContext = SingletonContext.getInstance().withDefaultCredentials();
        if (ntlmPasswordAuthenticator != null) {
            cifsContext = SingletonContext.getInstance().withCredentials(ntlmPasswordAuthenticator);
        }
        SmbFile file = new SmbFile(path, cifsContext);
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

    public static FileItem getFileItem(String path, NtlmPasswordAuthenticator ntlmPasswordAuthenticator) {
        try {
            CIFSContext cifsContext = SingletonContext.getInstance().withAnonymousCredentials();
            if (ntlmPasswordAuthenticator != null) {
                cifsContext = SingletonContext.getInstance().withCredentials(ntlmPasswordAuthenticator);
            }
            SmbFile f = new SmbFile(path, cifsContext);
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
            SmbFile smbFile = new SmbFile(path, SingletonContext.getInstance());
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

    public static SmbFile createSmbFile(String path, NtlmPasswordAuthenticator ntlmPasswordAuthenticator) throws MalformedURLException {
        CIFSContext cifsContext = SingletonContext.getInstance().withAnonymousCredentials();
        if (ntlmPasswordAuthenticator != null) {
            cifsContext = SingletonContext.getInstance().withCredentials(ntlmPasswordAuthenticator);
        }
        return new SmbFile(path.replace("+", "%2B"), cifsContext);
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
