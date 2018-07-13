package com.extensions;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.shared.release.versions.VersionInfo;
import org.apache.maven.shared.release.versions.VersionParseException;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The original implementation of this class is from org.apache.maven.shared.release.versions.DefaultVersionInfo.
 * Few changes have been done for ConfigurableVersionPolicy implementation.
 */
public class DefaultVersionInfo implements VersionInfo {
    private final String strVersion;
    private final List<String> digits;
    private String annotation;
    private String annotationRevision;
    private final String buildSpecifier;
    private String annotationSeparator;
    private String annotationRevSeparator;
    private final String buildSeparator;
    private static final int DIGITS_INDEX = 1;
    private static final int ANNOTATION_SEPARATOR_INDEX = 2;
    private static final int ANNOTATION_INDEX = 3;
    private static final int ANNOTATION_REV_SEPARATOR_INDEX = 4;
    private static final int ANNOTATION_REVISION_INDEX = 5;
    private static final int BUILD_SEPARATOR_INDEX = 6;
    private static final int BUILD_SPECIFIER_INDEX = 7;
    private static final String SNAPSHOT_IDENTIFIER = "SNAPSHOT";
    private static final String DIGIT_SEPARATOR_STRING = ".";
    public static final Pattern STANDARD_PATTERN = Pattern.compile("^((?:\\d+\\.)*\\d+)([-_])?([a-zA-Z]*)([-_])?(\\d*)(?:([-_])?(.*?))?$");
    public static final Pattern ALTERNATE_PATTERN = Pattern.compile("^(SNAPSHOT|[a-zA-Z]+[_-]SNAPSHOT)");
    public static final String DEFAULT_ANNOTATION_SEPARATOR = "-";
    public static final String DEFAULT_ANNOTATION_REV_SEPARATOR = "-";
    public static final String DEFAULT_BUILD_SEPARATOR = "-";
    public DefaultVersionInfo(String version) throws VersionParseException {
        this.strVersion = version;
        Matcher matcher = ALTERNATE_PATTERN.matcher(this.strVersion);
        if(matcher.matches()) {
            this.annotation = null;
            this.digits = null;
            this.buildSpecifier = version;
            this.buildSeparator = null;
        } else {
            Matcher m = STANDARD_PATTERN.matcher(this.strVersion);
            if(!m.matches()) {
                throw new VersionParseException("Unable to parse the version string: \"" + version + "\"");
            } else {
                this.digits = this.parseDigits(m.group(1));
                if(!"SNAPSHOT".equals(m.group(3))) {
                    this.annotationSeparator = m.group(2);
                    this.annotation = nullIfEmpty(m.group(3));
                    if(StringUtils.isNotEmpty(m.group(4)) && StringUtils.isEmpty(m.group(5))) {
                        this.buildSeparator = m.group(4);
                        this.buildSpecifier = nullIfEmpty(m.group(7));
                    } else {
                        this.annotationRevSeparator = m.group(4);
                        this.annotationRevision = nullIfEmpty(m.group(5));
                        this.buildSeparator = m.group(6);
                        this.buildSpecifier = nullIfEmpty(m.group(7));
                    }
                } else {
                    this.buildSeparator = m.group(2);
                    this.buildSpecifier = nullIfEmpty(m.group(3));
                }

            }
        }
    }

    public DefaultVersionInfo(List<String> digits, String annotation, String annotationRevision, String buildSpecifier, String annotationSeparator, String annotationRevSeparator, String buildSeparator) {
        this.digits = digits;
        this.annotation = annotation;
        this.annotationRevision = annotationRevision;
        this.buildSpecifier = buildSpecifier;
        this.annotationSeparator = annotationSeparator;
        this.annotationRevSeparator = annotationRevSeparator;
        this.buildSeparator = buildSeparator;
        this.strVersion = getVersionString(this, buildSpecifier, buildSeparator);
    }

    public boolean isSnapshot() {
        return ArtifactUtils.isSnapshot(this.strVersion);
    }

    public VersionInfo getNextVersion() {
        org.apache.maven.shared.release.versions.DefaultVersionInfo version = null;
        if(this.digits != null) {
            List<String> digits = new ArrayList(this.digits);
            String annotationRevision = this.annotationRevision;
            if(StringUtils.isNumeric(annotationRevision)) {
                annotationRevision = this.incrementVersionString(annotationRevision);
            } else {
                digits.set(digits.size() - 1, this.incrementVersionString((String)digits.get(digits.size() - 1)));
            }

            version = new org.apache.maven.shared.release.versions.DefaultVersionInfo(digits, this.annotation, annotationRevision, this.buildSpecifier, this.annotationSeparator, this.annotationRevSeparator, this.buildSeparator);
        }

        return version;
    }

    public int compareTo(VersionInfo obj) {
        org.apache.maven.shared.release.versions.DefaultVersionInfo that = (org.apache.maven.shared.release.versions.DefaultVersionInfo)obj;
        int result;
        if(this.strVersion.startsWith(that.strVersion) && !this.strVersion.equals(that.strVersion) && this.strVersion.charAt(that.strVersion.length()) != 45) {
            result = 1;
        } else if(that.strVersion.startsWith(this.strVersion) && !this.strVersion.equals(that.strVersion) && that.strVersion.charAt(this.strVersion.length()) != 45) {
            result = -1;
        } else {
            String thisVersion = this.strVersion.toUpperCase(Locale.ENGLISH).toLowerCase(Locale.ENGLISH);
            String thatVersion = that.strVersion.toUpperCase(Locale.ENGLISH).toLowerCase(Locale.ENGLISH);
            result = (new DefaultArtifactVersion(thisVersion)).compareTo(new DefaultArtifactVersion(thatVersion));
        }

        return result;
    }

    public boolean equals(Object obj) {
        return !(obj instanceof org.apache.maven.shared.release.versions.DefaultVersionInfo)?false:this.compareTo((VersionInfo)obj) == 0;
    }

    public int hashCode() {
        return this.strVersion.toLowerCase(Locale.ENGLISH).hashCode();
    }

    protected String incrementVersionString(String s) {
        int n = Integer.valueOf(s).intValue() + 1;
        String value = String.valueOf(n);
        if(value.length() < s.length()) {
            value = StringUtils.leftPad(value, s.length(), "0");
        }

        return value;
    }

    public String getSnapshotVersionString() {
        if(this.strVersion.equals("SNAPSHOT")) {
            return this.strVersion;
        } else {
            String baseVersion = this.getReleaseVersionString();
            if(baseVersion.length() > 0) {
                baseVersion = baseVersion + "-";
            }

            return baseVersion + "SNAPSHOT";
        }
    }

    public String getReleaseVersionString() {
        String baseVersion = this.strVersion;
        Matcher m = Artifact.VERSION_FILE_PATTERN.matcher(baseVersion);
        if(m.matches()) {
            baseVersion = m.group(1);
        } else if(StringUtils.right(baseVersion, 9).equalsIgnoreCase("-SNAPSHOT")) {
            baseVersion = baseVersion.substring(0, baseVersion.length() - "SNAPSHOT".length() - 1);
        } else if(baseVersion.equals("SNAPSHOT")) {
            baseVersion = "1.0";
        }

        return baseVersion;
    }

    public String toString() {
        return this.strVersion;
    }

    protected static String getVersionString(org.apache.maven.shared.release.versions.DefaultVersionInfo info, String buildSpecifier, String buildSeparator) {
        StringBuilder sb = new StringBuilder();
        if(info.digits != null) {
            sb.append(joinDigitString(info.digits));
        }

        if(StringUtils.isNotEmpty(info.annotation)) {
            sb.append(StringUtils.defaultString(info.annotationSeparator));
            sb.append(info.annotation);
        }

        if(StringUtils.isNotEmpty(info.annotationRevision)) {
            if(StringUtils.isEmpty(info.annotation)) {
                sb.append(StringUtils.defaultString(info.annotationSeparator));
            } else {
                sb.append(StringUtils.defaultString(info.annotationRevSeparator));
            }

            sb.append(info.annotationRevision);
        }

        if(StringUtils.isNotEmpty(buildSpecifier)) {
            sb.append(StringUtils.defaultString(buildSeparator));
            sb.append(buildSpecifier);
        }

        return sb.toString();
    }

    protected static String joinDigitString(List<String> digits) {
        return digits != null?StringUtils.join(digits.iterator(), "."):null;
    }

    private List<String> parseDigits(String strDigits) {
        return Arrays.asList(StringUtils.split(strDigits, "."));
    }

    private static String nullIfEmpty(String s) {
        return StringUtils.isEmpty(s)?null:s;
    }

    public List<String> getDigits() {
        return this.digits;
    }

    public String getAnnotation() {
        return this.annotation;
    }

    public String getAnnotationRevision() {
        return this.annotationRevision;
    }

    public String getBuildSpecifier() {
        return this.buildSpecifier;
    }

    public String getAnnotationSeparator() {
        return annotationSeparator;
    }

    public String getAnnotationRevSeparator() {
        return annotationRevSeparator;
    }

    public String getBuildSeparator() {
        return buildSeparator;
    }
}