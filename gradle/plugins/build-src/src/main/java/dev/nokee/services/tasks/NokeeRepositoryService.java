package dev.nokee.services.tasks;

import lombok.val;

import java.util.HashSet;
import java.util.Set;

import static dev.nokee.services.tasks.NokeeArtifactRepository.releaseRepository;
import static dev.nokee.services.tasks.NokeeArtifactRepository.snapshotRepository;

public final class NokeeRepositoryService {
    public Set<NokeeVersionInformation> getAllVersions() {
        val versions = new HashSet<NokeeVersionInformation>();
        versions.addAll(releaseRepository().getVersions());
        versions.addAll(snapshotRepository().getVersions());
        return versions;
    }
}
