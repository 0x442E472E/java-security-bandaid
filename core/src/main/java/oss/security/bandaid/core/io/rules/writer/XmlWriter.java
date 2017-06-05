package oss.security.bandaid.core.io.rules.writer;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import oss.security.bandaid.core.io.rules.xml.Group;
import oss.security.bandaid.core.io.rules.xml.Rule;
import oss.security.bandaid.core.io.rules.xml.RuleGroups;
import oss.security.bandaid.core.io.rules.xml.Rules;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by 0x442E472E on 26.05.2017.
 */
public class XmlWriter {
    private File xmlFile;

    public XmlWriter(File xmlFile) {
        this.xmlFile = xmlFile;
    }

    public void write(List<Group> ruleGroupsList) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        RuleGroups ruleGroups = new RuleGroups();
        ruleGroups.group = ruleGroupsList;
        xmlMapper.writeValue(xmlFile, ruleGroups);
    }
}
