package com.surecn.familymovie.common.samba;


import com.surecn.familymovie.common.VideoHelper;
import com.surecn.familymovie.domain.FileItem;
import com.surecn.familymovie.utils.DateUtils;
import com.surecn.familymovie.utils.UriUtil;

import java.io.IOException;

import jcifs.smb.SmbFile;

public class Samba1 {

    public static void getFileItem() {
//        try {
//            SmbFile f = new SmbFile(path, ntlmPasswordAuthentication);
//            FileItem fileItem = new FileItem();
//            fileItem.name = UriUtil.uriNameFormat(f.getName());
//            fileItem.path = f.getPath();
//            fileItem.lastModify = DateUtils.toDate(f.lastModified());
//            if (f.isFile()) {
//                fileItem.type = 2;
//                fileItem.extension = UriUtil.getUriExtension(f.getName());
//                if (fileItem.extension != null && VideoHelper.isVideoFile(fileItem.extension.toLowerCase())) {
//                    return fileItem;
//                }
//            } else {
//                fileItem.type = 1;
//                return fileItem;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
