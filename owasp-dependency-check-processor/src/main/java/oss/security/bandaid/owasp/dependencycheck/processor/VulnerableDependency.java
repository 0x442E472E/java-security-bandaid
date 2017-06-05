package oss.security.bandaid.owasp.dependencycheck.processor;

/**
 * Created by 0x442E472E on 25.05.2017.
 */
public class VulnerableDependency {
    private String cve;
    private double score;
    public String cwe;
    public String description;
    public String mavenDependency;
    public String filename;
    public String sha1;

    public VulnerableDependency(String cve, double score) {
        this.cve = cve;
        this.score = score;
    }

    public String getCve() {
        return cve;
    }

    public double getScore() {
        return score;
    }

    public String getCwe() {
        return cwe;
    }

    public void setCwe(String cwe) {
        this.cwe = cwe;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMavenDependency() {
        return mavenDependency;
    }

    public void setMavenDependency(String mavenDependency) {
        this.mavenDependency = mavenDependency;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }
}
