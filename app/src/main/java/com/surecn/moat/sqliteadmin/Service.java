package com.surecn.moat.sqliteadmin;

import android.os.Handler;
import android.os.Looper;

import com.surecn.moat.tools.log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by surecn on 17/1/3.
 */

//处理与client对话的线程
public class Service implements Runnable {

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private volatile boolean kk=true;
    private Socket socket;
    private BufferedReader in = null;
    private String msg = "";

    public Service(Socket socket) {
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            msg="OK";
            this.sendmsg(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }

        

    }

    public void run() {

        while(kk) {
            try {
                String msg = in.readLine();
                log.e("========" + msg);
//                if(msg!= null) {
//                    //当客户端发送的信息为：exit时，关闭连接
//                    if(msg.equals("exit")) {
//                        //mList.remove(socket);
//                        //in.close();
//                        //socket.close();
//                        break;
//                        //接收客户端发过来的信息msg，然后发送给客户端。
//                    } else {
//                        Message msgLocal = new Message();
//                        msgLocal.what = 0x1234;
//                        msgLocal.obj =msg+" （客户端发送）" ;
//                        System.out.println(msgLocal.obj.toString());
//                        System.out.println(msg);
//                        mHandler.sendMessage(msgLocal);
//                        msg = socket.getInetAddress() + ":" + msg+"（服务器发送）";
//                        this.sendmsg(msg);
//                    }
//
//                }
            } catch (IOException e) {
                System.out.println("close");
                kk=false;
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }


    }
    //向客户端发送信息
    public void sendmsg(String msg) {
        System.out.println(msg);
        PrintWriter pout = null;
        try {
            pout = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())),true);
            pout.println(msg);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}