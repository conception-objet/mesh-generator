package ca.uqam.ace.inf5153.mesh.generator.cli;

public class Configuration {

    private final int height;
    private final int width;
    private final String output;
    private final int polygons;
    private final int smoothing;

    public Configuration(int height, int width, String output, int polygons, int smoothing) {
        this.height = height;
        this.width = width;
        this.output = output;
        this.polygons = polygons;
        this.smoothing = smoothing;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public String getOutput() {
        return output;
    }

    public int getPolygons() {
        return polygons;
    }

    public int getSmoothing() {
        return smoothing;
    }
}
