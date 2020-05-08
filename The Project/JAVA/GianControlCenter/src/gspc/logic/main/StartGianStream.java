package gspc.logic.main;

import gspc.Main;
import gspc.gui.Screen;
import gspc.logic.tcp.TCPIPServer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class StartGianStream {
    private final Screen displayimage;
    private TCPIPServer sfa;
    private int portNumber = 8321;
    private String TAG = "StartGianStream";
    private Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

    public static final int DELAY = 100;
    private final BufferedImage[] bImage2 = {null};
    private final ByteArrayInputStream[] bis = {null};
    private final byte[][] imageBytes ={null};

    private boolean stopThreads = false;

    private Process p;
    private Runtime r;

    public StartGianStream(Screen displayimage){
        this.displayimage = displayimage;
        r = Runtime.getRuntime();

        Main.threads.submit(new Runnable() {
            @Override
            public void run() {
                JOptionPane info = new JOptionPane();
                info.setMessage("IP Address: "+getIP()+" Port: "+portNumber);
                info.createDialog("INFO").show();
            }
        });

//        System.out.println("IP Address: "+getIP());

//        Screen displayimage = new Screen("IP: "+getIP()+" Port: "+portNumber,dim.width-360,dim.height-640);

        TCPIPServer sfa = new TCPIPServer();
        sfa.makeServer(portNumber);
        sfa.listenForObjects();




        Main.threads.submit(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(DELAY);
                        synchronized (bis) {
                            bImage2[0] = ImageIO.read(bis[0]);
                            displayimage.updateImage(bImage2[0]);
//                            System.out.println("gspc.Main frame updated");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        Main.threads.submit(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(DELAY);
                        imageBytes[0] = (byte[]) sfa.getObject();
                        synchronized (bis){
                            bis[0] = new ByteArrayInputStream(imageBytes[0]);
                        }
                    } catch (Exception e) {
                        System.err.println(TAG+":> "+e);
                    }
                }
            }
        });

    }

    public String getIP(){
        String ip = "0.0.0.0";
        try {
            p=r.exec("hostname -I");
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            ip=in.readLine();
            p.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ip;
    }

    public void stop(){
        sfa.stopListeningForObjects();

    }

}
