package oss.security.bandaid.core.manipulator.behaviours;

import oss.security.bandaid.core.manipulator.behaviours.Fix;
import oss.security.bandaid.support.Bandaid;
import javassist.*;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 0x442E472E on 25.05.2017.
 */
public class StaticCodeFix implements Fix {
    public enum Entrypoint {Before, After, Replace}
    private String code;
    private Entrypoint entrypoint;

    public StaticCodeFix(String code, Entrypoint entrypoint) {
        this.code = code;
        this.entrypoint = entrypoint;
    }

    @Override
    public void apply(CtBehavior ctBehavior, Map<String, String> metadata) throws FixException {
        try {
            final String code = prepareCode(ctBehavior, metadata);
            if(entrypoint == Entrypoint.Before) {
                ctBehavior.insertBefore(code);
            } else if(entrypoint == Entrypoint.After){
                ctBehavior.insertAfter(code);
            } else if(entrypoint == Entrypoint.Replace) {
                ctBehavior.setBody(code);
            }
        } catch (CannotCompileException e) {
            throw new FixException("Could not compile Code", e);
        }
    }

    private String prepareCode(CtBehavior ctBehavior, Map<String, String> metadata) {
        String handlerCall;
        if(Modifier.isStatic(ctBehavior.getModifiers())) {
            handlerCall = String.format("%s.newStaticMethodEvent(bandaidMetadataMap, $class, \"%s\", $args)", Bandaid.class.getName(), ctBehavior.getName());
        } else {
            handlerCall = String.format("%s.newMethodEvent(bandaidMetadataMap, $0, \"%s\", $args)", Bandaid.class.getName(), ctBehavior.getName());
        }
        String metaDataCode = buildMetadataMapCode(metadata);
        return String.format("{%s if(!%s) {%s}}", metaDataCode, handlerCall, code);
    }
    private String buildMetadataMapCode(Map<String, String> metadata) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s bandaidMetadataMap = new %s();", HashMap.class.getName(), HashMap.class.getName()));
        for(Map.Entry<String, String> entry: metadata.entrySet()) {
            builder.append(String.format("bandaidMetadataMap.put(\"%s\", \"%s\");", StringEscapeUtils.escapeJava(entry.getKey()), StringEscapeUtils.escapeJava(entry.getValue())));
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return "StaticCodeFix{" +
                "entrypoint=" + entrypoint +
                '}';
    }
}
