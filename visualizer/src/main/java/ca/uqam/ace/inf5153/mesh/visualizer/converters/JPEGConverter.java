package ca.uqam.ace.inf5153.mesh.visualizer.converters;

import org.apache.batik.apps.rasterizer.DestinationType;

public class JPEGConverter extends AbstractConverter {
    @Override
    protected DestinationType getOutputDestinationType() {
        return DestinationType.JPEG;
    }
}
