package oss.security.bandaid.owasp.dependencycheck.owasp.writer.xmlformat;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

/**
 * Created by 0x442E472E on 31.05.2017.
 */
@JacksonXmlRootElement(localName = "suppressions")
public class Suppressions {
    public List<Suppress> suppress;
}
