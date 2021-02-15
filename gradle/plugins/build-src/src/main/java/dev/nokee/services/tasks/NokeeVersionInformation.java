package dev.nokee.services.tasks;

import lombok.Getter;
import lombok.Value;

import java.time.LocalDateTime;

@Value
class NokeeVersionInformation {
    @Getter String version;
    @Getter boolean snapshot;
    @Getter LocalDateTime buildTime;
}