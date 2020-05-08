package gspc.gui;

import javax.swing.*;
import java.awt.*;

public abstract class SideFrame extends JFrame {
    int sx,sy;

    static Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

    public SideFrame(String title, int lx, int ly, int sX, int sY) {
        this.sx = sX; this.sy = sY-30;
        setLayout(null);
//        setContentPane(sp);

        // Using sY instead of sy i subtracted thr toolBar from the height
        setBounds(lx,ly,sx,sY);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setBackground(Color.white);
        setTitle(title);

    }
}
