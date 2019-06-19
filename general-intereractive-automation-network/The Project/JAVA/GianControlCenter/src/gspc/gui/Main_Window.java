package gspc.gui;

import gspc.Main;
import gspc.gui.custom_objects.Colors;
import gspc.gui.custom_objects.CustButton;
import gspc.logic.java_to_py.MyStack;
import gspc.logic.java_to_py.ProcessCreator;
import gspc.logic.main.Main_window_logic;

import javax.swing.*;
import java.util.LinkedList;

public class Main_Window extends SideFrame {

    private Main_window_logic mwl;
    //    private Map<String, Object> buttonMap;
    private Screen screen;
    private ProcessCreator pyc;
    private MyStack jio;
    private LinkedList<CustButton> buttonList;

    public Main_Window(String title) {
        super(title, dim.width/2,0,dim.width/2, dim.height/2);
//        makeGUI();
        setBackground(Colors.white);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBounds(0,0,sx,sy);

        GIANControlPanel gcp = new GIANControlPanel(sx,50);
        gcp.setBounds(0,0,gcp.sx,gcp.sy);
        mainPanel.add(gcp);

        GianRobotPanel grp = new GianRobotPanel(sx/2, 50);
        grp.setBounds(grp.sx,grp.sy,grp.sx,grp.sy);
        mainPanel.add(grp);
//
        GIANGridPanel ggp = new GIANGridPanel(sx/2,sy-(gcp.sy*2));
        ggp.setBounds(0,gcp.sy,ggp.sx,ggp.sy);
        mainPanel.add(ggp);


        GIANInfoPanel gip = new GIANInfoPanel(sx/2, 50);
        gip.setBounds(0,ggp.sy+50, gip.sx, gip.sy);
        mainPanel.add(gip);

        Screen gspc = new Screen(sx/2, sy-(grp.sy*2));
        gspc.setBounds(gspc.sx, grp.sy+50, gspc.sx, gspc.sy);
        mainPanel.add(gspc);

        add(mainPanel);
        mwl = new Main_window_logic(gcp, ggp, gip, grp, gspc);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);



    }
}
