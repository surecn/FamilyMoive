package com.surecn.familymovie.common;

import com.surecn.familymovie.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-12
 * Time: 16:51
 */
public class VideoHelper {

    private static HashMap<String, Integer> extensionIcons = new HashMap<>(); {
    }


    public static ArrayList<String> extensions = new ArrayList<>(); {
        //AVI、WMV、RM、RMVB、MPEG1、MPEG2、MPEG4(MP4)、3GP、ASF、SWF、VOB、DAT、MOV、M4V、FLV、F4V、MKV、MTS、TS
    }

    static {
        extensions.add("avi");
        extensions.add("navi");
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
        extensions.add("aac");
        extensions.add("gif");
        extensions.add("mid");
        extensions.add("wmw");
        extensions.add("mpg");
        extensions.add("asx");
        extensions.add("dv");

        extensions.add("mp3");
        extensions.add("wma");
        extensions.add("flac");
        extensions.add("aac");
        extensions.add("aac");
        extensions.add("mmf");
        extensions.add("amr");
        extensions.add("m4a");
        extensions.add("m4r");
        extensions.add("ogg");
        extensions.add("mp2");
        extensions.add("wav");
        extensions.add("wv");

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

    public static boolean isVideoFile(String extension) {
        return extensions.contains(extension);
    }

    public static Integer getVideoFileIcon(String extension) {
        Integer icon = extensionIcons.get(extension);
        if (icon == null) {
            icon = R.mipmap.file_icon_default;
        }
        return icon;
    }

}
