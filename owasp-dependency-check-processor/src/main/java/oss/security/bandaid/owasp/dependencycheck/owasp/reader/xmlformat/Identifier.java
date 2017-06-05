package oss.security.bandaid.owasp.dependencycheck.owasp.reader.xmlformat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Created by 0x442E472E on 25.05.2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Identifier {
    @JacksonXmlProperty(isAttribute = true)
    public String type;

    @JacksonXmlProperty
    public String name;
}
