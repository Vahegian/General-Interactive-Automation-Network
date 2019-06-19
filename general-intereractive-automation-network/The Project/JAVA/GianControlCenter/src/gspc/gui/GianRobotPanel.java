package gspc.gui;

import gspc.gui.custom_objects.Colors;
import gspc.gui.custom_objects.CustButton;

import javax.swing.*;

public class GianRobotPanel extends JPanel {
    public final int sx;
    public final int sy;
    public CustButton connectToRobotButton;
    public JTextField userInput;
    public CustButton sendDataToRobotButton;

    public GianRobotPanel(int sx, int sy) {
        this.sx = sx; this.sy = sy;
//        setBackground(Colors.white);
        setLayout(null);

        int width = (sx/3)-30;


        connectToRobotButton = new CustButton("Connect");

        sendDataToRobotButton = new CustButton("Send Coord");

        userInput = new JTextField();
        userInput.setBackground(Colors.redLite);
        userInput.setLayout(null);
        userInput.setText("Not Connected");

        connectToRobotButton.setLayout(null); connectToRobotButton.setBounds(30,(sy/2)-(sy/4), width,sy/2);
        sendDataToRobotButton.setBounds(width+40,(sy/2)-(sy/4),width,sy/2);
        userInput.setBounds((width*2)+50,(sy/2)-(sy/4),width,sy/2);

        add(connectToRobotButton);
        add(sendDataToRobotButton);
        add(userInput);


    }
}
