package kds;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by cvium on 04-01-2016.
 */
public class PauseListener implements KeyListener {

    Simulator s;

    public PauseListener(Simulator s) {
        this.s = s;
    }
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_P) {
            s.pause();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
