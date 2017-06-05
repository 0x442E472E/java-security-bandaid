package oss.security.bandaid.owasp.dependencycheck.owasp.writer.xmlformat;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Created by 0x442E472E on 31.05.2017.
 */
public class Suppress {
    @JacksonXmlProperty
    public String notes;

    @JacksonXmlProperty
    public String cve;

    public FilePath filePath;

    @JacksonXmlProperty
    public String sha1;
}
