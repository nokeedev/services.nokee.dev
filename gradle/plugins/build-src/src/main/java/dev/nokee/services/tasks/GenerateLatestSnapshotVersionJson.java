package dev.nokee.services.tasks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.credentials.PasswordCredentials;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public abstract class GenerateLatestSnapshotVersionJson extends DefaultTask {
    @InputFile
    public abstract RegularFileProperty getAllVersionsFile();

    @OutputFile
    public abstract RegularFileProperty getOutputFile();

    @TaskAction
    private void doGenerate() throws IOException {
        List<NokeeVersionInformation> versions = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create()
                .fromJson(FileUtils.readFileToString(getAllVersionsFile().get().getAsFile(), StandardCharsets.UTF_8), new TypeToken<List<NokeeVersionInformation>>() {}.getType());
        val data = versions.stream().filter(NokeeVersionInformation::isSnapshot).max(Comparator.comparing(NokeeVersionInformation::getBuildTime)).get();

        FileUtils.write(getOutputFile().get().getAsFile(), new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).setPrettyPrinting().create().toJson(data), StandardCharsets.UTF_8);
    }
}
