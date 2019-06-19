package gspc;


import gspc.gui.Main_Window;
import gspc.logic.tcp.RobotConnector;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static ExecutorService threads;
    public static void main(String[] args){
        threads = Executors.newFixedThreadPool(25);
        System.out.println("Starting");
        new Main_Window("GIAN Control Window");
    }
}
