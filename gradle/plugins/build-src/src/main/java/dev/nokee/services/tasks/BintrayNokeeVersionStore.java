package dev.nokee.services.tasks;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.val;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.util.List;

public final class BintrayNokeeVersionStore {
    public List<NokeeVersionInformation> get() {
        this.getClass().getClassLoader().getResourceAsStream("/bintray-versions.json");

        try (val inStream = new InputStreamReader(openBintrayVersions())) {
            return new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .setPrettyPrinting()
                    .create()
                    .fromJson(inStream, new TypeToken<List<NokeeVersionInformation>>() {}.getType());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static InputStream openBintrayVersions() {
        return BintrayNokeeVersionStore.class.getClassLoader().getResourceAsStream("bintray-versions.json");
    }
}
