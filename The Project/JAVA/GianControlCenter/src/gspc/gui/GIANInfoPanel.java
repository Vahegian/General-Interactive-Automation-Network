package gspc.gui;

import gspc.gui.custom_objects.Colors;

import javax.swing.*;
import java.awt.*;

public class GIANInfoPanel extends JPanel {
    public final int sx;
    public final int sy;
    public JTextField ok;
    public JTextField robotPos;
    public JTextField lable;
    public JTextField handPos;

    public GIANInfoPanel(int sx, int sy) {
        this.sx = sx; this.sy = sy;
        setLayout(null);

        JTextField ta = new JTextField();
        ta.setLayout(null);
        ta.setBorder(BorderFactory.createEmptyBorder());
        ta.setBackground(Colors.white);
        ta.setText("coords:");
        ta.setEditable(false);

        JTextField ta2 = new JTextField();
        ta2.setLayout(null);
        ta2.setBorder(BorderFactory.createEmptyBorder());
        ta2.setBackground(Colors.white);
        ta2.setText("Detected:");
        ta2.setEditable(false);

        JTextField ta3 = new JTextField();
        ta3.setLayout(null);
        ta3.setBorder(BorderFactory.createEmptyBorder());
        ta3.setBackground(Colors.white);
        ta3.setText("Robot:");
        ta3.setEditable(false);

        JTextField ta4 = new JTextField();
        ta4.setLayout(null);
        ta4.setBorder(BorderFactory.createEmptyBorder());
        ta4.setBackground(Colors.white);
        ta4.setText("OK:");
        ta4.setEditable(false);

        handPos = new JTextField();
        handPos.setLayout(null);
        handPos.setBorder(BorderFactory.createEmptyBorder());
        handPos.setBackground(Colors.white);
        handPos.setForeground(Colors.blue);
        handPos.setEditable(false);

        lable = new JTextField();
        lable.setLayout(null);
        lable.setBorder(BorderFactory.createEmptyBorder());
        lable.setBackground(Colors.white);
        lable.setForeground(Colors.blue);
        lable.setEditable(false);

        robotPos = new JTextField();
        robotPos.setLayout(null);
        robotPos.setBorder(BorderFactory.createEmptyBorder());
        robotPos.setBackground(Colors.white);
        robotPos.setForeground(Colors.blue);
        robotPos.setEditable(false);

        ok = new JTextField();
        ok.setLayout(null);
        ok.setBorder(BorderFactory.createEmptyBorder());
        ok.setBackground(Colors.white);
        ok.setForeground(Colors.blue);
        ok.setEditable(false);

        int width = (int) ((sx/8)+0.5);
        ta.setBounds(0,0,width, sy);
        handPos.setBounds(width, 0, width,sy);
        ta2.setBounds(width*2, 0, width, sy);
        lable.setBounds(width*3, 0, width, sy);
        ta3.setBounds(width*4, 0, width, sy);
        robotPos.setBounds(width*5, 0, width, sy);
        ta4.setBounds(width*6, 0, width, sy);
        ok.setBounds(width*7, 0, width, sy);


        add(ta);
        add(handPos);
        add(ta2);
        add(lable);
        add(ta3);
        add(robotPos);
        add(ta4);
        add(ok);
    }
}
