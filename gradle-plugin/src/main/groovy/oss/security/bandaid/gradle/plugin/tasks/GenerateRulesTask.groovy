package oss.security.bandaid.gradle.plugin.tasks

import oss.security.bandaid.core.io.rules.reader.RuleReader
import oss.security.bandaid.core.io.rules.writer.XmlWriter
import oss.security.bandaid.core.io.rules.xml.Group
import oss.security.bandaid.gradle.plugin.utils.Utils
import oss.security.bandaid.owasp.dependencycheck.owasp.writer.SuppressionFileWriter
import oss.security.bandaid.owasp.dependencycheck.processor.Processor
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * Created by 0x442E472E on 26.05.2017.
 */
class GenerateRulesTask  extends DefaultTask {

    @Input
    File owaspXmlPath
    @Input
    File[] fixDatabasePath
    @Input
    @Optional
    File fixDestinationPath
    @Input
    @Optional
    File suppressionDestinationPath


    @TaskAction
    def generate() {
        init()
        createRulesFile()
    }

    def init() {
        if(fixDestinationPath == null) {
            fixDestinationPath = Utils.getDefaultFixDestinationPath(project)
        }
        fixDestinationPath.parentFile.mkdirs()

        if(suppressionDestinationPath == null) {
            suppressionDestinationPath = Utils.getDefaultSuppressionDestinationPath(project)
        }
        suppressionDestinationPath.parentFile.mkdirs()
    }

    def createRulesFile() {


        def owaspReader = new oss.security.bandaid.owasp.dependencycheck.owasp.reader.Reader(owaspXmlPath)
        def xmlWriter = new XmlWriter(fixDestinationPath)
        def suppressionWriter = new SuppressionFileWriter(suppressionDestinationPath)

        List<Group> fixes = []
        for(def file: fixDatabasePath) {
            def databaseReader = new RuleReader(file)
            def ruleGroups = databaseReader.readXml()
            if(ruleGroups != null && ruleGroups.group != null) {
                fixes.addAll(ruleGroups.group)
            }
        }
        def vulnerabilities = owaspReader.read()

        def processor = new Processor(fixes, vulnerabilities, xmlWriter, suppressionWriter)

        processor.process()
    }

}
