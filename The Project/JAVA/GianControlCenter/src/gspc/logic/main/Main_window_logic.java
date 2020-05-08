package gspc.logic.main;

import gspc.Main;
import gspc.gui.*;
import gspc.gui.custom_objects.Colors;
import gspc.logic.java_to_py.MyStack;
import gspc.logic.java_to_py.ProcessCreator;
import gspc.logic.tcp.RobotConnector;
import py4j.GatewayServer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main_window_logic {

    private final GIANControlPanel gcp;
    private final GIANGridPanel gp;
    private final GIANInfoPanel gip;
    private final GianRobotPanel grp;
    private final Screen gspc;
    private MyStack jio;
    private ProcessCreator pyc;
    private GatewayServer gatewayServer;
    private boolean stopThreads = false;
    private final Map<String, Object>[] pythonBridgData = new Map[1];
    private boolean mapEdited = false;
    private RobotConnector rcbt;

    private final int[][] yoloHandPos = {{0,0}};
    private final String[] yoloLable = {"Guess"};
    private boolean isRobotConnected = false;
    private final int[][] robotInfo = {{0,0,0,0}}; // x,y,orientation,options
    private final int[] joyOK = {0};
    private boolean stopRobotThreads = false;

    public Main_window_logic(GIANControlPanel gcp, GIANGridPanel gp, GIANInfoPanel gip, GianRobotPanel grp, Screen gspc){
        this.gp = gp; this.gcp = gcp; this.gip = gip; this.grp = grp; this.gspc = gspc;
        startGianStreamPC();
        startGianWhenPressed();
        connectToRobotWhenPressed();

        grp.userInput.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                grp.userInput.setText("");
            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    /******************************************************************************************************************
     *                                              Robot methods                                                     *
     ******************************************************************************************************************/

    private void connectToRobotWhenPressed() {
        grp.connectToRobotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rcbt = new RobotConnector(grp.userInput);
                Main.threads.submit(rcbt);
                isRobotConnected = rcbt.isConnected;
                grp.userInput.setBackground(Colors.greenLight);
                sendDataWhenPressed();
                updateRobotInformation();
            }
        });
    }

    private void sendDataWhenPressed() {
        grp.sendDataToRobotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int[] robotinfo = {0,0,0,0};
                    synchronized (robotInfo){
                        robotinfo = robotInfo[0];
                    }

                    String[] x_y_seperated = grp.userInput.getText().split(",");
                    int x = Integer.parseInt(x_y_seperated[0]);
                    int y = Integer.parseInt(x_y_seperated[1]);
                    System.err.println(robotinfo.length);
                    sendCoordsToRobot(new int[]{x,y, robotinfo[2], robotinfo[3]});
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
    }

    private void sendCoordsToRobot(int[] coord) {
        rcbt.updateCoord(coord);
    }

    private void updateRobotInformation(){
        Main.threads.submit(new Runnable() {
            @Override
            public void run() {
                while (!stopRobotThreads){
                    if (isRobotConnected) {
                        synchronized (robotInfo) {
                            robotInfo[0] = rcbt.getRobotPosAndOptions();
//                            System.out.println("Robot info updated: "+robotInfo[0][0]);
                        }
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    /******************************************************************************************************************
     *                                       Python communication                                                     *
     ******************************************************************************************************************/

    private void startCommunication() {
        Main.threads.submit(new Runnable() {
            @Override
            public void run() {
//                int[] temp_robotInfo = {0,0,0,0};
                ArrayList<Integer> l2 = new ArrayList<>();
                int imOption = 0;
//                int tempOption = 1;
                System.out.println("READY");
                while (!stopThreads){
                    try {
//                        synchronized (pythonBridgData) {
                            pythonBridgData[0] = jio.popData();

                        String[] l = ((String) pythonBridgData[0].get("handPos")).split(",");

                        if(l.length>0) {
                            synchronized (yoloHandPos) {
                                yoloHandPos[0][0] = Integer.parseInt(l[0]);
                                yoloHandPos[0][1] = Integer.parseInt(l[1]);
                            }
                        }

                            synchronized (yoloLable){
                                yoloLable[0] = (String) pythonBridgData[0].get("guess");
                            }

                            synchronized (joyOK){
                                joyOK[0] = (int) pythonBridgData[0].get("joyOK");
                            }

                            if (gcp.showImgRB.isSelected())imOption = 0;
                            else imOption = 1;

                            if(isRobotConnected) {
                                String robotPos = "";
                                synchronized (robotInfo) {
                                    for (Integer i : robotInfo[0]) {
                                        robotPos += (i + ",");
                                    }

                                }
                                pythonBridgData[0].replace("robotInfo", robotPos);
                            }

                            pythonBridgData[0].replace("img", imOption);

//                            System.out.println(pythonBridgData[0]);

                            jio.pushData(pythonBridgData[0]);

//                        }
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void openJavaToPythonBridge() {
        jio = new MyStack();
        gatewayServer = new GatewayServer(jio);
        gatewayServer.start();
    }

    private void updateOptions(int cam, int showCam) {
        openJavaToPythonBridge();
        Map<String, Object> m = jio.popData();
        m.replace("cam", cam);
        m.replace("img", showCam);
        try {
            jio.pushData(m);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        startGIAN();
    }

    private void startGIAN(){
        Main.threads.submit(new Runnable() {
            @Override
            public void run() {
                pyc = new ProcessCreator();
                pyc.lunchScript("/usr/GIAN/GIAN.sh", "withStack");

                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                startCommunication();
//                updateMapContent();
            }
        });


    }

    public void startGianWhenPressed() {
        gcp.startGianBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                if (gianStreamRB.isSelected()){

//                }
                int cam = 0; int showCam = 1;
                if(gcp.startGianBut.getText().equals("Start GIAN")) {
                    stopThreads = false;
                    if (gcp.extCamRB.isSelected()) {
                        cam = 1;
                    }
                    if (gcp.showImgRB.isSelected()){
                        showCam = 0;
                    }
                    gp.start();
                    updateOptions(cam, showCam);
                    gcp.extCamRB.setEnabled(false);
                    gcp.startGianBut.setText("Stop GIAN");
                    updateSystemInfo();
                    controlRobot();

                }else {
//                    pyc.stopProcess();
                    pyc.lunchScript("/usr/GIAN/killPY.sh");
                    stopThreads = true;
                    gatewayServer.shutdown();
                    gatewayServer = null;
                    gcp.extCamRB.setEnabled(true);
                    gp.terminate();
                    gcp.startGianBut.setText("Start GIAN");
//                    System.exit(0);
                }
            }
        });
    }

    /******************************************************************************************************************
     *                                                      GUI                                                       *
     ******************************************************************************************************************/

    private void updateSystemInfo(){
        Main.threads.submit(new Runnable() {
            @Override
            public void run() {
                System.err.println("updating sys info");
//                int[] robotInfo = {0,0};
                while (!stopThreads){
                    try {
//                    System.err.println("updating sys info");
//                    System.err.println("updated");
                        synchronized (yoloHandPos) {
                            gp.updateHandPos(yoloHandPos[0][0], yoloHandPos[0][1]);
                            gip.handPos.setText("X: " + yoloHandPos[0][0] + " Y: " + yoloHandPos[0][1]);
                        }
                        Thread.sleep(10);
                        synchronized (yoloLable) {
                            gip.lable.setText(yoloLable[0]);
                        }

                        Thread.sleep(10);
                        synchronized (joyOK){
                            gip.ok.setText(joyOK[0]+"");
                        }

                        Thread.sleep(10);
                        Main.threads.submit(new Runnable() {
                            @Override
                            public void run() {
                                if (isRobotConnected) {
                                    synchronized (robotInfo) {
//                                        System.err.println("sync update");
                                        gip.robotPos.setText(robotInfo[0][0] + " : " + robotInfo[0][1]);
//                                        System.err.println("updated" + robotInfo[0]);
                                    }
                                }
                            }
                        });
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /******************************************************************************************************************
     *                                              The Control                                                       *
     ****************************************************************************************************************
     * @param handPos
     * @param robotinfo*/

    private boolean isRobotLocated(int[] handPos, int[] robotinfo){
        if (robotinfo[2] == 3){
            return (handPos[0] >= robotinfo[0] && handPos[0] <= robotinfo[0] + 5) &&
                    (handPos[1] <= robotinfo[1] + 1) && (handPos[1] >= robotinfo[1] - 1);
        }else if (robotinfo[2] == 4){
            return (handPos[0] >= robotinfo[0] - 5 && handPos[0] <= robotinfo[0]) &&
                    (handPos[1] <= robotinfo[1] + 1) && (handPos[1] >= robotinfo[1] - 1);
        }else{
            return false;
        }
    }

    private void controlRobot(){
        Main.threads.submit(new Runnable() {
            @Override
            public void run() {
                int[] handPos = {0,0};
                int joyok = 0;
                String gesture = "";
                int step = 0;
                int tempStep =0;
                int[] robotinfo = {0,0,0,0};
                while (!stopThreads){
                    try {

                        synchronized (yoloHandPos){
                            handPos = yoloHandPos[0];
                        }

                        synchronized (joyOK){
                            joyok = joyOK[0];
                        }

                        synchronized (yoloLable){
                            gesture = yoloLable[0];
                        }

                        synchronized (robotInfo){
                            robotinfo = robotInfo[0];
                        }

                        if(isRobotConnected) {

                            boolean robotFound = isRobotLocated(handPos, robotinfo);
                            if (step == 0 && robotFound) {
                                System.out.println("Select robot");
//                                for (int i = 0 ; i<10; i++) {
//                                    Thread.sleep(10);
                                    if (joyok == 1 || gesture.equals("hand_ok")) {
                                        robotinfo[3] = 4;
                                        sendCoordsToRobot(robotinfo);
                                        step = 1;
                                        System.out.println(step);
                                    }
//                                }


                            } else if (step == 1 && !robotFound) {
                                System.out.println("Select POS");
//                                for (int i = 0 ; i<10; i++) {
//                                    Thread.sleep(10);
                                    if (joyok == 1 || gesture.equals("hand_ok")) {
                                        robotinfo[3] = 0;
                                        sendCoordsToRobot(new int[]{handPos[0], handPos[1], robotinfo[2], robotinfo[3]});
                                        step = 0;
                                        System.out.println(step);
                                    }
//                                }
                            }

                        }

                        Thread.sleep(10);

                    }catch (Exception e){

                    }
                }
            }
        });
    }

    /******************************************************************************************************************
     *                                             GianStreamPC                                                       *
     ******************************************************************************************************************/

    private void startGianStreamPC(){
        Main.threads.submit(new Runnable() {
            @Override
            public void run() {
                final StartGianStream[] sgs = {null};
                boolean stream = false;
//                System.out.println("gspc");
                while (!stopThreads){
//                    System.out.println("gspc");

                    if(gcp.gianStreamRB.isSelected() && !stream){
                        Main.threads.submit(new Runnable() {
                            @Override
                            public void run() {
                                sgs[0] = new StartGianStream(gspc);
                            }
                        });
                        stream = true;
                        System.out.println("selected");
                    }else if (!gcp.gianStreamRB.isSelected()) {
                        if (sgs[0] != null){
                            sgs[0].stop();
                            stream = false;
                            sgs[0] = null;
                            System.out.println("un_selected");
                        }
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
}
