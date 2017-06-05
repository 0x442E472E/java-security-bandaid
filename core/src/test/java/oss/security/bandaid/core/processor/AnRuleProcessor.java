package oss.security.bandaid.core.processor;

import oss.security.bandaid.core.manipulator.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by 0x442E472E on 25.05.2017.
 */
public class AnRuleProcessor {

//    @Test
//    public void selectsRuleByClassIfPresent() {
//        List<Rule> rules = new ArrayList<>();
//        rules.add(new Rule("Test", "ClassName", "MethodName", "Descriptor", null));
//        rules.add(new Rule("Test", "ClassName", null));
//        rules.add(new Rule("Test", "ClassName", "MethodName", null));
//
//        Map<String, List<Rule>> sorted = RuleProcessor.groupByClass(rules);
//        List<Rule> selected = sorted.get("ClassName");
//        assertTrue(selected.size() == 1);
//        assertEquals(selected.get(0).getMethodName(), Rule.WILDCARD);
//        assertEquals(selected.get(0).getMethodDescriptor(), Rule.WILDCARD);
//    }
//
//    @Test
//    public void selectsRuleByMethodNameIfPresent() {
//        List<Rule> rules = new ArrayList<>();
//        rules.add(new Rule("Test", "ClassName", "MethodName", "Descriptor", null));
//        rules.add(new Rule("Test", "ClassName", "MethodName", null));
//        rules.add(new Rule("Test", "ClassName", "MethodName", null));
//
//        Map<String, List<Rule>> sorted = RuleProcessor.groupByClass(rules);
//        List<Rule> selected = sorted.get("ClassName");
//        assertTrue(selected.size() == 1);
//        assertEquals(selected.get(0).getMethodDescriptor(), Rule.WILDCARD);
//    }
//
//    @Test
//    public void selectsRuleByMethodDescriptorIfNothingElse() {
//        List<Rule> rules = new ArrayList<>();
//        rules.add(new Rule("Test", "ClassName", "MethodName2", "Descriptor", null));
//        rules.add(new Rule("Test", "ClassName", "MethodName2", null));
//        rules.add(new Rule("Test", "ClassName", "MethodName",  "Descriptor", null));
//
//        Map<String, List<Rule>> sorted = RuleProcessor.groupByClass(rules);
//        List<Rule> selected = sorted.get("ClassName");
//        assertTrue(selected.size() == 2);
//        assertTrue(selected.stream().anyMatch(r -> r.getMethodName().equals("MethodName2") && Rule.WILDCARD.equals(r.getMethodDescriptor())));
//        assertTrue(selected.stream().anyMatch(r -> r.getMethodName().equals("MethodName") && !Rule.WILDCARD.equals(r.getMethodDescriptor())));
//    }
}
