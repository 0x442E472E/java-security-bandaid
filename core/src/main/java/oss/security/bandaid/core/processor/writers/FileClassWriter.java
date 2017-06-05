package oss.security.bandaid.core.processor.writers;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.IOException;

/**
 * Created by 0x442E472E on 25.05.2017.
 *
 * Writes classes to simple .class files
 */
public class FileClassWriter implements  ClassWriter {
    private String directory;

    public FileClassWriter(String directory) {
        this.directory = directory;
    }

    public FileClassWriter() {
    }

    @Override
    public void write(CtClass ctClass) throws CannotCompileException, IOException {
        if(directory != null) {
            ctClass.writeFile(directory);
        } else {
            try {
                ctClass.writeFile();
            } catch (NotFoundException e) {
                throw new IOException("Could not find class", e);
            }
        }
    }

    @Override
    public void close() throws IOException {

    }
}
