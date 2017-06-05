package oss.security.bandaid.owasp.dependencycheck.owasp.reader;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import oss.security.bandaid.owasp.dependencycheck.owasp.reader.xmlformat.Dependency;
import oss.security.bandaid.owasp.dependencycheck.owasp.reader.xmlformat.Identifier;
import oss.security.bandaid.owasp.dependencycheck.processor.VulnerableDependency;
import oss.security.bandaid.owasp.dependencycheck.owasp.reader.xmlformat.Analysis;
import oss.security.bandaid.owasp.dependencycheck.owasp.reader.xmlformat.Vulnerability;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by 0x442E472E on 25.05.2017.
 *
 * Reads an OWASP dependency check report
 */
public class Reader {
    private File xmlFile;
    private InputStream inputStream;

    private static final String IDENTIFIER_TYPE_MAVEN = "maven";

    public Reader(File xmlFile) {
        this.xmlFile = xmlFile;
    }

    public Reader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public List<VulnerableDependency> read() throws IOException {
        String xml = null;
        if(xmlFile != null) {
            xml = FileUtils.readFileToString(xmlFile, Charset.forName("UTF-8"));
        } else if(inputStream != null) {
            xml = IOUtils.toString(inputStream, Charset.forName("UTF-8"));
        }
        if(xml == null) {
            throw new RuntimeException("Could not read XML");
        }

        Analysis analysis = readAnalysis(xml);
        return convert(analysis);
    }


    protected Analysis readAnalysis(String xml) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.readValue(xml, Analysis.class);
    }

    protected List<VulnerableDependency> convert(Analysis analysis) {
        List<VulnerableDependency> result = new ArrayList<>();
        if(analysis.dependencies == null || analysis.dependencies.dependency == null) {
            return result;
        }
        for(Dependency dependency: analysis.dependencies.dependency) {
            if(dependency.vulnerabilities != null && dependency.vulnerabilities.vulnerability != null) {
                for(Vulnerability vulnerability: dependency.vulnerabilities.vulnerability) {
                    VulnerableDependency vulnerableDependency = new VulnerableDependency(vulnerability.name, vulnerability.cvssScore);
                    vulnerableDependency.setCwe(vulnerability.cwe);
                    vulnerableDependency.setDescription(vulnerability.description);
                    vulnerableDependency.setFilename(dependency.fileName);
                    vulnerableDependency.setSha1(dependency.sha1);

                    if(dependency.identifiers != null && dependency.identifiers.identifier != null) {
                        Optional<Identifier> mavenIdentifier = dependency.identifiers.identifier.stream().filter(identifier -> IDENTIFIER_TYPE_MAVEN.equals(identifier.type)).findAny();
                        if(mavenIdentifier.isPresent() && mavenIdentifier.get().name != null) {
                            vulnerableDependency.setMavenDependency(mavenIdentifier.get().name.replace("(","").replace(")",""));
                        }
                    }
                    result.add(vulnerableDependency);
                }
            }
        }
        return result;
    }


}
