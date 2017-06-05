package oss.security.bandaid.owasp.dependencycheck.processor;

import oss.security.bandaid.core.io.rules.xml.Entry;
import oss.security.bandaid.core.io.rules.xml.Group;
import oss.security.bandaid.core.io.rules.xml.MetaData;
import oss.security.bandaid.owasp.dependencycheck.database.reader.CveFilter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by 0x442E472E on 31.05.2017.
 */
public class AnProcessor {
    @Test
    public void detectsFixedVulnerabilities() {
        Group vul1 = new Group();
        vul1.metadata = new MetaData();
        Entry vul1Entry = new Entry();
        vul1Entry.key = CveFilter.CVE_KEY;
        vul1Entry.value = "vul1";
        vul1.metadata.entry = new ArrayList<>();
        vul1.metadata.entry.add(vul1Entry);

        Group vul2 = new Group();
        vul2.metadata = new MetaData();
        Entry vul2Entry = new Entry();
        vul2Entry.key = CveFilter.CVE_KEY;
        vul2Entry.value = "vul2";
        vul2.metadata.entry = new ArrayList<>();
        vul2.metadata.entry.add(vul2Entry);


        List<Group> fixList = Arrays.asList(
                vul1, vul2
        );
        List<VulnerableDependency> dependencies = Arrays.asList(
                new VulnerableDependency("vul1", 0),
                new VulnerableDependency("vul3", 0)
        );
        List<VulnerableDependency> fixedDependencies = Processor.buildFixableVulnerabilitiesList(fixList, dependencies);

        assertTrue(fixedDependencies.size() == 1);
        assertTrue("vul1".equals(fixedDependencies.get(0).getCve()));
    }
}
