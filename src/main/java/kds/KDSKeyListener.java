package kds;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by cvium on 04-01-2016.
 */
public class KDSKeyListener implements KeyListener {

    Simulator s;

    public KDSKeyListener(Simulator s) {
        this.s = s;
    }
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_P) {
            s.pause();
        } else if (e.getKeyCode() == KeyEvent.VK_K) {
            //faster
            s.faster();
        } else if (e.getKeyCode() == KeyEvent.VK_J) {
            //slower
            s.slower();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
