package gspc.logic.java_to_py;

import java.io.IOException;

import static java.lang.Compiler.command;

public class ProcessCreator {
    private ProcessBuilder pb;
    private Process p;

    public ProcessCreator(){}

    public void lunchScript(String location){
        pb = new ProcessBuilder();
        pb.command(location);
        try {
            p = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void lunchScript(String location, String arg){
        if (!arg.isEmpty()) {
            pb = new ProcessBuilder();
            pb.command(location, arg);
            try {
                p = pb.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
