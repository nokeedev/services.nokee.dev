package dev.nokee.services.tasks;

import lombok.Value;

import java.time.LocalDateTime;

@Value
class NokeeVersionInformationImpl implements NokeeVersionInformation {
    String version;
    boolean nightly;
    LocalDateTime buildTime;
}