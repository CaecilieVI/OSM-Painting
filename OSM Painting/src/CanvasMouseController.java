/**
 * Created by caecilieiversen on 22/02/2017.
 */

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class CanvasMouseController extends MouseAdapter {
    Model model;
    DrawCanvas canvas;
    Point2D lastMousePosition;

    public CanvasMouseController(DrawCanvas canvas, Model model) {
        this.model = model;
        this.canvas = canvas;
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
        canvas.addMouseWheelListener(this);
    }

    /**
     * {@inheritDoc}
     *
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {
        lastMousePosition = e.getPoint();
    }

    /**
     * {@inheritDoc}
     *
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * {@inheritDoc}
     *
     * @param e
     *
     * @since 1.6
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        Point2D currentMousePosition = e.getPoint();
        double dx = currentMousePosition.getX() - lastMousePosition.getX();
        double dy = currentMousePosition.getY() - lastMousePosition.getY();
        canvas.pan(dx, dy);
        lastMousePosition = currentMousePosition;
    }

    /**
     * {@inheritDoc}
     *
     * @param e
     *
     * @since 1.6
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double factor = Math.pow(0.9, e.getWheelRotation());
        Point2D currentMousePosition = e.getPoint();
        double dx = currentMousePosition.getX();
        double dy = currentMousePosition.getY();
        canvas.pan(-dx, -dy);
        canvas.zoom(factor);
        canvas.pan(dx, dy);
    }
}
