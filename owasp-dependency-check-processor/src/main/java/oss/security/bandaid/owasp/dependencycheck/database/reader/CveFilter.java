package oss.security.bandaid.owasp.dependencycheck.database.reader;

import oss.security.bandaid.core.io.rules.reader.RuleReader;
import oss.security.bandaid.core.io.rules.xml.Group;

import java.util.Set;

/**
 * Created by 0x442E472E on 04.06.2017.
 */
public class CveFilter implements RuleReader.RuleGroupFilter {
    public final static String CVE_KEY = "cve";
    private Set<String> cves;

    public CveFilter(Set<String> cves) {
        this.cves = cves;
    }

    @Override
    public boolean include(Group ruleGroup) {
        if(ruleGroup.metadata == null || ruleGroup.metadata.entry == null || ruleGroup.metadata.entry.size() == 0) {
            return false;
        } else {
            return ruleGroup.metadata.entry.stream().anyMatch(e -> CVE_KEY.equals(e.key) && cves.contains(e.value));
        }

    }
}
