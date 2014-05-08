package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by savasci on 4/26/2014.
 */
public interface FileIO {

    public InputStream readAsset(String fileName) throws IOException;

    public InputStream readFile(String fileName) throws IOException;

    public OutputStream writeFile(String fileName) throws IOException;

}
