package oss.security.bandaid.owasp.dependencycheck.owasp.writer.xmlformat;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

/**
 * Created by 0x442E472E on 31.05.2017.
 */
public class FilePath {
    @JacksonXmlProperty(isAttribute = true)
    public boolean regexp;

    @JacksonXmlText
    public String text;
}
