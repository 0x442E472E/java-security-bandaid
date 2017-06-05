package oss.security.bandaid.owasp.dependencycheck.owasp.writer;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import oss.security.bandaid.owasp.dependencycheck.owasp.writer.xmlformat.Suppressions;
import oss.security.bandaid.owasp.dependencycheck.processor.VulnerableDependency;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by 0x442E472E on 01.06.2017.
 */
public class AnSuppressionFileWriter {

    private Random random = new Random();

    @Test
    public void writesASuppressionFile() throws IOException {
        File xmlFile = File.createTempFile("SuppressionFile",".xml");
        xmlFile.deleteOnExit();
        List<VulnerableDependency> fixedDependencies = Arrays.asList(
                create("file"+random.nextInt()),
                create("file"+random.nextInt()),
                create("file"+random.nextInt())
        );
        SuppressionFileWriter writer = new SuppressionFileWriter(xmlFile);
        writer.write(fixedDependencies);
        assertResult(xmlFile);
    }

    private void assertResult(File xmlFile) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        Suppressions suppressions = xmlMapper.readValue(xmlFile, Suppressions.class);
        assertNotNull(suppressions);
        assertNotNull(suppressions.suppress);
        assertTrue(suppressions.suppress.size() > 0);
        assertTrue(suppressions.suppress.stream().anyMatch(s ->
                s.filePath != null &&
                isNotNullOrEmpty(s.filePath.text) &&
                isNotNullOrEmpty(s.cve) &&
                isNotNullOrEmpty(s.notes) &&
                isNotNullOrEmpty(s.sha1)
        ));
    }

    private boolean isNotNullOrEmpty(String s) {
        return s != null && !s.isEmpty();
    }

    private VulnerableDependency create(String filename) {
        VulnerableDependency result = new VulnerableDependency("cve"+random.nextInt(), random.nextDouble());
        result.setSha1("sha1" + random.nextInt());
        result.setMavenDependency("mvn" + random.nextInt());
        result.setFilename(filename);
        result.setDescription("desc" + random.nextInt());
        result.setCwe("cwe" + random.nextInt());
        return result;
    }


}
