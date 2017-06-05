package oss.security.bandaid.core.io.rules.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;

/**
 * Created by 0x442E472E on 03.06.2017.
 */
public class MetaData {
    @JacksonXmlElementWrapper(useWrapping=false)
    public List<Entry> entry;
}
