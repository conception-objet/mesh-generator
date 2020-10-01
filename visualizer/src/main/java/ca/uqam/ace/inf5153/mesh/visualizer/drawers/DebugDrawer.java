package ca.uqam.ace.inf5153.mesh.visualizer.drawers;

import ca.uqam.ace.inf5153.mesh.io.Structs;
import ca.uqam.ace.inf5153.mesh.io.Structs.*;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

public class DebugDrawer extends SVGDrawer {

    private static final Color CENTROID_COLOR = new Color(42,78,113);
    private static final Color NEIGHBOR_COLOR = new Color(169,169,169,50);
    private static final Color BORDER_COLOR = new Color(106,133,157,125);

    public DebugDrawer(int w, int h) {
        super(w, h);
    }

    @Override
    protected void process(Mesh mesh, Graphics2D g) {
        Stroke stroke = new BasicStroke(0.5f);
        g.setStroke(stroke);
        mesh.getPolygonsList().forEach( p -> { handle(p, mesh, g); });
        print(mesh);
    }

    private void print(Mesh mesh) {
        System.out.println(mesh);
    }

    private void handle(Structs.Polygon p, Mesh mesh, Graphics2D g) {
        drawNeighbours(p, mesh, g);
        drawTile(p, mesh, g, BORDER_COLOR);
        drawCentroid(p, mesh, g);
    }


    private void drawNeighbours(Structs.Polygon polygon, Mesh mesh, Graphics2D g) {
        Structs.Point centroid = mesh.getPoints(polygon.getCentroidIdx());
        polygon.getNeighborsList().forEach( i -> {
            Structs.Point that = mesh.getPoints(i);
            Line2D line = new Line2D.Float(centroid.getX(), centroid.getY(),that.getX(),that.getY());
            Color old = g.getColor();
            g.setColor(NEIGHBOR_COLOR);
            g.draw(line);
            g.setColor(old);
        });
    }

    private void drawCentroid(Structs.Polygon polygon, Structs.Mesh mesh, Graphics2D g) {
        Structs.Point p = mesh.getPoints(polygon.getCentroidIdx());
        Ellipse2D circle = new Ellipse2D.Float(p.getX()-1.5f, p.getY()-1.5f, 3, 3);
        Color old = g.getColor();
        g.setColor(CENTROID_COLOR);
        g.fill(circle);
        g.setColor(old);
    }

}
