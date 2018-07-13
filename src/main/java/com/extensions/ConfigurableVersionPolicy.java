package com.extensions;

import org.apache.maven.shared.release.policy.PolicyException;
import org.apache.maven.shared.release.policy.version.VersionPolicy;
import org.apache.maven.shared.release.policy.version.VersionPolicyRequest;
import org.apache.maven.shared.release.policy.version.VersionPolicyResult;
import org.apache.maven.shared.release.versions.VersionParseException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.util.StringUtils;

/**
 * This class uses the system properties for release and development version.
 */
@Component(role = VersionPolicy.class,
        hint = "ConfigurableVersionPolicy",
        description = "Adds suffix to release version. E.g. -beta")
public class ConfigurableVersionPolicy implements VersionPolicy {


    @Override
    public VersionPolicyResult getReleaseVersion(VersionPolicyRequest versionPolicyRequest)
            throws PolicyException, VersionParseException {

        return new VersionPolicyResult()
                .setVersion(getDefaultVersionInfo(versionPolicyRequest).getReleaseVersionString());
    }

    @Override
    public VersionPolicyResult getDevelopmentVersion(VersionPolicyRequest versionPolicyRequest) throws PolicyException, VersionParseException {
        DefaultVersionInfo defaultVersionInfo = new DefaultVersionInfo(versionPolicyRequest.getVersion());

        return new VersionPolicyResult()
                .setVersion(getDefaultVersionInfo(versionPolicyRequest).getNextVersion().getSnapshotVersionString());

    }

    private DefaultVersionInfo getDefaultVersionInfo(VersionPolicyRequest versionPolicyRequest) throws PolicyException, VersionParseException{
        DefaultVersionInfo defaultVersionInfo = new DefaultVersionInfo(versionPolicyRequest.getVersion());

        String annotation = System.getProperty("annotation");
        String annotationSeparator = System.getProperty("annotationSeparator");
        if(StringUtils.isEmpty(annotation)) {
            annotation = defaultVersionInfo.getAnnotation();
            annotationSeparator = defaultVersionInfo.getAnnotationSeparator();
        } else {
            if (StringUtils.isEmpty(annotationSeparator)) {
                annotationSeparator = DefaultVersionInfo.DEFAULT_ANNOTATION_SEPARATOR;
            }
        }

        String annotationRevision =  System.getProperty("annotationRevision");
        String annotationRevSeparator = System.getProperty("annotationRevSeparator");
        if(StringUtils.isEmpty(annotationRevision)) {
            annotationRevision = defaultVersionInfo.getAnnotationRevision();
            annotationRevSeparator = defaultVersionInfo.getAnnotationRevSeparator();
        } else {
            if(StringUtils.isEmpty(annotationRevSeparator)) {
                annotationRevSeparator = DefaultVersionInfo.DEFAULT_ANNOTATION_REV_SEPARATOR;
            }
        }

        String buildSpecifier = System.getProperty("buildSpecifier");
        String buildSeparator = System.getProperty("buildSeparator");
        if(StringUtils.isEmpty(buildSpecifier)) {
            buildSpecifier = defaultVersionInfo.getBuildSpecifier();
            buildSeparator = defaultVersionInfo.getBuildSeparator();
        } else {
            if (StringUtils.isEmpty(buildSeparator)) {
                buildSeparator = DefaultVersionInfo.DEFAULT_BUILD_SEPARATOR;
            }
        }


        return new DefaultVersionInfo(defaultVersionInfo.getDigits(), annotation,
                annotationRevision, buildSpecifier,
                annotationSeparator, annotationRevSeparator, buildSeparator);
    }
}
