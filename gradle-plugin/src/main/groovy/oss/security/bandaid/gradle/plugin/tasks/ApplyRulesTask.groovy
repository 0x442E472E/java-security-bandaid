package oss.security.bandaid.gradle.plugin.tasks

import oss.security.bandaid.core.io.rules.reader.RuleReader
import oss.security.bandaid.core.manipulator.Rule
import oss.security.bandaid.core.manipulator.RuleGroup
import oss.security.bandaid.core.processor.RuleProcessor
import oss.security.bandaid.core.processor.providers.ClasspathClassProvider
import oss.security.bandaid.core.processor.providers.SpringJarFileClassProvider
import oss.security.bandaid.core.processor.writers.ClassWriter
import oss.security.bandaid.core.processor.writers.FileClassWriter
import oss.security.bandaid.core.processor.writers.JarClassWriter
import oss.security.bandaid.core.processor.writers.SpringJarClassWriter
import oss.security.bandaid.gradle.plugin.utils.ClassProviderFactory
import oss.security.bandaid.gradle.plugin.utils.Utils
import groovy.io.FileType
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * Created by 0x442E472E on 27.05.2017.
 */
class ApplyRulesTask extends DefaultTask{

    @Input
    @Optional
    File[] fixXmlPath

    @TaskAction
    def apply() {
        init()
        applyRules()
    }

    def init() {
        if(fixXmlPath == null) {
            fixXmlPath = Utils.getDefaultFixDestinationPath(project)
        }
    }

    def applyRules() {
        def jarTask = project.tasks.findByPath("jar")
        if(jarTask == null) {
            throw new IllegalStateException("No jar task found. Is the Java plugin applied?")
        }
        File jarFile = jarTask.archivePath
        SpringJarClassWriter classWriter = null
        ClassProviderFactory.obtain(project).withCloseable { classProvider ->
            classProvider.addJarFile(jarFile)
//            project.configurations.compile.each { classProvider.addPath(it.absolutePath)}
//            classProvider.addPath(project.buildDir.absolutePath)
            if(project.getPlugins().hasPlugin("spring-boot") || project.getPlugins().hasPlugin("org.springframework.boot")) {
                classWriter = new SpringJarClassWriter(jarFile)
            } else {
                throw new RuntimeException("No supported Jar-Plugin could be found.")
            }
            List<RuleGroup> ruleGroups = []
            for(def file:fixXmlPath) {
                def ruleReader = new RuleReader(file)
                ruleGroups.addAll(ruleReader.read())
            }

            def ruleProcessor = new RuleProcessor(ruleGroups, classProvider, classWriter)

            ruleProcessor.process()
        }
        if(classWriter != null) {
            classWriter.saveJar()
        }


    }
}
