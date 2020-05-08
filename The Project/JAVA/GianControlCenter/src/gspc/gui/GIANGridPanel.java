package gspc.gui;

import gspc.Main;
import gspc.gui.custom_objects.Colors;

import javax.swing.*;
import java.awt.*;

public class GIANGridPanel extends JPanel implements Runnable {
    public final int sx;
    public final int sy;

    private int lineGapX;
    private int lineGapY;
    private final int[] handPos= {11,11};
    private boolean terminate = false;

    public GIANGridPanel(int sx, int sy){
        this.sx = sx; this.sy = sy;
        setBackground(Colors.white);
        repaint();
    }

    public void updateHandPos(int x, int y){
        if((x>-1 && x<12) && (y>-1 && y<12)){
            synchronized (handPos){
                handPos[0] = x;
                handPos[1] = y;
            }
        }
    }

    public void terminate(){
        terminate = true;
    }

    public void start(){
        terminate=false;
        Main.threads.submit(this);
    }

    @Override
    public void paint (Graphics g){
        super.paint(g);
        Graphics2D graphics = (Graphics2D) g;
//        paintMessage(graphics);
        paintGrid(graphics, sx, sy, 12,12);
        synchronized (handPos) {
            paintHandPOS(graphics, handPos[0], handPos[1]);
        }


    }

    private void paintHandPOS(Graphics2D graphics, int handX, int handY) {
        paintPos(graphics, handX, handY);
        paintPos(graphics, handX+1, handY);
        paintPos(graphics, handX, handY+1);
        paintPos(graphics, handX+1, handY+1);
    }

    private void paintPos(Graphics2D graphics, int x, int y){
        if(x>11)x-=(x-11);
        if(y>11)y-=(y-11);
        y = (y-11)*-1;
        graphics.setColor(Colors.blue);
        int ovalWidth=lineGapX/2; int ovalHeight = lineGapY/2;
        int cellMidPointX = (lineGapX-ovalWidth); int cellMidPointY =(lineGapY-ovalHeight);
        graphics.fillOval(lineGapX*x+(cellMidPointX-(ovalWidth/2)), lineGapY*y+(cellMidPointY-(ovalHeight/2)), ovalWidth, ovalHeight);
    }

    private void paintMessage(Graphics2D g, int x, int y, String message){
        g.setColor(Colors.blue);
        g.drawString(message, x, y);
    }

    private void paintGrid(Graphics2D graphics, int width, int height, int stepsX, int stepsY){
        graphics.setColor(Colors.white);
        graphics.fillRect(0,0, width, height);

        lineGapX =(int) ((width/stepsX)+0.5);
        lineGapY = (int) ((height/stepsY)+0.5);

        int coordX = 0;
        int coordY = 0;

        graphics.setColor(Colors.black);

        for (int i = 0; i<=stepsX; i++){
            graphics.drawLine(coordX, coordY, coordX, height);
            coordX+=lineGapX;
        }

        coordX = 0;
        coordY = 0;
        for (int i = 0; i<=stepsY; i++){
            graphics.drawLine(coordX, coordY, width, coordY);
            coordY+=lineGapY;
        }

    }

    @Override
    public void run() {
        while(!terminate){
            try {
                Thread.sleep(10);
                repaint();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
