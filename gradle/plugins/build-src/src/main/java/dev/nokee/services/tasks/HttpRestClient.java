package dev.nokee.services.tasks;

import com.google.gson.Gson;
import lombok.val;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Base64;

class HttpRestClient {
    public static String get(URL endpoint, String username, String password) throws IOException {
        val httpConnection = (HttpURLConnection) endpoint.openConnection();
        try {
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Accept", "application/json");
            httpConnection.setDoOutput(false);

            val basicCredentials = username + ":" + password;
            val basicAuthentication = "Basic " + new String(Base64.getEncoder().encode(basicCredentials.getBytes()));
            httpConnection.setRequestProperty("Authorization", basicAuthentication);

            if (!isSuccessful(httpConnection)) {
                throw new RuntimeException(String.format("Bad response, received %d with message '%s'.", httpConnection.getResponseCode(), asString(httpConnection.getErrorStream())));
            }

            return asString(httpConnection.getInputStream());
        } finally {
            httpConnection.disconnect();
        }
    }

    private static String asString(InputStream inStream) throws IOException {
        return IOUtils.toString(inStream, Charset.defaultCharset());
    }

    private static boolean isSuccessful(HttpURLConnection connection) throws IOException {
        return connection.getResponseCode() >= 200 && connection.getResponseCode() < 300;
    }
}
