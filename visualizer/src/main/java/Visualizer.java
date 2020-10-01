import ca.uqam.ace.inf5153.mesh.visualizer.MeshVisualizer;
import ca.uqam.ace.inf5153.mesh.visualizer.cli.CLIParser;
import ca.uqam.ace.inf5153.mesh.visualizer.cli.Configuration;


public class Visualizer {

    public static void main(String[] args) {
        try {
            Configuration config = CLIParser.parse(args);
            MeshVisualizer visualizer = new MeshVisualizer(config);
            visualizer.proceed();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("Exiting with error code: 1");
            System.exit(1);
        }
    }

}
