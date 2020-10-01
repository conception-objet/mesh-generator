package ca.uqam.ace.inf5153.mesh.generator.cli;

import org.apache.commons.cli.*;

public class CLIParser {

    private static final String DEFAULT_HEIGHT = "1000";
    private static final String DEFAULT_WIDTH = "1000";
    private static final String DEFAULT_OUTPUT = "data.mesh";
    private static final String DEFAULT_POLYGONS = "512";
    private static final String DEFAULT_SMOOTHING = "10";


    public static Configuration parse(String[] args) throws RuntimeException {
        return (new CLIParser()).load(args);
    }

    public Configuration load(String[] args) throws RuntimeException {
        Options opts = configure();
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cl = parser.parse(opts, args);
            if(cl.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("generator", opts);
                System.exit(0);
            }
            return buildConfiguration(cl);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private Options configure() {
        Options opts = new Options();
        opts.addOption(new Option("H", "height", true,"Height of the mesh (in px)" ));
        opts.addOption(new Option("W", "width", true,"Width of the mesh (in px)" ));
        opts.addOption(new Option("o", "output", true,"Output file name"));
        opts.addOption(new Option("P", "polygons", true,"Number of polygons" ));
        opts.addOption(new Option("L", "level", true,"Number of smoothing iterations"));
        opts.addOption(new Option("h", "help", false,"print this message (and exit)"));
        return opts;
    }

    private Configuration buildConfiguration(CommandLine cl) {
        int height = Integer.parseInt(cl.getOptionValue("h", DEFAULT_HEIGHT));
        int width = Integer.parseInt(cl.getOptionValue("w", DEFAULT_WIDTH));
        String output = cl.getOptionValue("o", DEFAULT_OUTPUT);
        int polygons = Integer.parseInt(cl.getOptionValue("p", DEFAULT_POLYGONS));
        int smoothing = Integer.parseInt(cl.getOptionValue("s", DEFAULT_SMOOTHING));
        return new Configuration(height,width,output,polygons,smoothing);
    }

}
