package oss.security.bandaid.gradle.plugin

import oss.security.bandaid.gradle.plugin.tasks.ApplyRulesTask
import oss.security.bandaid.gradle.plugin.tasks.GenerateRulesTask
import oss.security.bandaid.gradle.plugin.tasks.ListSelectorsTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by 0x442E472E on 26.05.2017.
 */
class BandaidPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        project.task("createBandaidRules", type: GenerateRulesTask, description: "Generates a Bandaid rules file", group: "bandaid") {
            mustRunAfter 'dependencyCheck'
        }

        project.task("applyBandaidRules", type: ApplyRulesTask, description: "Applies Bandaid rules", group: "bandaid") {
            mustRunAfter 'jar', 'bootRepackage'
        }

        project.task("listBandaidSelectors", type: ListSelectorsTask, description: "Prints a list of selectors for the specified classes", group: "bandaid") {
            dependsOn 'jar', 'bootRepackage'
        }

        project.tasks.findByPath("build").dependsOn "applyBandaidRules"


    }
}
