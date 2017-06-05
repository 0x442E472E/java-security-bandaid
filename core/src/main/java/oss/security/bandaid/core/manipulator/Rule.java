package oss.security.bandaid.core.manipulator;

import oss.security.bandaid.core.manipulator.behaviours.Fix;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by 0x442E472E on 25.05.2017.
 *
 * Applies a fix to every Method that satisfies the selector
 */
public class Rule {
    private static final Logger logger = Logger.getLogger(Rule.class.getName());

    private Selector selector;
    private Fix fix;
    private RuleGroup ruleGroup;

    public Rule(Selector selector, Fix fix, RuleGroup ruleGroup) {
        this.selector = selector;
        this.fix = fix;
        this.ruleGroup = ruleGroup;
    }

    public Selector getSelector() {
        return selector;
    }

    public void apply(CtClass clazz) throws RuleException {
        try {
            List<CtBehavior> behaviors = selector.selectFromClass(clazz);
            if(behaviors == null || behaviors.size() == 0) {
                throw new RuleException("Could not apply to "+selector+ "because no matching methods could be found");
            }
            for(CtBehavior behavior: behaviors) {
                apply(behavior);
            }
        } catch (NotFoundException e) {
            throw new RuleException("Could not apply Rule", e);
        }
    }

    protected void apply(CtBehavior ctBehavior) throws RuleException {
        try {
            if(!ctBehavior.getDeclaringClass().getName().equals(Object.class.getName())) {
                fix.apply(ctBehavior, ruleGroup.getMetadata());
            }

        } catch (Fix.FixException e) {
            throw new RuleException("Could not apply fix to method "+ctBehavior.getLongName(), e);
        }
    }

    @Override
    public String toString() {
        return "Rule{" +
                "selector=" + selector +
                ", fix=" + fix +
                '}';
    }

    public Fix getFix() {
        return fix;
    }

    public static class RuleException extends Exception {
        public RuleException() {
        }

        public RuleException(String message) {
            super(message);
        }

        public RuleException(String message, Throwable cause) {
            super(message, cause);
        }

        public RuleException(Throwable cause) {
            super(cause);
        }
    }
}
