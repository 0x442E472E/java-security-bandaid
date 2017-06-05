package oss.security.bandaid.core.io.rules.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

/**
 * Created by 0x442E472E on 25.05.2017.
 */
public class Rules {
    @JacksonXmlElementWrapper(useWrapping=false)
    public List<Rule> rule;
}
