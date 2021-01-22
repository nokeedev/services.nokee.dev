package dev.nokee.services.tasks;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static java.time.LocalDateTime.ofInstant;

abstract class NokeeVersionInformation {
    public abstract String getVersion();
    public abstract boolean isSnapshot();
    public abstract LocalDateTime getBuildTime();

    public static NokeeVersionInformation of(String version, boolean snapshot, String buildTime) {
        return new DefaultNokeeVersionInformation(version, snapshot, ofInstant(Instant.parse(buildTime), ZoneOffset.UTC));
    }

    @Value
    @EqualsAndHashCode(callSuper = false)
    private static class DefaultNokeeVersionInformation extends NokeeVersionInformation {
        String version;
        boolean snapshot;
        LocalDateTime buildTime;
    }

    public static NokeeVersionInformation missing() {
        return MissingNokeeVersionInformation.MISSING_VERSION;
    }

    private static class MissingNokeeVersionInformation extends NokeeVersionInformation {
        public static final NokeeVersionInformation MISSING_VERSION = new MissingNokeeVersionInformation();

        @Override
        public String getVersion() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isSnapshot() {
            throw new UnsupportedOperationException();
        }

        @Override
        public LocalDateTime getBuildTime() {
            throw new UnsupportedOperationException();
        }
    }
}