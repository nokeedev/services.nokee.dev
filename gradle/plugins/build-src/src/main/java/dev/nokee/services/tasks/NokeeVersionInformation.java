package dev.nokee.services.tasks;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

interface NokeeVersionInformation {
    String getVersion();
    boolean isSnapshot();
    LocalDateTime getBuildTime();

    static NokeeVersionInformation of(String version, boolean snapshot, String buildTime) {
        return new NokeeVersionInformationImpl(version, snapshot, LocalDateTime.ofInstant(Instant.parse(buildTime), ZoneOffset.UTC));
    }
}