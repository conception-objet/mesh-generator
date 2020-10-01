package ca.uqam.ace.inf5153.mesh.visualizer.drawers;

import ca.uqam.ace.inf5153.mesh.io.Structs;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.locationtech.jts.algorithm.ConvexHull;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import java.awt.*;
import java.awt.geom.Path2D;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class SVGDrawer {

    private final int width;
    private final int height;

    public SVGDrawer(int w, int h) {
        this.width = w;
        this.height = h;
    }

    public void run(Structs.Mesh mesh, String output) {
        DOMImplementation dom = GenericDOMImplementation.getDOMImplementation();
        String svgNS = "http://www.w3.org/2000/svg";
        Document doc = dom.createDocument(svgNS, "svg", null);
        SVGGraphics2D g = new SVGGraphics2D(doc);
        g.setSVGCanvasSize(new Dimension(this.width, this.height));
        g.setColor(Color.BLACK);
        process(mesh, g);
        try {
            g.stream(output, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract void process(Structs.Mesh mesh, Graphics2D g);


    protected void drawTile(Structs.Polygon polygon, Structs.Mesh mesh, Graphics2D g, Color border) {
        Set<Coordinate> points =
                polygon.getSegmentIdxList()
                        .stream()
                        .map(mesh::getSegments)
                        .flatMap( s -> (new HashSet<>(Arrays.asList(s.getV1Idx(),s.getV2Idx()))).stream())
                        .map(mesh::getPoints)
                        .map( p -> new Coordinate(p.getX(),p.getY()))
                        .collect(Collectors.toSet());
        Coordinate[] convexHull = new ConvexHull(points.toArray(new Coordinate[0]),
                new GeometryFactory(new org.locationtech.jts.geom.PrecisionModel(1000)))
                .getConvexHull().getCoordinates();
        Path2D path = new Path2D.Float();
        path.moveTo(convexHull[0].x, convexHull[0].y);
        for(int i = 1; i < convexHull.length; i++) {
            path.lineTo(convexHull[i].x, convexHull[i].y);
        }
        path.closePath();
        Color old = g.getColor();
        g.setColor(border);
        g.draw(path);
        Optional<Color> fill = readColor(polygon.getPropertiesList());
        if(fill.isPresent()) {
            g.setColor(fill.get());
            g.fill(path);
        }
        g.setColor(old);
    }

    protected Optional<String> readProperty(String key, java.util.List<Structs.Property> props) {
        Optional<Structs.Property> prop = props.stream()
                .filter(pr -> {
                    return pr.getKey().equals(key);
                }).findFirst();
        if(prop.isPresent())
            return Optional.of(prop.get().getValue());
        return Optional.empty();
    }

    protected Optional<Color> readColor(java.util.List<Structs.Property> props) {
        Optional<String> prop = readProperty("color", props);
        if (prop.isPresent()) {
            String[] encoded = prop.get().split(":");
            Color c;
            switch (encoded.length) {
                case 3:
                    c = new Color(Integer.parseInt(encoded[0]),
                            Integer.parseInt(encoded[1]), Integer.parseInt(encoded[2]));
                    return Optional.of(c);
                case 4:
                    c = new Color(Integer.parseInt(encoded[0]), Integer.parseInt(encoded[1]),
                            Integer.parseInt(encoded[2]), Integer.parseInt(encoded[3]));
                    return Optional.of(c);
                default:
                    throw new IllegalArgumentException("Unable to decode [color] : " + prop.get());
            }
        }
        return Optional.empty();
    }

}
