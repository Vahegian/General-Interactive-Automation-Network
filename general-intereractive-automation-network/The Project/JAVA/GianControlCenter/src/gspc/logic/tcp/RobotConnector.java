package gspc.logic.tcp;

import gspc.Main;
import lejos.geom.Line;
import lejos.geom.Rectangle;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;


import javax.swing.*;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Scanner;

public class RobotConnector implements Runnable {
    private DataInputStream dis;
    private DataOutputStream dos;
    private final int[][]coordinates = new int[1][4];
    private final int[][] roboCoordAndOptions = new int[1][4];
    public boolean stopThread =false;

    public boolean isConnected = false;

    //    int[] coord = {11,11};
    public RobotConnector(JTextField userInput){
        try {
            userInput.setText("Searching...");
            NXTComm nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
            NXTInfo[] nxtInfo = nxtComm.search("NXT");
//            System.out.println(nxtInfo[0].deviceAddress);
            userInput.setText("number of units = "+nxtInfo.length);
            nxtComm.open(nxtInfo[0]);

            userInput.setText(nxtInfo[0].name+" : "+nxtInfo[0].deviceAddress);
            dos = new DataOutputStream(nxtComm.getOutputStream());
            dis = new DataInputStream(nxtComm.getInputStream());
            isConnected = true;

            userInput.setText("Connected!");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendData(int[] coord) {
        System.err.println("Sending to robot");
//        if(coord.length == 4){
        try {
            for (int i : coord) {
                dos.writeInt(i);
            }
            dos.flush();
        }catch (Exception e){}
    }

    public void updateCoord(int[] coord){
        synchronized (coordinates){
            coordinates[0] = new int[]{coord[0], coord[1], coord[2], coord[3]};
        }
        System.err.println("Coords Updated");
    }

    private void recieveData(){
        try {
            synchronized (roboCoordAndOptions){
                for (int i = 0; i < 4; i++) { roboCoordAndOptions[0][i] = dis.readInt(); }
                System.err.println("received: "+roboCoordAndOptions[0][0]+" : "+roboCoordAndOptions[0][1]+" : "+roboCoordAndOptions[0][2]+" : "+roboCoordAndOptions[0][3]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int[] getRobotPosAndOptions(){
        synchronized (roboCoordAndOptions){
            return roboCoordAndOptions[0];
        }
    }


    @Override
    public void run() {
         final int[][] tempCoord = {{0,0}};

        while (!stopThread) {
            try {
                Main.threads.submit(new Runnable() {
                    @Override
                    public void run() {
                        recieveData();
                    }
                });

                Thread.sleep(100);

                Main.threads.submit(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (coordinates) {
//                            if (coordinates[0][0] != tempCoord[0][0] || coordinates[0][1] != tempCoord[0][1]) {
                                sendData(coordinates[0]);
//                                tempCoord[0] = new int[]{coordinates[0][0], coordinates[0][1]};
//                            }
                        }
                    }
                });

                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
