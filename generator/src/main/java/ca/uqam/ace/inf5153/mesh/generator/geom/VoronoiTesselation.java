package ca.uqam.ace.inf5153.mesh.generator.geom;

import ca.uqam.ace.inf5153.mesh.generator.PrecisionModel;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.triangulate.VoronoiDiagramBuilder;

import java.util.HashSet;
import java.util.Set;

public class VoronoiTesselation {

    private final int width;
    private final int height;

    public VoronoiTesselation(int width, int heigth) {
        this.height = heigth;
        this.width = width;
    }

    public VoronoiTesselation(int width, int heigth, boolean clip) {
        this(width,heigth);
    }

    public Geometry build(Set<Coordinate> sites) {
        VoronoiDiagramBuilder voronoi = new VoronoiDiagramBuilder();
        voronoi.setSites(sites);
        Geometry raw = voronoi.getDiagram(PrecisionModel.getFactory());
        VoronoiClipper clipper = new VoronoiClipper(width, height);
        raw.apply(clipper);
        return PrecisionModel.getFactory().createGeometryCollection(clipper.getClipped().toArray(new Geometry[0]));
    }

    private static class VoronoiClipper implements GeometryFilter {

        private final Geometry boundingBox;

        public Set<Geometry> getClipped() {
            return clipped;
        }

        private final Set<Geometry> clipped = new HashSet<>();

        public VoronoiClipper(int width, int height) {
            this.boundingBox = PrecisionModel.getFactory().createPolygon(new Coordinate[]{
                    new Coordinate(0,0), new Coordinate(0,height),
                    new Coordinate(width,height), new Coordinate(width,0),
                    new Coordinate(0,0) });
        }

        @Override
        public void filter(Geometry geometry) {
            if (geometry instanceof Polygon) {
                collect((Polygon) geometry);
            }
        }

        private void collect(Polygon original) {
            clipped.add(original.intersection(boundingBox));
        }

    }

}
