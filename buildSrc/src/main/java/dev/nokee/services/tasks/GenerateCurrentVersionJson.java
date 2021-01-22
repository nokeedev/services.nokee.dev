package dev.nokee.services.tasks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.credentials.PasswordCredentials;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDateTime;

public abstract class GenerateCurrentVersionJson extends DefaultTask {
    @OutputFile
    public abstract RegularFileProperty getOutputFile();

    @Internal
    public abstract Property<PasswordCredentials> getCredentials();

    @TaskAction
    private void doGenerate() throws IOException {
        val content = HttpRestClient.get(new URL("https://api.bintray.com/packages/nokeedev/distributions/dev.nokee:nokee-gradle-plugins/versions/_latest"), getBintrayUser(), getBintrayKey());
        val response = new Gson().fromJson(content, BintrayVersionResponse.class);
        val data = NokeeVersionInformation.of(response.getName(), false, response.getCreated());

        FileUtils.write(getOutputFile().get().getAsFile(), new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).setPrettyPrinting().create().toJson(data), Charset.defaultCharset());
    }

    private String getBintrayUser() {
        return getCredentials().get().getUsername();
    }

    private String getBintrayKey() {
        return getCredentials().get().getPassword();
    }
}
