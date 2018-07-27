[![Build Status](https://travis-ci.org/Maven-Plugin-Ext/maven-release-policies.svg??branch=master)](https://travis-ci.org/Maven-Plugin-Ext/maven-release-policies)

Maven Release Plugin Policies
---

This plugin uses the run time arguments for the versioning of an artifact.

## Use Cases:

Maven currently doesn't allow the variables in artifact version and maven release plugin has limited options when it comes to versioning of released artifacts.

This plugin helps in setting up the released versions at runtime using run time variables. You can add prefix, suffix, build number etc in released version without doing any changes in project.

**Profile Based Release:**

If you need to do multiple releases of an artifact based on maven profile then you can add a *annotation* property in maven profiles and then you can release the artifact using different profiles.

E.g. If you have three mavnen profiles p1, p2, p3 and artifact with SNAPSHOT version 1.0.0-SNAPSHOT then you can perform following releases 1.0.0-p1, 1.0.0-p2, 1.0.0-p3 by running following command "mvn release:prepare -P<profile_name> -DupdateWorkingCopyVersions=false"

please set updateWorkingCopyVersions to true when you are releasing last artifact so that development version is incremented for your artifact.


**Run time parameter based release**

Maven allows you to set the version of an artifact in following format:
**digits-annotation-annotationRevision-buildSpecifier**

- digits represents  MajorVersion.MinorVersion.IncrementalVersion number
- annotationRevision is Integer qualifier for the annotation. (4 as in RC-4)
- buildSpecifier is additional specifier for build. (SNAPSHOT, or build number like "20041114.081234-2")

If you have configured this plugin then all you have to do is pass run time arguments to release:preare command for artifact version:

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
