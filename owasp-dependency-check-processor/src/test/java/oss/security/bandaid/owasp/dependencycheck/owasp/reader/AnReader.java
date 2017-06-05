package oss.security.bandaid.owasp.dependencycheck.owasp.reader;

import oss.security.bandaid.owasp.dependencycheck.owasp.reader.Reader;
import oss.security.bandaid.owasp.dependencycheck.processor.VulnerableDependency;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by 0x442E472E on 25.05.2017.
 */
public class AnReader {

    @Test
    public void canReadReport() throws IOException {
        Reader reader = new Reader(Thread.currentThread().getContextClassLoader().getResourceAsStream("oss/security/bandaid/owasp/dependencycheck/owaspreader/dependency-check-report.xml"));
        List<VulnerableDependency> vulnerableDependencyList = reader.read();
        assertTrue(vulnerableDependencyList != null);
        assertTrue(vulnerableDependencyList.size() > 0);
        assertTrue(vulnerableDependencyList.stream().anyMatch(vulnerableDependency ->
                isNotNullorEmpty(vulnerableDependency.getCve()) &&
                vulnerableDependency.getScore() > 0 &&
                isNotNullorEmpty(vulnerableDependency.getCwe()) &&
                isNotNullorEmpty(vulnerableDependency.getDescription()) &&
                isNotNullorEmpty(vulnerableDependency.getFilename()) &&
                isNotNullorEmpty(vulnerableDependency.getMavenDependency()) &&
                isNotNullorEmpty(vulnerableDependency.getSha1())
        ));
    }

    private static boolean isNotNullorEmpty(String s) {
        return s != null && !s.trim().isEmpty();
    }
}
