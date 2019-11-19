package com.surecn.familymovie.common;

import java.util.ArrayList;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-12
 * Time: 16:51
 */
public class VideoHelper {

    public static ArrayList<String> extensions = new ArrayList<>(); {
        //AVI、WMV、RM、RMVB、MPEG1、MPEG2、MPEG4(MP4)、3GP、ASF、SWF、VOB、DAT、MOV、M4V、FLV、F4V、MKV、MTS、TS
    }

    static {
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
    }

    public static boolean isVideoFile(String extension) {
        return extensions.contains(extension);
    }

}
