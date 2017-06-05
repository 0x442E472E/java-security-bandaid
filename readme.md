# Bandaid
## Project Goal
This project aims to support developers with a proper tool set to fix known vulnerabilities of third party libraries in cases where updating said libraries is not feasible or possible.

This project is not meant to be a silver bullet for every vulnerability, but rather a band-aid for when other approaches are not enough. It can also be used to back-port fixes to older versions.

## Modus Operandi
This project consists of two steps that have to be taken in order to let it do its work.
### Analyze Step
A set of rules has to be defined so that this library knows what to do.
You can provide your own rules, but to make things easier, it is possible to select rules based on the result of the OWASP Dependency Check.
The result of this step is one or more XML files with rules and also a XML file for OWASP that can be used to suppress now-fixed vulnerabilities.
### Manipulation Step
Based on the given rules, the specified methods will be adjusted using bytecode manipulation.

## Support
This project currently supports Spring Boot Applications that are build with gradle and use the OWASP Dependency Check.

## Usage
### Gradle
Please refer to the [gradle-plugin](gradle-plugin) documentation.
### Example
An example can be found in [gradle-sample](gradle-sample).
### Rules XML
```xml
<rulegroups>
    <group>
        <metadata>
            <entry key="cve">CVE-2016-7051</entry>
            <entry key="author.name">0x442E472E</entry>
        </metadata>
        <rules>
            <rule>
                <selector>SELECTOR</selector>
                <fix type="before|after">CODE</fix>
            </rule>
        </rules>
    </group>
</rulegroups>
```
A Rule contains code that shall be inserted before or after the original method body. Rules are divided into groups and every group solves a specific problem.
You can attach meta data to a group that can be used to select groups (the cve tag is used by owasp-depenency-check-processor, for example) or at runtime.

#### Selector
The selector can select one or more methods or constructors of a class to which the fix shall be applied. It has to be of the following format:
```
fully.qualified.class.Name[->methodName[->methodDescriptor]]
```

The method descriptor must be according to the java specifications. The gradle-plugin contains a task that prints possible selectors for a class.

Examples:
```
//select every method or constructor of a class:
com.myexample.Application

//selects (one or more) methods with the name doSomething
com.myexample.Application->doSomething

//selects (one or more) constructors
com.myexample.Application->Application

//selects a single method with the name doSomething, no parameters and a void return type
com.myexample.Application->doSomething->()V
```

#### How to write good rules
Ideally you would provide a rule that completely fixes the underlying vulnerability. However, because this is not always possible, please try the following approaches and stop at whichever works:
- Provide code that completely fixes the vulnerability
- If the above is not possible, provide code that detects an attempted exploitation of the vulnerability, and fails safely in those cases (i.e. by throwing an exception)
- If none of the above is possible, fail safely (i.e. by throwing an exception)

#### There are some conventions regarding meta data
| **Key**             | **Contains**                                                                                                                                                |
|---------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------|
| cve                 | The CVE-Key, if available. This is used by owasp-dependency-check-processor to select rules based on which vulnerabilities have been found in your project. |
| resolution.strategy | The resolution strategy that has been used to solve this problem. Must be either fix or fail                                                                |
| author.name         | The name of the author that has created this group, comma-separated if more than one                                                                        |
| author.website      | The website of the author, comma-separated if more than one                                                                                                 |
| detail.website      | A website containing more information about this group or vulnerability                                                                                     |



### Support Library
Your application can provide a custom handler that will be called whenever a modified method is called.
Within this handler, you are able to inspect details of the rule and the affected method. You can override the behaviour supplied by the rule by returning true, or you can simply log the attempt.
```java
Bandaid.addHandler(new BandaidHandlerAdapter() {
    @Override
    public boolean handleStaticMethodEvent(Map<String, String> metadata, Class sender, String methodName, Object... args) {
        System.out.println(String.format("Received call to %s.%s with %d arguments", sender.getName(), methodName, args.length));
        return false;
    }

    @Override
    public boolean handleMethodEvent(Map<String, String> metadata, Object sender, String methodName, Object... args) {
        return false;
    }
});
```
## Modules
| **Module**                                                           | **Description**                                                                                                                           |
|----------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------|
| [core](core)                                                         | Reads rule files and applies them using bytecode manipulation                                                                             |
| [support](owasp-dependency-check-processor)                          | A required dependency for every project that uses Bandaid                                                                                 |
| [owasp-dependency-check-processor](owasp-dependency-check-processor) | Combines an existing dependency-check result with a global rule file and outputs only the relevant rules together with a suppression file |
| [gradle-plugin](gradle-plugin)                                       | Adds a few tasks to make use of core and owasp-dependency-check-processor                                                                 |
| [gradle-sample](gradle-sample)                                       | An example for the gradle plugin                                                                                                          |