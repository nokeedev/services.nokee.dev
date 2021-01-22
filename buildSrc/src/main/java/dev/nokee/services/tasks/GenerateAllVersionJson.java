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
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class GenerateAllVersionJson extends DefaultTask {
    @OutputFile
    public abstract RegularFileProperty getOutputFile();

    @Internal
    public abstract Property<PasswordCredentials> getCredentials();

    @TaskAction
    private void doGenerate() throws IOException {
        val allVersions = Stream.of("distributions", "distributions-snapshots").flatMap(it -> findAllVersions(it).stream()).collect(Collectors.toSet());

        val data = allVersions.stream().map(this::versionInformation).filter(it -> !it.equals(NokeeVersionInformation.missing())).sorted(Comparator.comparing(NokeeVersionInformation::getBuildTime).reversed()).collect(Collectors.toList());

        FileUtils.write(getOutputFile().get().getAsFile(), new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).setPrettyPrinting().create().toJson(data), Charset.defaultCharset());
    }

    private Set<String> findAllVersions(String repositoryName) {
        val allVersions = new HashSet<String>();
        val content = getContent("https://dl.bintray.com/nokeedev/" + repositoryName + "/dev/nokee/jni-library/dev.nokee.jni-library.gradle.plugin/");
        val matcher = Pattern.compile("(\\d+\\.\\d+\\.\\d+(-[a-z0-9]{8})?)").matcher(content);
        while (matcher.find()) {
            allVersions.add(matcher.group(1));
        }

        return allVersions;
    }

    private NokeeVersionInformation versionInformation(String version) {
        val isSnapshot = version.contains("-");
        val repositoryName = isSnapshot ? "distributions-snapshots" : "distributions";
        val packageName = isSnapshot ? "artifacts" : "dev.nokee:nokee-gradle-plugins";

        try {
            val content = HttpRestClient.get(new URL("https://api.bintray.com/packages/nokeedev/" + repositoryName + "/" + packageName + "/versions/" + version), getBintrayUser(), getBintrayKey());
            val response = new Gson().fromJson(content, BintrayVersionResponse.class);

            if (response.getMessage() != null && response.getMessage().equals("Version '" + version + "' was not found")) {
                System.out.println(response.getMessage());
                return NokeeVersionInformation.missing();
            }
            try {
                return NokeeVersionInformation.of(version, isSnapshot, response.getCreated());
            } catch (Throwable e) {
                throw new RuntimeException("An error happen, see api query output of '" + content + "'.", e);
            }
        } catch (IOException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    private String getBintrayUser() {
        return getCredentials().get().getUsername();
    }

    private String getBintrayKey() {
        return getCredentials().get().getPassword();
    }

    private String getContent(Object uri) {
        return getProject().getResources().getText().fromUri(uri).asString();
    }
}
