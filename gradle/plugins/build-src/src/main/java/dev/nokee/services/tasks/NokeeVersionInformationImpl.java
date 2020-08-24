package dev.nokee.services.tasks;

import lombok.Value;

import java.time.LocalDateTime;

@Value
final class NokeeVersionInformationImpl implements NokeeVersionInformation {
    String version;
    boolean snapshot;
    LocalDateTime buildTime;
}