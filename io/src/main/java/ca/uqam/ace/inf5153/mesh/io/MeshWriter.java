package ca.uqam.ace.inf5153.mesh.io;

import ca.uqam.ace.inf5153.mesh.io.Structs.Mesh;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Write a mesh
 */
public class MeshWriter {

    public void writeToFile(Mesh m, String fileName) throws IOException {
        m.writeTo(new FileOutputStream(fileName));
    }

}
