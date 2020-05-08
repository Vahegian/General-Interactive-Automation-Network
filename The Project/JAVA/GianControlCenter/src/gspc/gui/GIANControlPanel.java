package gspc.gui;

import gspc.gui.custom_objects.Colors;
import gspc.gui.custom_objects.CustButton;

import javax.swing.*;

public class GIANControlPanel extends JPanel {
    public final int sx;
    public final int sy;
    public CustButton startGianBut;
    public JRadioButton gianStreamRB;
    public JRadioButton extCamRB;
    public JRadioButton showImgRB;

    public GIANControlPanel(int sx, int sy) {
        this.sx = sx; this.sy = sy;
        setLayout(null);
        setBackground(Colors.white);

        JPanel p = new JPanel();
//        FlowLayout fl = new FlowLayout();
//        fl.setHgap(20);

        p.setLayout(null);
        p.setBounds(0,0,sx,sy);

        startGianBut = new CustButton("Start GIAN");

        gianStreamRB = new JRadioButton();

        JTextField ta = new JTextField();
        ta.setText("Use GIAN Stream");
        ta.setEditable(false);
        ta.setBorder(BorderFactory.createEmptyBorder());


        extCamRB = new JRadioButton();

        JTextField ta2 = new JTextField();
        ta2.setText("External Camera");
        ta2.setEditable(false);
        ta2.setBorder(BorderFactory.createEmptyBorder());

        showImgRB = new JRadioButton();

        JTextField ta3 = new JTextField();
        ta3.setText("Show Camera");
        ta3.setEditable(false);
        ta3.setBorder(BorderFactory.createEmptyBorder());

        int width = sx/5;
        int gap = width/6;

        int distFromBegining = 0;

        gianStreamRB.setLayout(null); gianStreamRB.setBounds(distFromBegining,0,gap, sy);
        ta.setLayout(null); ta.setBounds(distFromBegining+=gap,0,width,sy);
        extCamRB.setLayout(null); extCamRB.setBounds(distFromBegining+=width,0,gap,sy);
        ta2.setLayout(null); ta2.setBounds(distFromBegining+=gap,0,width,sy);
        showImgRB.setLayout(null); showImgRB.setBounds(distFromBegining+=width,0,gap,sy);
        ta3.setLayout(null); ta3.setBounds(distFromBegining+=gap,0,width,sy);
        startGianBut.setLayout(null); startGianBut.setBounds(distFromBegining+=width,(sy/2)-(sy/4),width,sy/2);

        System.out.println(sx+"  "+distFromBegining);

        p.add(gianStreamRB);
        p.add(ta);
        p.add(extCamRB);
        p.add(ta2);
        p.add(showImgRB);
        p.add(ta3);
        p.add(startGianBut);

        add(p);

    }
}
