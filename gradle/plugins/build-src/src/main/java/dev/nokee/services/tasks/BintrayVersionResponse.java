package dev.nokee.services.tasks;

import lombok.Value;

@Value
class BintrayVersionResponse {
    String name;
    String created;
    String message;
}