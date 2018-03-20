/**
 * Created by caecilieiversen on 22/02/2017.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.util.Observable;
import java.util.Observer;

public class DrawWindow implements Observer {
    Model model;
    JFrame window;
    DrawCanvas canvas;

    public DrawWindow(Model model) {
        this.model = model;
        model.addObserver(this);
        window = new JFrame("OSM painting");
        window.setLayout(new BorderLayout());
        canvas = new DrawCanvas(model);
        canvas.setPreferredSize(new Dimension(2000, 1000));
        canvas.setBackground(new Color(146, 219, 237));
        new CanvasMouseController(canvas, model);
        window.add(canvas, BorderLayout.CENTER);
        window.pack();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
        canvas.pan(-model.getMinLon(), -model.getMaxLat());
        canvas.zoom(window.getWidth() / (model.getMaxLon() - model.getMinLon()));
        new WindowKeyController(this, model);
    }

    /**
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>Observable</tt> object's
     * <code>notifyObservers</code> method to have all the object's
     * observers notified of the change.
     *
     * @param o   the observable object.
     * @param arg an argument passed to the <code>notifyObservers</code>
     */
    @Override
    public void update(Observable o, Object arg) {

    }

    public void addKeyListener(KeyListener keyListener) {
        window.addKeyListener(keyListener);
    }

    public void toggleAA() {canvas.toggleAA();}

    public void toggleRoads() {canvas.toggleRoads();}

    public void toggleBuildings() {canvas.toggleBuildings();}

    public void toggleParks() {canvas.toggleParks();}

    public void toggleWater() {canvas.toggleWater();}

    public void toggleFootway() {canvas.toggleFootways();}
}
