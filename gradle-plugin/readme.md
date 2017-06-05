# gradle-plugin
## Usage
```groovy
//apply plugin
apply plugin: 'oss.security.bandaid'

buildscript {

    //add a dependency to the plugin artifact so it can be found
    dependencies {
        classpath 'oss.security.bandaid:gradle-plugin:0.1-SNAPSHOT'
    }

}

//Guide the createBandaidRules-Task to your dependency check report and (one or more) databases containing rules for found vulnerabilities
createBandaidRules.owaspXmlPath = new File(buildDir,"/reports/dependency-check-report.xml")
createBandaidRules.fixDatabasePath = [new File(projectDir, "fix-db.xml")]
//Guide the applyBandaidRules-Task to (one or more) XML files containing your rules
applyBandaidRules.fixXmlPath = [new File(projectDir, "bandaid-custom-rules.xml"), new File(projectDir, "bandaid-rules.xml")]

//Add a dependency to the support library
dependencies {
    compile group: 'oss.security.bandaid', name: 'support', version: '0.1-SNAPSHOT'
}
```
## Print selectors
The task listBandaidSelectors will output possible selectors for given classes. You can specify the classes with the property bandaid.listclasses (comma-separated).
Example:
```
gradlew example:listBandaidSelectors -Pbandaid.listclasses=com.example.Main,com.example.Util
```
## Why is only Spring Boot supported currently?
The plugin needs to know where it can find the classes belonging to your project or third party dependencies.
While loading these classes is no problem, writing is, because we don't want to poison your local dependency cache. Because of this, this plugin needs to know how you distribute your dependencies and adapt to that. This has only been done for Spring Boot so far.