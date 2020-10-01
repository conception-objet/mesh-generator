package ca.uqam.ace.inf5153.mesh.visualizer.drawers;

import ca.uqam.ace.inf5153.mesh.io.Structs;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RegularDrawer extends SVGDrawer {

    private static final Color LIGHT_GRAY = new Color(169,169,169,50);

    public RegularDrawer(int w, int h) {
        super(w, h);
    }

    @Override
    protected void process(Structs.Mesh mesh, Graphics2D g) {
        Stroke stroke = new BasicStroke(0.5f);
        g.setStroke(stroke);
        mesh.getPolygonsList().forEach( p -> handle(p, mesh, g));
        mesh.getSegmentsList().stream()
                .filter(  s -> s.getPropertiesCount() != 0)
                .forEach( s -> handle(s, mesh, g));
        mesh.getPointsList().stream()
                .filter(  p -> p.getPropertiesCount() != 0)
                .forEach( p -> handle(p, mesh, g));
    }

    private void handle(Structs.Polygon p, Structs.Mesh mesh, Graphics2D g) {
        drawTile(p,mesh,g,LIGHT_GRAY);
    }

    private void handle(Structs.Segment s, Structs.Mesh mesh, Graphics2D g) {
        Optional<Color> color = readColor(s.getPropertiesList());
        Color c = Color.BLACK;
        if(color.isPresent())
            c = color.get();
        Integer t = 1;
        Optional<String> thickness = readProperty("thickness", s.getPropertiesList());
        if(thickness.isPresent())
            t = Integer.parseInt(thickness.get());
        Stroke stroke = new BasicStroke(t);
        Optional<String> style = readProperty("style", s.getPropertiesList());
        if (style.isPresent()) {
            switch(style.get()){
                case "dashed":
                    float[] pattern = {2f, 2f};
                    stroke = new BasicStroke(t, BasicStroke.CAP_BUTT,
                            BasicStroke.JOIN_MITER, 1.0f, pattern, 2.0f);
            }
        }
        Stroke oldStroke = g.getStroke();
        Color oldColor = g.getColor();
        g.setStroke(stroke);
        g.setColor(c);
        Structs.Point start = mesh.getPoints(s.getV1Idx());
        Structs.Point end = mesh.getPoints(s.getV2Idx());
        Line2D line = new Line2D.Float(start.getX(), start.getY(),end.getX(),end.getY());
        g.draw(line);
        g.setColor(oldColor);
        g.setStroke(oldStroke);
    }

    private void handle(Structs.Point p, Structs.Mesh mesh, Graphics2D g) {
        Optional<Color> color = readColor(p.getPropertiesList());
        Color c = Color.BLACK;
        if(color.isPresent())
            c = color.get();
        Integer t = 3;
        Optional<String> thickness = readProperty("thickness", p.getPropertiesList());
        if(thickness.isPresent())
            t = Integer.parseInt(thickness.get());
        Ellipse2D circle = new Ellipse2D.Float(p.getX()-(t/2.0f), p.getY()-(t/2.0f), t, t);
        Color old = g.getColor();
        g.setColor(c);
        g.fill(circle);
        g.setColor(old);
    }


}
