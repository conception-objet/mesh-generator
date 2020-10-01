package ca.uqam.ace.inf5153.mesh.generator;

import ca.uqam.ace.inf5153.mesh.generator.cli.Configuration;
import ca.uqam.ace.inf5153.mesh.generator.exporter.MeshExporter;
import ca.uqam.ace.inf5153.mesh.generator.geom.CentroidGenerator;
import ca.uqam.ace.inf5153.mesh.generator.geom.DelaunayTriangulation;
import ca.uqam.ace.inf5153.mesh.generator.geom.VoronoiBinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class MeshBuilder {

    private final Configuration config;
    private final CentroidGenerator generator;
    private final VoronoiBinder binder;
    private final DelaunayTriangulation triangulation;

    public MeshBuilder(Configuration config) {
        this.config = config;
        this.generator = new CentroidGenerator(config.getWidth(), config.getHeight());
        this.binder = new VoronoiBinder(config.getWidth(), config.getHeight());
        this.triangulation = new DelaunayTriangulation();
    }

    public void proceed() {
        Set<Coordinate> sites = generator.generate(config.getPolygons(), config.getSmoothing());
        Map<Coordinate, Polygon> tesselation = binder.bind(sites);
        Set<Set<Coordinate>> neighbors = triangulation.process(tesselation.keySet());
        MeshExporter exporter = new MeshExporter(tesselation,neighbors);
        try {
            exporter.serialize(config.getOutput(), config.getWidth(), config.getHeight());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
