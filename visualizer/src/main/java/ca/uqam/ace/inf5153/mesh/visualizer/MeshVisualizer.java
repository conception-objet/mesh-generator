package ca.uqam.ace.inf5153.mesh.visualizer;

import ca.uqam.ace.inf5153.mesh.io.MeshReader;
import ca.uqam.ace.inf5153.mesh.io.Structs.*;
import ca.uqam.ace.inf5153.mesh.visualizer.cli.Configuration;
import ca.uqam.ace.inf5153.mesh.visualizer.converters.AbstractConverter;
import ca.uqam.ace.inf5153.mesh.visualizer.converters.JPEGConverter;
import ca.uqam.ace.inf5153.mesh.visualizer.converters.PDFConverter;
import ca.uqam.ace.inf5153.mesh.visualizer.converters.PNGConverter;
import ca.uqam.ace.inf5153.mesh.visualizer.drawers.DebugDrawer;
import ca.uqam.ace.inf5153.mesh.visualizer.drawers.RegularDrawer;
import ca.uqam.ace.inf5153.mesh.visualizer.drawers.SVGDrawer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MeshVisualizer {

    private final Configuration config;
    private static Map<String, AbstractConverter> BINDINGS = new HashMap<>();

    public MeshVisualizer(Configuration config) {
        this.config = config;
        BINDINGS.put("PDF", new PDFConverter());
        BINDINGS.put("PNG", new PNGConverter());
        BINDINGS.put("JPG", new JPEGConverter());
    }

    public void proceed() {
        Mesh mesh = null;
        try {
            mesh = (new MeshReader()).readFromFile(config.getInputFile());
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        AbstractConverter conv = BINDINGS.get(config.getType());
        draw(mesh, conv == null ? Optional.empty() : Optional.of(conv));
    }

    private void draw(Mesh mesh, Optional<AbstractConverter> converter) {
        int height = Integer.parseInt(readMetadata(mesh, "height"));
        int width = Integer.parseInt(readMetadata(mesh, "width")) ;
        SVGDrawer drawer = (config.isDebug() ? new DebugDrawer(width, height) : new RegularDrawer(width, height));
        if (converter.isPresent()) {
            converter.get().render(drawer, mesh, config.getOutputFile());
        } else {
            drawer.run(mesh, config.getOutputFile());
        }
    }

    private String readMetadata(Mesh m, String key) {
        Optional<Property> prop = m.getPropertiesList().stream()
                .filter( p-> { return p.getKey().equals(key); }).findFirst();
        if(prop.isPresent()) {
            return prop.get().getValue();
        } else {
            throw new IllegalArgumentException("Missing property ["+ key + "] in "
                    + config.getInputFile());
        }

    }



}
