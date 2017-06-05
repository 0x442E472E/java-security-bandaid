package oss.security.bandaid.owasp.dependencycheck.owasp.reader.xmlformat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * Created by 0x442E472E on 25.05.2017.
 */
@JacksonXmlRootElement(localName="analysis")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Analysis {
    public Dependencies dependencies;
}
