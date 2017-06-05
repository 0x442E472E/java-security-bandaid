package oss.security.bandaid.core.io.rules.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

/**
 * Created by 0x442E472E on 03.06.2017.
 */
@JacksonXmlRootElement(localName="rulegroups")
public class RuleGroups {
    @JacksonXmlElementWrapper(useWrapping=false)
    public List<Group> group;
}
