/**
 * Created by caecilieiversen on 22/02/2017.
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.StrokeBorder;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class DrawCanvas extends JComponent implements Observer {
    Model model;
    AffineTransform transform = new AffineTransform();
    boolean antiAlias;
    boolean showRoads = true;
    boolean showResidential = true;
    boolean showBuildings = true;
    boolean showParks = true;
    boolean showWater = true;
    boolean showFootways = true;

    public DrawCanvas(Model model) {
        this.model = model;
        model.addObserver(this);
    }

    /**
     * Calls the UI delegate's paint method, if the UI delegate
     * is non-<code>null</code>.  We pass the delegate a copy of the
     * <code>Graphics</code> object to protect the rest of the
     * paint code from irrevocable changes
     * (for example, <code>Graphics.translate</code>).
     * <p>
     * If you override this in a subclass you should not make permanent
     * changes to the passed in <code>Graphics</code>. For example, you
     * should not alter the clip <code>Rectangle</code> or modify the
     * transform. If you need to do these operations you may find it
     * easier to create a new <code>Graphics</code> from the passed in
     * <code>Graphics</code> and manipulate it. Further, if you do not
     * invoker super's implementation you must honor the opaque property,
     * that is
     * if this component is opaque, you must completely fill in the background
     * in a non-opaque color. If you do not honor the opaque property you
     * will likely see visual artifacts.
     * <p>
     * The passed in <code>Graphics</code> object might
     * have a transform other than the identify transform
     * installed on it.  In this case, you might get
     * unexpected results if you cumulatively apply
     * another transform.
     *
     *
     * @see #paint
     */
    @Override
    protected void paintComponent(Graphics _g) {
        Graphics2D g = (Graphics2D) _g;
        g.setTransform(transform);

        g.setColor(new Color(146, 219, 237));
        g.fillRect(0,0, getWidth(), getHeight());

        g.setStroke(new BasicStroke(Float.MIN_VALUE));
        if (antiAlias) g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (showWater) {
            g.setColor(new Color(156, 202, 207));
            for (Shape s : model.get(WayType.WATER)) {
                g.fill(s);
            }
        }

        g.setColor(Color.LIGHT_GRAY);
        g.setStroke(new BasicStroke(0.00001f));
        for (Shape s : model.get(WayType.COASTLINE)) {
            g.draw(s);
        }

        g.setColor(new Color(170, 228, 180));
        for (Shape s : model.get(WayType.PARK)) {
            g.fill(s);
        }

        // g.setColor(new Color(108, 153,111));
        for (Shape s : model.get(WayType.FOREST)) {
            BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_3BYTE_BGR);
            try {
                image = ImageIO.read(new File("/Desktop/red_stripes.jpg"));
            } catch (IOException e) { }
            Graphics2D graphics2D = image.createGraphics();
            TexturePaint texturePaint = new TexturePaint(image, new Rectangle(0, 0, 50, 50) {
            });
            graphics2D.setPaint(texturePaint);
            graphics2D.fillRect(0, 0,50, 50);
            graphics2D.fill(s);
            graphics2D.dispose();
        }

        g.setColor(new Color(244, 224, 169));
        for (Shape s : model.get(WayType.FARMLAND)) {
            g.fill(s);
        }

        if (showBuildings) {
            g.setColor(new Color(190, 178, 167));
            for (Shape s : model.get(WayType.BUILDING)) {
                g.fill(s);
            }
        }

        if (showFootways) {
            g.setColor(Color.gray);
            g.setStroke(new BasicStroke(0.00001f));
            for (Shape s : model.get(WayType.FOOTWAY)) {
                g.draw(s);
            }
        }

        if (showRoads) {
            g.setColor(Color.GRAY);
            g.setStroke(new BasicStroke(0.00006f));
            for (Shape s : model.get(WayType.ROAD)) g.draw(s);
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(0.00004f));
            for (Shape s : model.get(WayType.ROAD)) g.draw(s);
        }

        if (showResidential) {
            g.setColor(Color.GRAY);
            g.setStroke(new BasicStroke(0.00004f));
            for (Shape s : model.get(WayType.RESIDENTIAL_ROAD)) g.draw(s);
            g.setColor(Color.ORANGE);
            g.setStroke(new BasicStroke(0.00002f));
            for (Shape s : model.get(WayType.RESIDENTIAL_ROAD)) g.draw(s);
        }

    }

    public void pan(double dx, double dy) {
        transform.preConcatenate(AffineTransform.getTranslateInstance(dx, dy));
        repaint();
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
        repaint();
    }

    public void zoom(double factor) {
        transform.preConcatenate(AffineTransform.getScaleInstance(factor, factor));
        repaint();
    }

    public Point2D toModelCoords(Point2D lastMousePosition) {
        try {
            return transform.inverseTransform(lastMousePosition, null);
        } catch (NoninvertibleTransformException e) {
            throw new RuntimeException(e);
        }
    }

    public void toggleAA() {
        antiAlias = !antiAlias;
        repaint();
    }

    public void toggleRoads() {
        showRoads = !showRoads;
        repaint();
    }

    public void toggleBuildings() {
        showBuildings = !showBuildings;
        repaint();
    }

    public void toggleParks() {
        showParks = !showParks;
        repaint();
    }

    public void toggleWater() {
        showWater = !showWater;
        repaint();
    }

    public void toggleFootways() {
        showFootways = !showFootways;
        repaint();
    }
}
