package oss.security.bandaid.core.reader.rules;

import oss.security.bandaid.core.io.rules.reader.RuleReader;
import oss.security.bandaid.core.manipulator.RuleGroup;
import oss.security.bandaid.core.manipulator.Selector;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import oss.security.bandaid.core.manipulator.Rule;

import static org.junit.Assert.*;

/**
 * Created by 0x442E472E on 25.05.2017.
 */
public class AnRuleReader {
    @Test
    public void canReadXmlWithDescriptor() throws IOException {
        RuleReader ruleReader = new RuleReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("oss/security/bandaid/core/reader/rules/rules.xml"));
        List<RuleGroup> ruleGroups = ruleReader.read();
        assertNotNull(ruleGroups);
        assertTrue(ruleGroups.size() > 0);
        RuleGroup group = ruleGroups.get(0);
        assertTrue(group.getRules().size() > 0);
        assertTrue(group.getMetadata() != null);
        assertEquals(group.getMetadata().get("testkey"), "testvalue");
        oss.security.bandaid.core.manipulator.Rule rule = group.getRules().get(0);
        Selector selector = rule.getSelector();
        assertEquals("MethodName", selector.getMethodName());
        assertEquals("Descriptor", selector.getMethodDescriptor());
        assertNotNull(rule.getFix());
        assertEquals("ClassName", selector.getClassName());
    }

//    @Test
//    public void canReadXmlWithoutDescriptor() throws IOException {
//        RuleReader ruleReader = new RuleReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("oss/security/bandaid/core/reader/rules/ruleswithoutdescriptor.xml"));
//        List<Rule> rules = ruleReader.read();
//        assertNotNull(rules);
//        assertTrue(rules.size() > 0);
//        oss.security.bandaid.core.manipulator.Rule rule = rules.get(0);
//        assertEquals("Test", rule.getReason());
//        assertEquals("MethodName", rule.getMethodName());
//        assertEquals("*", rule.getMethodDescriptor());
//        assertNotNull(rule.getFix());
//        assertEquals("ClassName", rule.getFullyQualifiedClassName());
//    }

    @Test
    public void canReadMinimalXml() throws IOException {
        RuleReader ruleReader = new RuleReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("oss/security/bandaid/core/reader/rules/minimalrules.xml"));
        List<RuleGroup> ruleGroups = ruleReader.read();
        assertNotNull(ruleGroups);
        assertTrue(ruleGroups.size() > 0);
        RuleGroup group = ruleGroups.get(0);
        Selector selector = group.getRules().get(0).getSelector();
        assertNull(selector.getMethodDescriptor());
        assertNull(selector.getMethodName());
        assertNotNull(selector.getClassName());
    }

    @Test
    public void cannotReadMalformedXml() throws IOException {
        RuleReader ruleReader = new RuleReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("oss/security/bandaid/core/reader/rules/malformedrules.xml"));
        try {
            List<RuleGroup> ruleGroups = ruleReader.read();
        } catch(RuntimeException e) {
            if(e.getCause() == null || ! (e.getCause() instanceof IllegalArgumentException)) {
                throw e;
            }
        }
    }
}
