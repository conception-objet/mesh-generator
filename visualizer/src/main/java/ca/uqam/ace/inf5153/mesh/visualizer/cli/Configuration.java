package ca.uqam.ace.inf5153.mesh.visualizer.cli;

public class Configuration {

    private final String inputFile;
    private final String outputFile;
    private final boolean debug;
    private final String type;

    public Configuration(String inputFile, String outputFile, boolean debug, String type) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.debug = debug;
        this.type = type.toUpperCase();
    }

    public String getInputFile() {
        return inputFile;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public boolean isDebug() {
        return debug;
    }

    public String getType() {
        return type;
    }
}
