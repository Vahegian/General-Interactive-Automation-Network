package gspc.gui.custom_objects; /**
 * Created by Vahe Grigoryan on 26/10/2017.
 */


import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Colors implements Runnable {
    private Random random = new Random();
    public Colors(String str){
        if(str.equals("shades")) this.run();

    }

    public static Color white = new Color(250,250,250);
    public static Color red = new Color(140,0,0);
    public static Color redLite = new Color(189,50,70);
    public static Color green = new Color(0,128,64);
    public static Color black = new Color(0,3,2);
    public static Color blue = new Color(94,143,230);
    public static Color greenLight = new Color(189,240,160);
    public static Color lightBlue = new Color(205,224,244);
    public static Color binanceYellow = new Color(244, 187, 45);


    public Color randomColor(){
        int r = random.nextInt(200)+20;
        int g = random.nextInt(180)+20;
        int b = random.nextInt(190)+23;

        return new Color(r,g,b);
    }


    List<Color> shadesOfRed = new ArrayList<Color>();
    List<Color> shadesOfGreen = new ArrayList<Color>();
    List<Color> shadesOfBlue = new ArrayList<Color>();
    public List<Color> getBlueShades(){
        return shadesOfBlue;
    }
    public List<Color> getRedShades(){
        return shadesOfRed;
    }
    public List<Color> getGreenShades(){
        return shadesOfGreen;
    }

    /*
        happens on a separate thread creates shades of red, blue and green in a list.
        if object is called with string other than "shades" this will not happen
     */
    @Override
    public void run() {
        for(int i = 0; i<250;i++){
            shadesOfRed.add(new Color(i,0,0));
        }
        for(int i = 0; i<250;i++){
            shadesOfGreen.add(new Color(0,i,0));
        }
        for(int i = 0; i<250;i++){
            shadesOfBlue.add(new Color(0,0,i));
        }
    }
}
