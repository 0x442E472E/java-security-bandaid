package oss.security.bandaid.core.io.rules.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

/**
 * Created by 0x442E472E on 03.06.2017.
 */
public class Entry {
    @JacksonXmlProperty(isAttribute = true)
    public String key;
    @JacksonXmlText
    public String value;
}
