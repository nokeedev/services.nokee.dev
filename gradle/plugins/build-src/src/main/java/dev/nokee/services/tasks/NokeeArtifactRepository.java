package dev.nokee.services.tasks;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.gson.GsonBuilder;
import lombok.val;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

final class NokeeArtifactRepository {
    private final String base;

    private NokeeArtifactRepository(String base) {
        this.base = base;
    }

    public static NokeeArtifactRepository releaseRepository() {
        return new NokeeArtifactRepository("https://repo.nokee.dev/release");
    }

    public static NokeeArtifactRepository snapshotRepository() {
        return new NokeeArtifactRepository("https://repo.nokee.dev/snapshot");
    }

    private URL path(String path) {
        try {
            return new URL(base + "/" + path);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Cannot build URL from base '" + base + "' and path '" + path + "'.");
        }
    }

    public Set<NokeeVersionInformation> getVersions() {
        return get(path("dev/nokee/version/maven-metadata.xml"), readAllVersions().andThen(eachVersion(queryVersion())));
    }

    private <T> Set<T> get(URL url, Function<InputStream, Set<T>> mapper) {
        try {
            val connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "services.nokee.dev");
            connection.connect();
            if (connection.getResponseCode() == 200) {
                return mapper.apply(connection.getInputStream());
            } else if (connection.getResponseCode() == 404) {
                return ImmutableSet.of();
            } else if (connection.getResponseCode() == 403) {
                return ImmutableSet.of();
            }
            throw new RuntimeException("Receive '" + connection.getResponseCode() + "' with message '" + connection.getResponseMessage() + "' for url '" + url.toString() + "'.");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Function<InputStream, Set<String>> readAllVersions() {
        return inStream -> {
            try {
                return ImmutableSet.copyOf(new MetadataXpp3Reader().read(inStream).getVersioning().getVersions());
            } catch (IOException | XmlPullParserException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private Function<Iterable<String>, Set<NokeeVersionInformation>> eachVersion(Function<String, Optional<NokeeVersionInformation>> versionMapper) {
        return versions ->
                StreamSupport.stream(versions.spliterator(), false)
                        .flatMap(it -> versionMapper.apply(it).map(Stream::of).orElseGet(Stream::empty))
                        .collect(ImmutableSet.toImmutableSet());
    }

    private Function<String, Optional<NokeeVersionInformation>> queryVersion() {
        return version -> {
            return Optional.ofNullable(Iterables.getOnlyElement(get(path("dev/nokee/version/" + version + "/version-" + version + ".json"), inStream -> {
                    try {
                        return ImmutableSet.of(new GsonBuilder()
                                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                                .create()
                                .fromJson(new InputStreamReader(inStream), NokeeVersionInformation.class));
                    } catch (Throwable e) {
                        return ImmutableSet.of(); // ignore version
                    }
                }), null));
        };
    }
}