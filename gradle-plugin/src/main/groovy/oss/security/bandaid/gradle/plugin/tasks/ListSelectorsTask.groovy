package oss.security.bandaid.gradle.plugin.tasks

import oss.security.bandaid.core.processor.writers.SpringJarClassWriter
import oss.security.bandaid.core.util.SelectorLister
import oss.security.bandaid.gradle.plugin.utils.ClassProviderFactory
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Created by 0x442E472E on 04.06.2017.
 */
class ListSelectorsTask extends DefaultTask {

    @TaskAction
    def print() {
        def jarTask = project.tasks.findByPath("jar")
        if(jarTask == null) {
            throw new IllegalStateException("No jar task found. Is the Java plugin applied?")
        }
        File jarFile = jarTask.archivePath
        ClassProviderFactory.obtain(project).withCloseable { classProvider ->
            classProvider.addJarFile(jarFile)
            def selectorListener = new SelectorLister(readClassNames(), classProvider)
            def foundClasses = selectorListener.detect()
            for(def entry: foundClasses.entrySet()) {
                println "----- " + entry.key
                for(def method: entry.value) {
                    println method.name + ": " +method.selector
                }
            }
        }
    }

    Set<String> readClassNames() {
        if(project.hasProperty("bandaid.listclasses")) {
            String listClasses = project.property("bandaid.listclasses")
            return new HashSet<String>(Arrays.asList(listClasses.split(",")))
        } else {
            throw new IllegalStateException("No classes have been specified. You should assign a comma separated list of classes to the property bandaid.listclasses. Example: -Pbandaid.listclasses=com.example.Main,com.example.Util")
        }
    }
}
