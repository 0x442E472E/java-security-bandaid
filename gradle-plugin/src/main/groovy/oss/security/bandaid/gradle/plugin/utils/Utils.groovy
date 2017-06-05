package oss.security.bandaid.gradle.plugin.utils

import org.gradle.api.Project

/**
 * Created by 0x442E472E on 27.05.2017.
 */
class Utils {
    static File getDefaultFixDestinationPath(Project project) {
        return new File(new File(project.buildDir, "bandaid"), "bandaid-rules.xml")
    }

    static File getDefaultSuppressionDestinationPath(Project project) {
        return new File(new File(project.buildDir, "bandaid"), "owasp-suppressions.xml")
    }
}
