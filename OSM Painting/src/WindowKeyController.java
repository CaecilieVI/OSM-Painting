/**
 * Created by caecilieiversen on 22/02/2017.
 */

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class WindowKeyController extends KeyAdapter {
    Model model;
    DrawWindow window;;

    public WindowKeyController(DrawWindow window, Model model) {
        window.addKeyListener(this);
        this.window = window;
        this.model = model;
    }

    /**
     * Invoked when a key has been pressed.
     *
     * @param e
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'x':
                window.toggleAA();
                break;
            case 's':
                model.save("savegame.bin");
                break;
            case 'l':
                model.load("savegame.bin");
                break;
            case '1':
                window.toggleRoads();
                break;
            case '2':
                window.toggleBuildings();
                break;
            case '3':
                window.toggleParks();
                break;
            case '4':
                window.toggleWater();
                break;
            case '5':
                window.toggleFootway();
                break;
        }
    }
}
