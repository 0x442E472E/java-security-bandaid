package oss.security.bandaid.owasp.dependencycheck.processor;


import oss.security.bandaid.core.io.rules.xml.Group;
import oss.security.bandaid.core.io.rules.xml.Rule;
import oss.security.bandaid.core.io.rules.writer.XmlWriter;
import oss.security.bandaid.owasp.dependencycheck.database.reader.CveFilter;
import oss.security.bandaid.owasp.dependencycheck.owasp.writer.SuppressionFileWriter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by 0x442E472E on 26.05.2017.
 *
 * Combines an OWASP dependency check report with supplied rules
 */
public class Processor {
    private final SuppressionFileWriter suppressionWriter;
    private final List<Group> fixes;
    private final List<VulnerableDependency> dependencies;
    private final XmlWriter writer;

    public Processor(List<Group> fixes, List<VulnerableDependency> dependencies, XmlWriter writer, SuppressionFileWriter suppressionWriter) {
        this.fixes = fixes;
        this.dependencies = dependencies;
        this.writer = writer;
        this.suppressionWriter = suppressionWriter;
    }

    public void process() throws IOException {
        Set<String> cves = buildCveSet(dependencies);
        List<Group> fixList = buildFixList(fixes, cves);
        List<VulnerableDependency> fixedDependencies = buildFixableVulnerabilitiesList(fixes, dependencies);
        writer.write(fixList);
        suppressionWriter.write(fixedDependencies);
    }

    protected static List<Group> buildFixList(List<Group> fixes, final Set<String> relevantCves) {
        CveFilter filter = new CveFilter(relevantCves);
        return fixes.stream().filter(filter::include).collect(Collectors.toList());
    }

    protected static Set<String> buildCveSet(List<VulnerableDependency> dependencies) {
        return dependencies.stream().map(VulnerableDependency::getCve).collect(Collectors.toSet());
    }

    protected static List<VulnerableDependency> buildFixableVulnerabilitiesList(List<Group> fixList, List<VulnerableDependency> dependencyList) {
        Set<String> fixed = new HashSet<>();
        fixList.stream()
                .map(group -> group.metadata)
                .filter(metaData -> metaData != null && metaData.entry != null)
                .flatMap(metaData -> metaData.entry.stream())
                .filter(entry -> CveFilter.CVE_KEY.equals(entry.key))
                .forEach(entry -> fixed.add(entry.value));

        List<VulnerableDependency> result = new ArrayList<>();
        result.addAll(dependencyList);
        result.removeIf(d -> !fixed.contains(d.getCve()));

        return result;
    }
}
