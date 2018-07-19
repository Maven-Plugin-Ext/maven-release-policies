[![Build Status](https://travis-ci.org/Maven-Plugin-Ext/maven-release-policies?branch=master)](https://travis-ci.orgMaven-Plugin-Ext/maven-release-policies)

Maven Release Plugin Policies
---

This plugin uses the run time arguments for the versioning of an artifact.

## Use Cases:

Maven currently doesn't allow the variables in artifact version and maven release plugin has limited options when it comes to versioning of released artifacts.

This plugin helps in setting up the released versions at runtime using run time variables. You can add prefix, suffix, build number etc in released version without doing any changes in project.


You can set the version of an artifact in following format:
**digits-annotation-annotationRevision-buildSpecifier**

- component is the name of the component that is being released
- digits represents  MajorVersion.MinorVersion.IncrementalVersion number
- annotationRevision is Integer qualifier for the annotation. (4 as in RC-4)
- buildSpecifier is additional specifier for build. (SNAPSHOT, or build number like "20041114.081234-2")

All you have to do is pass run time arguments to release:preare command:

Example Command:
***mvn release:prepare -DannotationRevSeparator=. -Dannotation=java -DannotationRevision=3 -DbuildSeperator=- -DbuildSpecifier=SNAPSHOT -DdryRun=true***

## Please refer below table for various combinations:

digits | annotationSeparator  | annotation | annotationRevSeperator | annotationRevision | buildSeperator | buildSpecifier | Released Version | SNAPSHOT Version
------------- | ------------- | ------------- | -------------| -------------| -------------| ------------- | -------------| -------------
1.0.0 | -  | java | - | 7 | - | SNAPSHOT | 1.0.0-java-7 | 1.0.0-java-8-SNAPSHOT
1.0.0 | -  | RC | - | 4 | - | SNAPSHOT | 1.0.0-RC-4 | 1.0.0-RC-5-SNAPSHOT
1.2.9 |		|	|	|	|	|	|1.2.9 | 1.2.10-SNAPSHOT


## Steps to use this plugin in a maven project:

1. Add following in plugins section

```xml
<plugin>
				<artifactId>maven-release-plugin</artifactId>
				<version>2.5.3</version>
				<configuration>
					<projectVersionPolicyId>ConfigurableVersionPolicy</projectVersionPolicyId>
				</configuration>
				<dependencies>
					<dependency>
						<groupId>com.extensions</groupId>
						<artifactId>maven-release-policies</artifactId>
						<version>1.0-SNAPSHOT</version>
					</dependency>
				</dependencies>
			</plugin>
```
2. Pass run time arugments during maven release:prepare

Sample:

mvn release:prepare -DannotationRevSeparator=. -Dannotation=raptor -DannotationRevision=3 -DbuildSeperator=- -DbuildSpecifier=SNAPSHOT -DdryRun=true
