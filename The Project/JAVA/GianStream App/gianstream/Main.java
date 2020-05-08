package com.hima.skizb.gianstream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.Image;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hima.skizb.gianstream.cameraPKG.LunchCam;
import com.hima.skizb.gianstream.tcpip.pcSocketClient;


public class Main extends Activity {
    private com.hima.skizb.gianstream.Main I = this;
    public Image image;
    public ImageButton mStillImageButton;
    public TextureView textureViewCam;

    private LunchCam cam;
    private pcSocketClient client;
    public Bitmap map;

    private boolean camloaded = false;
    private TextView ipTextEdit;
    private TextView portTextEdit;
    private Button connectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textureViewCam = (TextureView) findViewById(R.id.textureView);
        ipTextEdit = (TextView)findViewById(R.id.editTextIP);
        portTextEdit = (TextView)findViewById(R.id.editTextPort);
        connectButton = (Button)findViewById(R.id.connectBut);
        ipTextEdit.setText(getResources().getString(R.string.defaultip));
        portTextEdit.setText(getResources().getString(R.string.defaultPort));

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectToServer();
            }
        });

        waitTillCameraOpenStartStreaming();
    }

    private void waitTillCameraOpenStartStreaming(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!camloaded){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                startStreamingCamImages();
            }
        }).start();
    }

    private void connectToServer(){
        final String ip = ipTextEdit.getText().toString();
        try {
            if (!ip.equals("")) {
                String[] ips = ip.split(".");
                for (String i : ips) {
                    Integer.parseInt(i);
                }
            }else ipTextEdit.setHint(getResources().getString(R.string.ipnotentered));

            try {
                int port = Integer.parseInt(portTextEdit.getText().toString());
                establishConnection(ip, port);
            }catch (Exception e){portTextEdit.setHint(getResources().getString(R.string.portnotentered));}

            hideTCPlayout();
            startCamera();
        }catch (Exception e){ipTextEdit.setHint(getResources().getString(R.string.ipnotentered));}
    }

        public void hideTCPlayout(){
        LinearLayout tcpLayout = (LinearLayout)findViewById(R.id.lauoutForTCP);
        tcpLayout.setVisibility(View.GONE);
        textureViewCam.setVisibility(View.VISIBLE);
    }

    private void establishConnection(String ip, int port){
        client = new pcSocketClient(I);
        client.makeClient(ip,port);
    }

    private void startCamera(){
        cam= new LunchCam(I);
        textureViewCam.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(final SurfaceTexture surfaceTexture, int width, int height) {
                cam.setupCamera(width, height);
                cam.checkPermissionsAndOpenCamera();
                camloaded=true;
            }
            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) { }
            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) { return false;}
            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) { }
        });
    }

    private void startStreamingCamImages(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (textureViewCam) {
                    map = textureViewCam.getBitmap();
                }
                client.streamBitmap();
                while (true){
                    try {
                        Thread.sleep(100);
                        synchronized (textureViewCam) {
                            map = textureViewCam.getBitmap();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public Bitmap getimageBitmap(){ return map; }

    @Override
    protected void onResume() {
        super.onResume();
        if(textureViewCam.isAvailable()) {
            cam.setupCamera(textureViewCam.getWidth(), textureViewCam.getHeight());
            cam.checkPermissionsAndOpenCamera();
//            startStreamingCamImages();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        client.stopStreamingBitmap();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocas) {
        super.onWindowFocusChanged(hasFocas);
        View decorView = getWindow().getDecorView();
        if(hasFocas) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }

}
