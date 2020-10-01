package ca.uqam.ace.inf5153.mesh.demo;

import ca.uqam.ace.inf5153.mesh.io.*;
import ca.uqam.ace.inf5153.mesh.io.Structs.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;


public class Demonstration {

    public static void main(String[] args) {
        try {
            Mesh mesh = new MeshReader().readFromFile(args[0]);
            int height = Integer.parseInt(readMetadata(mesh, "height"));
            int width = Integer.parseInt(readMetadata(mesh, "width"));
            Mesh updated = process(mesh, width, height);
            (new MeshWriter()).writeToFile(updated, args[1]);
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }

    private static Mesh process(Mesh mesh, int w, int h) {
        // Switch to "edition" mode
        Mesh.Builder builder = mesh.toBuilder();
        identifyBorders(builder, w, h);
        int centre = findCentralPolygon(builder, w, h);
        drawPath(builder, centre, 5);
        // finalizing the updated mesh
        return builder.build();
    }

    private static void identifyBorders(Mesh.Builder builder, int width, int height) {
        Set<Integer> border = new HashSet<>();
        // Finding all points touching the border
        for (int i = 0; i < builder.getPointsCount(); i++) {
            Point p = builder.getPoints(i);
            if (p.getX() <= 0 || p.getX() >= width || p.getY() <= 0 || p.getY() >= height)
                border.add(i);
        }
        // Identifying associated segments
        Set<Integer> frontier = new HashSet<>();
        for (int i = 0; i < builder.getSegmentsCount(); i++) {
            Segment s = builder.getSegments(i);
            if (border.contains(s.getV1Idx()) || border.contains(s.getV2Idx()))
                frontier.add(i);
        }
        // Marking associated polygons in light blue
        for (int i = 0; i < builder.getPolygonsCount(); i++) {
            Polygon p = builder.getPolygons(i);
            Set<Integer> involved = new HashSet<Integer>(frontier);
            involved.retainAll(p.getSegmentIdxList());
            if (involved.size() != 0) {
                Property color = Property.newBuilder().setKey("color").setValue("106:133:157:125").build();
                builder.getPolygonsBuilder(i).addProperties(color);
            }
        }
    }

    private static int findCentralPolygon(Mesh.Builder builder, int width, int height) {
        double idealX = width / 2.0;
        double idealY = height / 2.0;
        double distanceToIdeal = Double.MAX_VALUE;
        int candidate = -1;
        for (int i = 0; i < builder.getPolygonsCount(); i++) {
            Point centroid = builder.getPoints(builder.getPolygons(i).getCentroidIdx());
            double distance = Math.sqrt(
                    ((idealX - centroid.getX()) * (idealX - centroid.getX())) +
                            ((idealY - centroid.getY()) * (idealY - centroid.getY())));
            if (distance < distanceToIdeal) {
                distanceToIdeal = distance;
                candidate = i;
            }
        }
        // Tagging the most central centroid
        Point.Builder center = builder.getPointsBuilder(builder.getPolygons(candidate).getCentroidIdx());
        Property color = Property.newBuilder().setKey("color").setValue("42:78:113").build();
        Property size = Property.newBuilder().setKey("thickness").setValue("3").build();
        center.addProperties(color).addProperties(size);
        return candidate;
    }

    private static void drawPath(Mesh.Builder builder, int start, int length) {
        if(length <= 0)
            return;
        Polygon p = builder.getPolygons(start);
        Random bag = new Random();
        Integer nextCentroid = p.getNeighbors(bag.nextInt(p.getNeighborsCount()));
        Integer nextPolygon = -1;
        for (int i = 0; i < builder.getPolygonsCount(); i++) {
            Polygon candidate = builder.getPolygons(i);
            if(candidate.getCentroidIdx() == nextCentroid) {
                nextPolygon = i;
                break;
            }
        }
        Property color = Property.newBuilder().setKey("color").setValue("169:169:169").build();
        Property size = Property.newBuilder().setKey("thickness").setValue(length+"").build();
        Property style = Property.newBuilder().setKey("style").setValue("dashed").build();
        Segment s = Segment.newBuilder()
                .setV1Idx(p.getCentroidIdx()).setV2Idx(nextCentroid)
                .addProperties(color).addProperties(size).addProperties(style)
                .build();
        builder.addSegments(s);
        drawPath(builder, nextPolygon, length-1);

    }

    private static String readMetadata(Mesh m, String key) {
        Optional<Property> prop = m.getPropertiesList().stream().filter(p -> p.getKey().equals(key)).findFirst();
        if (prop.isPresent()) {
            return prop.get().getValue();
        } else {
            throw new IllegalArgumentException("Missing property [" + key + "]");
        }
    }

}
