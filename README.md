# maven-release-policies
Maven Release Plugin Policies

Maven release plugin support the version of an artifact in following format:
component-digits-annotation-annotationRevision-buildSpecifier

component is the name of the component that is being released
digits represents  MajorVersion.MinorVersion.IncrementalVersion number
annotationRevision is Integer qualifier for the annotation. (4 as in RC-4)
buildSpecifier is additional specifier for build. (SNAPSHOT, or build number like "20041114.081234-2")

Examples:
my-component-1.0.1-alpha-2-SNAPSHOT
log4j-1.2.9-beta-9-SNAPSHOT
log4j1.2.9beta9SNAPSHOT
log4j_1.2.9_beta_9_SNAPSHOT
1.0.0-SNAPSHOT
1.0.0-java7-SNAPSHOT
1.0.0-RC-4-SNAPSHOT
20041114-SNAPSHOT


