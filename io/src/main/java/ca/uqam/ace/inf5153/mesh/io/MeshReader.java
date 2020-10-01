package ca.uqam.ace.inf5153.mesh.io;

import ca.uqam.ace.inf5153.mesh.io.Structs.Mesh;

import java.io.FileInputStream;
import java.io.IOException;

public class MeshReader {

    public Mesh readFromFile(String fileName) throws IOException {
        return Mesh.parseFrom(new FileInputStream(fileName));
    }

}
