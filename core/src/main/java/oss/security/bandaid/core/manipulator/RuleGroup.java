package oss.security.bandaid.core.manipulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 0x442E472E on 03.06.2017.
 *
 * Groups multiple Rules and assigns meta data to them
 */
public class RuleGroup {
    private Map<String, String> metadata;
    private List<Rule> rules = new ArrayList<>();

    public RuleGroup(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public List<Rule> getRules() {
        return rules;
    }
}
