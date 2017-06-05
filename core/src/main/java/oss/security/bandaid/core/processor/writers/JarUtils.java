package oss.security.bandaid.core.processor.writers;

import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.CRC32;
import java.util.zip.ZipOutputStream;

public class JarUtils {
    private static final Logger logger = Logger.getLogger(JarClassWriter.class.getName());

    /**
     * Adds the specified entries to an existing jar file but at a new location
     * @param jar existing jar
     * @param saveEntries entries to save
     * @return the new file
     * @throws IOException
     */
    public static File saveJarFile(JarFile jar, List<SaveEntry> saveEntries) throws IOException {
        Set<String> filenames = saveEntries.stream().map(SaveEntry::getFilename).collect(Collectors.toSet());
        File target = File.createTempFile("TempJar",".jar");
        try(JarOutputStream tempJar =
                    new JarOutputStream(new FileOutputStream(target))) {

            //add changed files
            CRC32 checksumCalculator = new CRC32();
            for(SaveEntry saveEntry: saveEntries) {
                logger.log(Level.FINE, "saving "+saveEntry.getFilename());
                JarEntry entry = new JarEntry(saveEntry.getFilename());
                if(!saveEntry.isUseCompression()) {
                    checksumCalculator.reset();
                    checksumCalculator.update(saveEntry.getBytes());
                    entry.setCrc(checksumCalculator.getValue());
                    entry.setMethod(JarEntry.STORED);
                    entry.setSize(saveEntry.bytes.length);
                    entry.setCompressedSize(saveEntry.bytes.length);
                }

                tempJar.putNextEntry(entry);
                tempJar.write(saveEntry.getBytes());
            }


            // Loop through the jar entries and add them to the temp jar,
            // skipping the entry that was added to the temp jar already.
            byte[] buffer = new byte[1024];
            int bytesRead;
            for (Enumeration entries = jar.entries(); entries.hasMoreElements(); ) {

                JarEntry entry = (JarEntry) entries.nextElement();

                // If the entry has not been added already, so add it.

                if (! filenames.contains(entry.getName())) {
                    try(InputStream entryStream = jar.getInputStream(entry)) {
                        tempJar.putNextEntry(entry);

                        while ((bytesRead = entryStream.read(buffer)) != -1) {
                            tempJar.write(buffer, 0, bytesRead);
                        }

                        tempJar.closeEntry();
                    }
                } else {
                    filenames.remove(entry.getName());
                    logger.log(Level.FINE, "prevented overwriting of manipulated file: "+entry.getName());
                }
            }
        }

        if(filenames.size() > 0) {
            throw new IllegalStateException("The following files have been added instead of replaced: "+String.join(", ", filenames));
        }
        return target;
    }


    protected static class SaveEntry {
        private byte[] bytes;
        private String filename;
        private boolean useCompression;

        public SaveEntry(byte[] bytes, String filename) {
            this.bytes = bytes;
            this.filename = filename;
            useCompression = true;
        }

        public SaveEntry(byte[] bytes, String filename, boolean useCompression) {
            this.bytes = bytes;
            this.filename = filename;
            this.useCompression = useCompression;
        }

        public byte[] getBytes() {
            return bytes;
        }

        public String getFilename() {
            return filename;
        }

        public boolean isUseCompression() {
            return useCompression;
        }
    }
}
