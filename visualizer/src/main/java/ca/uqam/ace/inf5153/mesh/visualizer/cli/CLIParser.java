package ca.uqam.ace.inf5153.mesh.visualizer.cli;

import org.apache.commons.cli.*;

public class CLIParser {

    private static final String DEFAULT_INPUT_MESH = "data.mesh";
    private static final String DEFAULT_OUTPUT_FILE = "output.svg";
    private static final String DEFAULT_TYPE = "svg";

    public static Configuration parse(String[] args) throws RuntimeException {
        return (new CLIParser()).load(args);
    }

    private Configuration load(String[] args) {
        Options opts = configure();
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cl = parser.parse(opts, args);
            if(cl.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("visualizer", opts);
                System.exit(0);
            }
            return buildConfiguration(cl);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private Configuration buildConfiguration(CommandLine cl) {
        String input = cl.getOptionValue("i", DEFAULT_INPUT_MESH);
        String output = cl.getOptionValue("o", DEFAULT_OUTPUT_FILE);
        boolean debug = cl.hasOption("d");
        String type = cl.getOptionValue("t", DEFAULT_TYPE);
        return new Configuration(input, output, debug, type);
    }

    private Options configure() {
        Options opts = new Options();
        opts.addOption(new Option("i", "input", true,"Input mesh" ));
        opts.addOption(new Option("o", "output", true,"output file" ));
        opts.addOption(new Option("t", "type", true,"Output type (in svg, pdf)"));
        opts.addOption(new Option("d", "debug", false,"Debug mode"));
        opts.addOption(new Option("h", "help", false,"print this message (and exit)"));
        return opts;
    }
}
