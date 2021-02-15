package dev.nokee.services.tasks;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.GsonBuilder;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Comparator;

public abstract class GenerateAllVersionJson extends DefaultTask {
    @OutputFile
    public abstract RegularFileProperty getOutputFile();

    @TaskAction
    private void doGenerate() throws IOException {
        val data = ImmutableSet.<NokeeVersionInformation>builder()
                .addAll(new BintrayNokeeVersionStore().get())
                .addAll(new NokeeRepositoryService().getAllVersions())
                .build()
                .stream()
                .sorted(Comparator.comparing(NokeeVersionInformation::getBuildTime).reversed())
                .collect(ImmutableList.toImmutableList());

        FileUtils.write(getOutputFile().get().getAsFile(), new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).setPrettyPrinting().create().toJson(data), Charset.defaultCharset());
    }
}
