/*
cutomised button for CoinCapWindow
 */
package gspc.gui.custom_objects;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class CustButton extends JButton {

    public CustButton(String text){
//        setLayout(null);
        setBackground(Colors.white);
        setForeground(Colors.blue);
//        setBounds(x,y,w,h);
        setText(text);
//        this.setBorder(BorderFactory.createEmptyBorder());
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                setBackground(Colors.greenLight);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
//                setBackground(Colors.white);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(Colors.lightBlue);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(Colors.white);
            }
        });
        setVisible(true);
    }
}
