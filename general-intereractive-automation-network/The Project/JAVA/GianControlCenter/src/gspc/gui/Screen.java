package gspc.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Screen extends JPanel {
    private final ImageIcon imageicon;
    public final int sx;
    public final int sy;
    private String TAG = "gspc.gui.Screen:> ";

    public Screen(int sx, int sy) {
//        super(title, lx, ly, 360, 640);
        setLayout(null);
        this.sx = sx; this.sy = sy;
        imageicon = new ImageIcon();
        JLabel mainLabel = new JLabel(imageicon);
        mainLabel.setBounds(50,0,sx,sy);
        add(mainLabel);
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        repaint();
    }


    public void updateImage(BufferedImage image){
        try {
            imageicon.setImage(resizeTheImage(image));
            repaint();
        }catch (Exception e){System.err.println(TAG+"Problem when Updating imageicon");}
    }

    private Image resizeTheImage(BufferedImage image) {
        AffineTransform at = new AffineTransform();
        at.scale(0.5, 0.5);
        BufferedImage resizedImage = new BufferedImage(sx, sy, BufferedImage.TYPE_INT_ARGB);
        AffineTransformOp atOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        resizedImage = atOp.filter(image, resizedImage);
        return resizedImage;
    }
}
