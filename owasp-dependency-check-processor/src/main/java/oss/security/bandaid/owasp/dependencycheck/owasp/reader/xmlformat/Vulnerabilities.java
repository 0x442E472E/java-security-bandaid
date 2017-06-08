package oss.security.bandaid.owasp.dependencycheck.owasp.reader.xmlformat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;

/**
 * Created by 0x442E472E on 26.05.2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Vulnerabilities {
    @JacksonXmlElementWrapper(useWrapping=false)
    public List<Vulnerability> vulnerability;
}
