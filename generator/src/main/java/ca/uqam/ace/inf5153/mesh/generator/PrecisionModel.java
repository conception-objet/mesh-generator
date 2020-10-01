package ca.uqam.ace.inf5153.mesh.generator;

import org.locationtech.jts.geom.GeometryFactory;

public class PrecisionModel {

    private static final GeometryFactory factory =
            new GeometryFactory(new org.locationtech.jts.geom.PrecisionModel(1000));

    public static GeometryFactory getFactory() {
        return factory;
    }

}
