package oss.security.bandaid.owasp.dependencycheck.owasp.writer;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import oss.security.bandaid.owasp.dependencycheck.owasp.writer.xmlformat.FilePath;
import oss.security.bandaid.owasp.dependencycheck.owasp.writer.xmlformat.Suppress;
import oss.security.bandaid.owasp.dependencycheck.owasp.writer.xmlformat.Suppressions;
import oss.security.bandaid.owasp.dependencycheck.processor.VulnerableDependency;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SuppressionFileWriter {
    private File xmlFile;

    public SuppressionFileWriter(File xmlFile) {
        this.xmlFile = xmlFile;
    }

    public void write(List<VulnerableDependency> fixedDependencies) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        xmlMapper.writeValue(xmlFile, convert(fixedDependencies));
    }

    protected Suppressions convert(List<VulnerableDependency> fixedDependencies) {
        List<Suppress> suppresses = new ArrayList<>();
        for(VulnerableDependency fixedDependency: fixedDependencies) {
            Suppress suppress = new Suppress();
            if(isNotNullorEmpty(fixedDependency.getCve())) {
                suppress.cve = fixedDependency.getCve();
            }
            if(isNotNullorEmpty(fixedDependency.getSha1())) {
                suppress.sha1 = fixedDependency.getSha1();
            }
            if(isNotNullorEmpty(fixedDependency.getFilename())) {
                FilePath filePath = new FilePath();
                filePath.regexp = true;
                filePath.text = ".*\\b" + Pattern.quote(fixedDependency.getFilename());
                suppress.filePath = filePath;
            }
            if(isNotNullorEmpty(fixedDependency.getDescription())) {
                suppress.notes = fixedDependency.getDescription();
            }
            suppresses.add(suppress);
        }
        Suppressions result = new Suppressions();
        result.suppress = suppresses;
        return result;
    }

    private static boolean isNotNullorEmpty(String s) {
        return s != null && !s.trim().isEmpty();
    }
}