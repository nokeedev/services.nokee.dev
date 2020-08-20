package dev.nokee.services.tasks;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

interface NokeeVersionInformation {
    String getVersion();
    boolean isNightly();
    LocalDateTime getBuildTime();

    static NokeeVersionInformation of(String version, boolean nightly, String buildTime) {
        return new NokeeVersionInformationImpl(version, nightly, LocalDateTime.ofInstant(Instant.parse(buildTime), ZoneOffset.UTC));
    }
}