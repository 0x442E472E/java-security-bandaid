package oss.security.bandaid.core.io.rules.reader;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import oss.security.bandaid.core.io.rules.xml.*;
import oss.security.bandaid.core.io.rules.xml.Fix;
import oss.security.bandaid.core.io.rules.xml.Rule;
import oss.security.bandaid.core.manipulator.*;
import oss.security.bandaid.core.manipulator.behaviours.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by 0x442E472E on 25.05.2017.
 */
public class RuleReader {
    public static final String SELECTOR_SEPARATOR = "->";
    private File xmlFile;
    private InputStream inputStream;
    private RuleGroupFilter filter;

    public RuleReader(File xmlFile) {
        this.xmlFile = xmlFile;
    }

    public RuleReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public List<RuleGroup> read() throws IOException {

        RuleGroups ruleGroups = readXml();

        return convert(ruleGroups);
    }

    public void setFilter(RuleGroupFilter filter) {
        this.filter = filter;
    }

    public RuleGroups readXml() throws IOException {
        String xml = null;
        if(xmlFile != null) {
            xml = FileUtils.readFileToString(xmlFile, Charset.forName("UTF-8"));
        } else if(inputStream != null) {
            xml = IOUtils.toString(inputStream, Charset.forName("UTF-8"));
        }
        if(xml == null) {
            throw new RuntimeException("Could not read XML");
        }
        RuleGroups ruleGroups = readRuleGroups(xml);
        if(ruleGroups.group != null && filter != null) {
            ruleGroups.group = ruleGroups.group.stream().filter(r -> filter.include(r)).collect(Collectors.toList());
        }
        return ruleGroups;
    }

    protected RuleGroups readRuleGroups(String xml) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.readValue(xml, RuleGroups.class);
    }

    public static List<RuleGroup> convert(RuleGroups ruleGroups) {
        List<RuleGroup> result = new ArrayList<>();
        if(ruleGroups == null || ruleGroups.group == null) {
            return result;
        }
        for(Group group: ruleGroups.group) {
            if(group.rules == null) {
                continue;
            }
            Map<String, String> metaData = convertMetaData(group.metadata);
            RuleGroup ruleGroup = new RuleGroup(metaData);
            for(Rule rule: group.rules.rule) {
                ruleGroup.getRules().add(convertRule(rule, ruleGroup));
            }
            result.add(ruleGroup);
        }
        return result;
    }

    private static oss.security.bandaid.core.manipulator.Rule convertRule(Rule rule, RuleGroup ruleGroup) {
        if(rule.selector == null) {
            throw new IllegalArgumentException("No selector specified");
        }
        if(rule.fix == null) {
            throw new IllegalArgumentException("No fix specified");
        }
        try {
            Selector selector = convertSelector(rule.selector);
            oss.security.bandaid.core.manipulator.behaviours.Fix fix = convertFix(rule.fix);
            return new oss.security.bandaid.core.manipulator.Rule(selector, fix, ruleGroup);
        } catch(IllegalArgumentException e) {
            throw new RuntimeException("Could not parse rule", e);
        }
    }

    private static Selector convertSelector(String selector) {
        String[] split = selector.split(Pattern.quote(SELECTOR_SEPARATOR));
        if(split.length == 3) {
            return new Selector(split[0].trim(), split[1].trim(), split[2].trim());
        } else if(split.length == 2) {
            return new Selector(split[0].trim(), split[1].trim());
        } else if(split.length == 1) {
            return new Selector(split[0].trim());
        } else {
            throw new IllegalArgumentException("invalid selector");
        }
    }

    private static oss.security.bandaid.core.manipulator.behaviours.Fix convertFix(Fix fix) {
        if(fix.type == Fix.Type.after) {
            return new StaticCodeFix(fix.text, StaticCodeFix.Entrypoint.After);
        } else if(fix.type == Fix.Type.before){
            return new StaticCodeFix(fix.text, StaticCodeFix.Entrypoint.Before);
        } else {
            throw new IllegalArgumentException("invalid fix");
        }
    }

    private static Map<String, String> convertMetaData(MetaData metaData) {
        Map<String, String> result = new HashMap<>();
        if(metaData == null || metaData.entry == null) {
            return result;
        }
        for(Entry entry: metaData.entry) {
            result.put(entry.key, entry.value);
        }
        return result;
    }

    public static interface RuleGroupFilter {
        public boolean include(Group ruleGroup);
    }
}
