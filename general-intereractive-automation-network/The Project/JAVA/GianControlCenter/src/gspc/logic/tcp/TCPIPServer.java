package gspc.logic.tcp;

import gspc.Main;
import gspc.logic.main.StartGianStream;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPIPServer {
    private String TAG ="gspc.logic.tcp.TCPIPServer";

    private ServerSocket serverSocket;
    private Socket clientSocket;

    private ObjectInputStream objectIN;
    private final Object[] objectBuffer = {null};
    private boolean listeningForObjects = false;


    public void makeServer(int portNumber){
        try {
            serverSocket = new ServerSocket(portNumber);
            System.out.println(TAG+" > Server Created");
            clientSocket = serverSocket.accept();
            System.out.println(TAG+" > clientConnected");
            openCommunication();
        }catch (Exception e){
            System.err.println(TAG+"> makeServer: "+e);
        }
    }

    private void openCommunication(){
        try {
            objectIN = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void listenForObjects(){
        Main.threads.submit(new Runnable() {
            @Override
            public void run() {
                listeningForObjects=true;
                while (listeningForObjects) {
                    try {
                        Thread.sleep(StartGianStream.DELAY);
                        synchronized (objectBuffer) {
                            try {
                                objectBuffer[0] = objectIN.readObject();
                            }catch (EOFException e){}
//                            System.err.println(TAG+ ": Recieved");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public Object getObject(){
        synchronized (objectBuffer){
            return objectBuffer[0];
        }
    }

    public void stopListeningForObjects(){
        listeningForObjects=false;
    }
}
