import lejos.nxt.*;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.robotics.Color;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Stopwatch;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
    public static final int[][] curCoord = {{7,2}};
    public static int[] newCoord={0,0};
    public static final int[] orientation = {3};
    public static final int[] options = {0};
    private static BTReceive bt;

    public static void main(String[] args){
        bt = new BTReceive();
        new Thread(bt).start();
        LCD.clear();
        System.out.println("Run< >Calibrate");
        while (true) {
            if (Button.RIGHT.isDown()) {
                LCD.clear(); System.out.println("Calibrate");
                new Calibrator();
//                break;
            }else if (Button.LEFT.isDown()){
                LCD.clear(); System.out.println("Running Default");
                startRobotDefault();

            }

            if(Button.ENTER.isDown()) System.exit(0);
        }
//        new Calibrator();
////        Button.waitForAnyPress();
    }

    private static void startRobotDefault() {
//        System.out.println("part 1");
        // def == 2.693943
        //4.2
        PathNavigator pn = new PathNavigator(2.693943f, 5.9f);
        PathPlanner pp = new PathPlanner();

        do {

            newCoord = bt.getCoord();
            synchronized (options){options[0] = newCoord[3];}

            if ((newCoord[0]!=curCoord[0][0] && newCoord[1]!=curCoord[0][1]) && (newCoord[0]!=0 && newCoord[1]!=0)) {
                System.out.println(curCoord[0][0] + ":" + curCoord[0][1] + "|" + newCoord[0] + ":" + newCoord[1]);
                pn.followPath(pp.getPlannedPath(curCoord[0], newCoord, orientation[0]));
                synchronized (curCoord){
                    curCoord[0] = new int[]{newCoord[0], newCoord[1]};
                }
            }

        } while (!Button.ENTER.isDown());

    }
}

/***********************************************************************************************************************
 *                                                  Calibrator                                                         *
 **********************************************************************************************************************/

class Calibrator{
    Thread LEDThread;
    public boolean StopThreads = false;
    private ColorSensor ledSens;

    public Calibrator(){
        ledSens = new ColorSensor(SensorPort.S1);
        ledSens.setFloodlight(false);

//        System.out.println(findWheelDiameter(15.0));
//        System.out.println(findTrackWidth((2.66470/2)/100));
//        Button.waitForAnyPress();

    }

    public double findWheelDiameter(double distance) {
        /*
            find wheel diameter
            D = Circumference/piHigh
            Circumference = Distance traveled / revolutions
            1 degree = 0.002778 revolutions
        */
        PathNavigator.moveForwardNoPilot(180); // 0.5 rpm  => 360 == 1 rpm
        int[] motorDgreeRevolutions = {0,0};
        while (!StopThreads) {
            try {
                if (ledSens.getRawLightValue() > 500) {
                    PathNavigator.stopMotors();
                    motorDgreeRevolutions = PathNavigator.getTacho();
                    System.out.println("Found "+motorDgreeRevolutions[0]+" : "+motorDgreeRevolutions[1]);
                    break;
                }
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        double revolution = motorDgreeRevolutions[1]*0.002778;
        double circumference = distance/revolution;
        return circumference/Math.PI;
    }

    public double findTrackWidth(double wheelRadius) {
        /**
         * find track width  look robotics lab
         */
        long beginTime = System.currentTimeMillis();
        long elapsedTime = 0;
        PathNavigator.rotateLeftWheelNoPilot(360);
        int[] motorDgreeRevolutions = {0,0};
        while (!StopThreads) {
            try {
                motorDgreeRevolutions = PathNavigator.getTacho();
                if (ledSens.getRawLightValue() > 500) {
                    elapsedTime = (System.currentTimeMillis()-beginTime)/1000;
                    PathNavigator.stopMotors();
                    System.out.println("Found "+motorDgreeRevolutions[0]+" : "+Math.abs(motorDgreeRevolutions[1])+" : "+elapsedTime);
                    break;
                }
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        double linearSpeed = wheelRadius*0.10472; // in m/s
        double distance = linearSpeed*elapsedTime; System.out.println("V="+linearSpeed+"\nS="+distance); // in meters
        System.out.println("exp="+((distance/Math.PI)*100)); // in cm
        double trackWidth = (2*Math.PI*distance)/(motorDgreeRevolutions[0]*0.002778);
        return trackWidth*200; // in cm
    }

    public void moveRobotWithIntervals(int sec){
        PathNavigator pn = new PathNavigator(2.693943f, 5.9f);
                try {
                    for (int i =1; i<=7; i++) {
                        pn.moveForward(i*2);
                        System.out.println("travled : "+(i*2)+" cm");
                        Thread.sleep(sec * 1000);
                    }
                }catch (Exception e){System.out.println(e.toString());}
    }
}


/***********************************************************************************************************************
 *                                                 Path Navigator                                                      *
 **********************************************************************************************************************/

class PathNavigator{
    private final DifferentialPilot pilot;
    private static NXTRegulatedMotor motorRight = Motor.C;
    private static NXTRegulatedMotor motorLeft = Motor.A;
    public boolean naviagate = true;
    private int[] tacho;

    public PathNavigator(float wheelDiameter, float trackWidth){
        pilot = new DifferentialPilot(wheelDiameter, trackWidth, motorRight, motorLeft);
        OdometryPoseProvider odometry = new OdometryPoseProvider(pilot);
        pilot.addMoveListener(odometry);
        odometry.moveStarted(null, pilot);
        pilot.setTravelSpeed(10);
        pilot.setRotateSpeed(50);
    }

    public void moveForward(double distance){
        pilot.travel(distance-findTravelOffSet(distance));
    }

    private double findTravelOffSet(double distance) {
//        (distance*0.2)-0.2)
//        (0.37661236750712285*distance)-0.9650401673818262
        /* a python script was written to find these numbers
            the numbers are calculated using linear function y = a * x - b
            where "a" and "b" are un-knows and are calculated
            "y" is the amount of error the robot makes when it travels "x" distance
         */
        if(distance<6) return (0.22041666666666662*distance)-0.20000000000000004;
        else return (0.22138888888888889*distance)-0.2000000000000001;
    }

    public void rotate(double degree){
        pilot.rotate(degree+findRotationOffSet(degree));
    }

    private double findRotationOffSet(double degree) {
        if (degree<0) return (0.5493*(degree+1))+0.5555;
        return (0.5493*(degree-1))+0.5555;
    }

    private static void resetTachos(){motorLeft.resetTachoCount();    motorRight.resetTachoCount();}

    public static void moveForwardNoPilot(int revolutions){
        resetTachos();
        motorLeft.setSpeed(revolutions);    motorRight.setSpeed(revolutions);
        motorLeft.forward();    motorRight.forward();
    }

    public static void rotateLeftWheelNoPilot(int revolutions) {
        resetTachos();
        motorLeft.setSpeed(revolutions);   // motorRight.setSpeed(revolutions);
        motorLeft.forward();    //motorRight.backward();
    }

    public static void stopMotors(){
        motorLeft.stop();  motorRight.stop();
    }

    public static int[] getTacho(){ // degrees turned
        return new int[]{motorLeft.getTachoCount(), motorRight.getTachoCount()};
    }



    public void followPath(List<double[]> path) {
        /*
         * int[0] := 1 || 0
         * 0= rotate
         * 1= travel
         */
        for (double[] i : path){
            if (i.length == 2){
                if (i[0] == 0) rotate(i[1]);
                else if (i[0]==1) moveForward(i[1]);
            }
        }
    }
}

/***********************************************************************************************************************
 *                                                 Path Planner                                                        *
 **********************************************************************************************************************/

class PathPlanner{
    private Map<Integer, Integer> orientationToDegree = new HashMap<Integer, Integer>();

    public PathPlanner(){
        populateOrientationMap();
    }

    private void populateOrientationMap() {
        orientationToDegree.put(1,0);
        orientationToDegree.put(2,180);
        orientationToDegree.put(3, -90);
        orientationToDegree.put(4,90);
    }

    public List<double[]> getPlannedPath(int[] curPos, int[] destination, int orientation){
        List<double[]> instructions = new LinkedList<double[]>();
        /*
         * info 'orientation'
         * 1=North
         * 2=South
         * 3=West
         * 4=East
         */

        if (destination[1] > curPos[1]){
            instructions = planFromSouthToNorth(curPos, destination, orientation);
        }else if (destination[1]<curPos[1]){
            // plan from the other side of the frame
            instructions = planFromNorthToSouth(curPos, destination, orientation);
        }

        return instructions;
    }

    private List<double[]> planFromNorthToSouth(int[] curPos, int[] destination, int orientation) {
        System.out.println("North->South");
        List<double[]> instructions = new LinkedList<double[]>();

        // set orientation towards SOUTH
        if(orientationToDegree.get(orientation) != 0) {
            instructions.add(new double[]{0, (orientationToDegree.get(orientation))});
            synchronized (Main.orientation) {
                Main.orientation[0] = orientation;
            }
        } else {
            instructions.add(new double[]{0, 180});
            synchronized (Main.orientation) {
                Main.orientation[0] = 2;
            }
        }

        // Cover The Y distance
        if (orientationToDegree.get(orientation) == 0){
            double y = ((destination[1] - curPos[1])+2) * -4.5;
            instructions.add(new double[]{1, y});
        }else {
            double y = (destination[1] - curPos[1]) * -4.5;
            instructions.add(new double[]{1, y});
        }

        System.out.println(destination[0]+" : "+curPos[0]);
        // set orientation towards destination
        if(destination[0] > curPos[0]) { // go  west
            instructions.add(new double[]{0, orientationToDegree.get(3)});
            synchronized (Main.orientation) {
                Main.orientation[0] = 4;
            }
        }else if (destination[0] < curPos[0]) { // go east
            instructions.add(new double[]{0, orientationToDegree.get(4)});
            synchronized (Main.orientation) {
                Main.orientation[0] = 3;
            }
        }

        // drive to the destination
        double x = ((destination[0] - curPos[0]));
        // drive to destination
        if(x<0){ // robot made a turn to west
            if(orientationToDegree.get(orientation)==0) instructions.add(new double[]{1, -4.5*(x+2)}); // robot skips 2 steps if it was position towards north initially
            else if (orientationToDegree.get(orientation)==90) instructions.add(new double[]{1, -4.5*(x+4)}); // 4 steps for west
            else if (orientationToDegree.get(orientation)==-90) instructions.add(new double[]{1, -4.5*x}); // no skip for east
        }else if (x>0){ // robot made turn to east
            if(orientationToDegree.get(orientation)==0) instructions.add(new double[]{1, 4.5*(x-2)});
            else if (orientationToDegree.get(orientation)==90) instructions.add(new double[]{1, 4.5*(x)});
            else if (orientationToDegree.get(orientation)==-90) instructions.add(new double[]{1, 4.5*(x-4)});
        }

        return instructions;
    }
/*
*
*
*
* */
    private List<double[]> planFromSouthToNorth(int[] curPos, int[] destination, int orientation) {
        System.out.println("South->North");
        List<double[]> instructions = new LinkedList<double[]>();

        // set orientation towards NORTH
        if(orientationToDegree.get(orientation) != 180) {
            instructions.add(new double[]{0, (-1 * orientationToDegree.get(orientation))});
            synchronized (Main.orientation) {
                Main.orientation[0] = 2;
            }
        }

        // Cover The Y distance
        if (orientationToDegree.get(orientation) == 180){
            double y = ((destination[1] - curPos[1])-2) * 4.5;
            instructions.add(new double[]{1, y});
        }else {
            double y = (destination[1] - curPos[1]) * 4.5;
            instructions.add(new double[]{1, y});
        }

        System.out.println(destination[0]+" : "+destination[1]);
        // set orientation towards destination
        if(destination[0] < curPos[0]) { // go  west
            instructions.add(new double[]{0, orientationToDegree.get(3)});
            synchronized (Main.orientation) {
                Main.orientation[0] = 3;
            }
        }else if (destination[0] > curPos[0]) { // go east
            instructions.add(new double[]{0, orientationToDegree.get(4)});
            synchronized (Main.orientation) {
                Main.orientation[0] = 4;
            }
        }

        // drive to the destination
        double x = ((destination[0] - curPos[0]));

        // drive to destination
        if(x<0){ // robot is pointing towards west
            if(orientationToDegree.get(orientation)==180) instructions.add(new double[]{1, -4.5*(x+2)});
            else if (orientationToDegree.get(orientation)==90) instructions.add(new double[]{1, -4.5*(x+4)});
            else if (orientationToDegree.get(orientation)==-90) instructions.add(new double[]{1, -4.5*x});
        }else if (x>0){ // robot is pointing towards east
            if(orientationToDegree.get(orientation)==180) instructions.add(new double[]{1, 4.5*(x-2)});
            else if (orientationToDegree.get(orientation)==90) instructions.add(new double[]{1, 4.5*(x)});
            else if (orientationToDegree.get(orientation)==-90) instructions.add(new double[]{1, 4.5*(x-4)});
        }

        return instructions;
    }
}

/***********************************************************************************************************************
 *                                             Bluetooth communicator                                                  *
 **********************************************************************************************************************/

class BTReceive implements Runnable {
    private DataOutputStream dos;
    private BTConnection connection;
    public boolean communicate = true;
    private DataInputStream dis;
    public final int[][] coords = new int[1][4];

    public BTReceive(){
        String connected = "Connected";
        String waiting = "Waiting...";
        String closing = "Closing...";

        System.out.println(waiting);
        connection = Bluetooth.waitForConnection();
        dis = connection.openDataInputStream();
        dos = connection.openDataOutputStream();

        System.out.println(connected);
        Sound.beep();


    }

    public int[] getCoord(){
        synchronized (coords){
            return coords[0];
        }
    }

    @Override
    public void run() {
        int[] coord = {0,0,0,0};
        boolean isBipped = false;
        while (communicate) {

            try {
                synchronized (Main.curCoord) {
                    for (int i : Main.curCoord[0]) {
                        dos.writeInt(i);
                    }
                }

                synchronized (Main.orientation){
                    dos.writeInt(Main.orientation[0]);
                }

                synchronized (Main.options){
                    dos.writeInt(Main.options[0]);
                }

                dos.flush();


                for (int i = 0; i < 4; i++) {
                    coord[i] = dis.readInt();
                }

                if (coord[3]==4 && !isBipped){
                    Sound.beep();
                    isBipped = true;
                }else if(coord[3] != 4) {
                    isBipped = false;
                }

                synchronized (coords) {
                    if (coord[0]!=coords[0][0] || coord[1]!= coords[0][1]) {
                        coords[0] = coord;
                        System.out.println(coord[0] + " :: " + coord[1]);
                    }
                }

                Thread.sleep(100);

//                dos.close();
            }catch (Exception e){}


        }

        try {
            dis.close();
            dos.close();
            connection.close();
//            LCD.clear();
        }catch (Exception e){}
    }

    private void communicateWithPython(DataInputStream dis, DataOutputStream dos, int[] coord) {
        String dataToSend = ":";

        synchronized (Main.curCoord){
            for (int i : Main.curCoord[0]) {
                dataToSend+=i;
                dataToSend+=",";
            }
        }


        synchronized (Main.orientation){dataToSend+=Main.orientation[0]; dataToSend+=",";}
        synchronized (Main.options){dataToSend+=Main.options[0];}

        try {
            dos.writeBytes(dataToSend);
            dos.flush();
            Thread.sleep(100);
        }catch (Exception e){}


        try {
            System.out.println("to receive");

            String receivedData = dis.readLine();
//                dis.wait(100);
                System.out.println("received: "+receivedData);


                boolean firstNumber = true;
                String firstNum = "";
                String secondNum = "";
                for (char c : receivedData.toCharArray()) {
                    if (c != ',' && firstNumber) {
                        firstNum += c;
                    } else if (c == ',' && firstNumber) {
                        firstNumber = false;
                        continue;
                    } else {
                        secondNum += c;
                    }
                }

                coord[0] = Integer.parseInt(firstNum);
                coord[1] = Integer.parseInt(secondNum);
                synchronized (coords) {
                    if (!Arrays.equals(coord, coords[0])) {
                        coords[0] = coord;
                        System.out.println(coord[0] + " :: " + coord[1]);
                    }
                }

        }catch (Exception e){
//            System.out.println(e+"");
            }
    }

}
