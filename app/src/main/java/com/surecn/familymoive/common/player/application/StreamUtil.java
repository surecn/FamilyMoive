package com.surecn.familymoive.common.player.application;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-10-28
 * Time: 13:19
 */


import java.io.IOException;

import java.io.InputStream;

import java.io.OutputStream;

public class StreamUtil {

    byte[] buffer = new byte[524288];

    public   void convertStream(InputStream in, OutputStream out) {

// InputStream in = new FileInputStream(srcFile);

// out = new FileOutputStream(destFile);

        int byteread;



        byteread = read(in);



        while (byteread != -1) {

            if (byteread != 0) {

                try {

                    out.write(buffer, 0, byteread);

                    System.out.println("---out.write.."+byteread);

//out.flush();

                } catch (Exception e) {

                    e.printStackTrace();

                    throw new RuntimeException(e);

                }

            }

            byteread = read(in);



        }





        try {

            in.close();

        } catch (IOException e) {

// TODO Auto-generated catch block

            e.printStackTrace();

        }

        try {

            out.close();

        } catch (IOException e) {

// TODO Auto-generated catch block

            e.printStackTrace();

        }

    }



    private   int read(InputStream in ) {

        int byteread;

        try {

            byteread = in.read(buffer,0,524288);

        } catch (Exception e) {

            e.printStackTrace();

            byteread = 0;

        }

        return byteread;

    }



}