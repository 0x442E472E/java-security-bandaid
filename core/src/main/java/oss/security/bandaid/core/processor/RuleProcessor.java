package oss.security.bandaid.core.processor;

import oss.security.bandaid.core.manipulator.Rule;
import oss.security.bandaid.core.manipulator.RuleGroup;
import oss.security.bandaid.core.processor.providers.ClassProvider;
import oss.security.bandaid.core.processor.writers.ClassWriter;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by 0x442E472E on 25.05.2017.
 */
public class RuleProcessor {
    private static final Logger logger = Logger.getLogger(RuleProcessor.class.getName());
    private List<RuleGroup> ruleGroups;
    private ClassProvider classProvider;
    private ClassWriter classWriter;

    public RuleProcessor(List<RuleGroup> ruleGroups, ClassProvider classProvider, ClassWriter classWriter) {
        this.ruleGroups = ruleGroups;
        this.classProvider = classProvider;
        this.classWriter = classWriter;
    }

    public void process() throws Rule.RuleException, NotFoundException, CannotCompileException, IOException {
        Map<String, List<Rule>> classMap = groupByClass(ruleGroups);

        for(Map.Entry<String, List<Rule>> entry: classMap.entrySet()) {
            logger.log(Level.FINE, "Modifying class {0}", entry.getKey());
            CtClass ctClass = classProvider.getClass(entry.getKey());
            for(Rule rule: entry.getValue()) {
                logger.log(Level.FINER, "Applying Rule: {0}", rule);
                rule.apply(ctClass);
            }
            classWriter.write(ctClass);
        }
    }

    protected static Map<String, List<Rule>> groupByClass(List<RuleGroup> ruleGroups) {
        return ruleGroups.stream()
                .flatMap(r -> r.getRules().stream())
                .collect(Collectors.groupingBy(r -> r.getSelector().getClassName()));
//        return classMap.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> selectRules(e.getValue())));
    }

    /**
     * Selects rules in a way to achieve the best coverage of the class
     * @param rules
     * @return
     */
//    protected static List<Rule> selectRules(List<Rule> rules) {
//        //use a rule that covers everything, if present
//        Optional<Rule> coverageByClass = rules.stream().filter(r -> Rule.WILDCARD.equals(r.getMethodName()) && Rule.WILDCARD.equals(r.getMethodDescriptor())).findAny();
//        if(coverageByClass.isPresent()) {
//            return Arrays.asList(coverageByClass.get());
//        }
//
//        //select rules that cover distinct method names (dropping rules if there is more than one per method name)
//        Map<String, Rule> coverageByMethodName = rules.stream()
//                .filter(r -> !Rule.WILDCARD.equals(r.getMethodName()) && Rule.WILDCARD.equals(r.getMethodDescriptor()))
//                .collect(Collectors.toMap(Rule::getMethodName, r -> r, (r1, r2) -> r1));
//
//        //select rules that are specific to a method name and signature
//        Map<String, Rule> coverageByMethodDescriptor = rules.stream()
//                .filter(r -> !Rule.WILDCARD.equals(r.getMethodName()) && !Rule.WILDCARD.equals(r.getMethodDescriptor()))
//                .collect(Collectors.toMap(r -> r.getMethodName() + r.getMethodDescriptor(), r -> r, (r1, r2) -> r1));
//
//        //merge rules, so that broader rules have precedence
//        for(Map.Entry<String, Rule> entry: coverageByMethodDescriptor.entrySet()) {
//            coverageByMethodName.putIfAbsent(entry.getValue().getMethodName(), entry.getValue());
//        }
//
//        return new ArrayList<>(coverageByMethodName.values());
//    }
}
