import ca.uqam.ace.inf5153.mesh.generator.MeshBuilder;
import ca.uqam.ace.inf5153.mesh.generator.cli.Configuration;
import ca.uqam.ace.inf5153.mesh.generator.cli.CLIParser;


public class Generator {

    public static void main(String[] args) {
        try {
            Configuration config = CLIParser.parse(args);
            MeshBuilder builder = new MeshBuilder(config);
            builder.proceed();
        } catch(Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }

}
