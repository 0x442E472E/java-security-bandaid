package oss.security.bandaid.owasp.dependencycheck.owasp.reader.xmlformat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by 0x442E472E on 25.05.2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Dependency {

    public Vulnerabilities vulnerabilities;
    public Identifiers identifiers;

    public String fileName;
    public String sha1;
}
