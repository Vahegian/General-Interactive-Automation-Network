package com.hima.skizb.gianstream.tcpip;

import android.graphics.Bitmap;
import android.media.Image;
import android.media.ImageWriter;
import android.util.Log;

import com.hima.skizb.gianstream.Main;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class pcSocketClient {
    private String TAG = "pcSocketClient";
    private int threadSleepTime = 100;
    private PrintWriter out;
    private Main context;
    private boolean streamBitmap = false;
    private Socket socket;
    private ObjectOutputStream dataOut;
    private final byte[][] byteArray = {null};
    private ByteArrayOutputStream stream;
    private Bitmap imageMap;

    public pcSocketClient(Main context){
        this.context=context;
    }

    public void makeClient(final String hostName, final int portNumber) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(hostName, portNumber);
                    dataOut = new ObjectOutputStream(socket.getOutputStream());

                }catch (Exception e){
                    Log.d(TAG, e+"");
//                    Toast.makeText(context,"Wrong IP Address",Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }


    public void streamBitmap(){
        streamBitmap=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (streamBitmap) {
                    try {
                        Thread.sleep(threadSleepTime);
//                        imageMap = context.getimageBitmap();
                        stream = new ByteArrayOutputStream();
                        imageMap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                        synchronized (byteArray) {
                            byteArray[0] = stream.toByteArray();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (streamBitmap) {
                    try {
                        Thread.sleep(threadSleepTime);
                        synchronized (byteArray) {
                            dataOut.writeObject(byteArray[0]);
//                            Log.e(TAG, "object is Sent");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void stopStreamingBitmap(){
        streamBitmap=false;
    }
}
