package ca.uqam.ace.inf5153.mesh.visualizer.converters;

import ca.uqam.ace.inf5153.mesh.io.Structs;
import ca.uqam.ace.inf5153.mesh.visualizer.drawers.SVGDrawer;
import org.apache.batik.apps.rasterizer.DestinationType;
import org.apache.batik.apps.rasterizer.SVGConverter;
import org.apache.batik.apps.rasterizer.SVGConverterException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public abstract class AbstractConverter {
    
    public void render(SVGDrawer drawer, Structs.Mesh mesh, String output) {
        try {
            File tmp = File.createTempFile("visu", "svg");
            drawer.run(mesh,tmp.getAbsolutePath());
            SVGConverter converter = new SVGConverter();
            converter.setDestinationType(getOutputDestinationType());
            converter.setSources(new String[] { tmp.getAbsolutePath() });
            converter.setDst(new File(output));
            silentConvert(converter);
        } catch (IOException | SVGConverterException e) {
            throw new RuntimeException(e);
        }
    }

    private void silentConvert(SVGConverter converter) throws SVGConverterException {
        PrintStream original = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        converter.execute();
        System.setOut(original);
    }

    protected abstract DestinationType getOutputDestinationType();

}
