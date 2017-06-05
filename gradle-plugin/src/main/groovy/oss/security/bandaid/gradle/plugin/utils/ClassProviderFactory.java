package oss.security.bandaid.gradle.plugin.utils;

import oss.security.bandaid.core.processor.providers.ClassProvider;
import oss.security.bandaid.core.processor.providers.SpringJarFileClassProvider;
import org.gradle.api.Project;

/**
 * Created by 0x442E472E on 04.06.2017.
 */
public class ClassProviderFactory {
    public static ClassProvider obtain(Project project) {
        if(project.getPlugins().hasPlugin("spring-boot") || project.getPlugins().hasPlugin("org.springframework.boot")) {
            return new SpringJarFileClassProvider(true);
        } else {
            throw new RuntimeException("No supported Jar-Plugin could be found.");
        }
    }
}
