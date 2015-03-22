package ru.maxmoto1702.fw;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by m on 22.03.2015.
 */
public class HhApi {
    public static final String CHARSET_NAME = "UTF-8";
    private static Logger LOG = LoggerFactory.getLogger(HhApi.class);

    private UserProperties userProperties;
    private ApplicationProperties applicationProperties;
    private String authorizationCode;
    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private String refreshToken;

    public HhApi() {
        this(new UserProperties());
    }

    public HhApi(UserProperties userProperties) {
        this.userProperties = userProperties;
        this.applicationProperties = new ApplicationProperties();
        setAuthorizationCode();
        setToken();
    }

    private void setAuthorizationCode() {
        WebDriver driver = new PhantomJSDriver();
        driver.get("https://m.hh.ru/oauth/authorize?response_type=code&client_id=" + applicationProperties.getClientId());
        driver.findElement(By.xpath("//input[@name='username']")).sendKeys(userProperties.getUsername());
        driver.findElement(By.xpath("//input[@name='password']")).sendKeys(userProperties.getPassword());
        driver.findElement(By.xpath("//input[@type='submit']")).click();
        String currentUrl = driver.getCurrentUrl();
        if (currentUrl.equals("https://m.hh.ru/oauth/authorize?response_type=code&client_id=" + applicationProperties.getClientId())) {
            driver.findElement(By.xpath("//button[contains(@class, 'button_green')]")).click();
        }
        this.authorizationCode = currentUrl.substring(currentUrl.indexOf("code=") + "code=".length());
    }

    private void setToken() {
        String parameters = "grant_type=authorization_code&client_id=" + applicationProperties.getClientId() +
                "&client_secret=" + applicationProperties.getClientSecret() +
                "&code=" + authorizationCode;
        String source = executePost("https://m.hh.ru/oauth/token", parameters);
        try {
            JSONObject tokenResponse = new JSONObject(source);
            this.accessToken = tokenResponse.getString("access_token");
            this.tokenType = tokenResponse.getString("token_type");
            this.expiresIn = tokenResponse.getLong("expires_in");
            this.refreshToken = tokenResponse.getString("refresh_token");
        } catch (JSONException e) {
            LOG.error("Response isn't JSON", e);
        }
    }

    public String executeGet(String url) {
        return executeGet(url, null);
    }

    public String executeGet(String url, String parameters) {
        return executeGet(url, null, parameters);
    }

    public String executeGet(String url, Map<String, String> headers, String parameters) {
        return execute("GET", url, headers, parameters);
    }

    public String executePost(String url, String parameters) {
        return executePost(url, null, parameters);
    }

    public String executePost(String url, Map<String, String> headers, String parameters) {
        return execute("POST", url, headers, parameters);
    }

    public String execute(String method, String url, Map<String, String> headers, String parameters) {
        StringBuffer response = null;
        try {
            HttpURLConnection connection;
            if ((method == null || method.equals("GET")) && parameters != null) {
                connection = (HttpURLConnection) new URL(url + "?" + parameters).openConnection();
            } else {
                connection = (HttpURLConnection) new URL(url).openConnection();
            }
            String host = url.substring(url.indexOf("://") + "://".length(), url.indexOf("://") + "://".length() + url.substring(url.indexOf("://") + "://".length()).indexOf('/'));
            if (method != null) {
                connection.setRequestMethod(method);
            }
            connection.setRequestProperty("Host", host);
            connection.setRequestProperty("Accept", "*/*");
            connection.setRequestProperty("User-Agent", applicationProperties.getName() + "/" + applicationProperties.getVersion() + " (" + userProperties.getUsername() + ")");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);

            if (headers != null) {
                for (String header : headers.keySet()) {
                    connection.setRequestProperty(header, headers.get(header));
                }
            }

            if (method != null && method.equals("POST")) {
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Content-Length", "" + parameters.getBytes(CHARSET_NAME).length);
                connection.setDoOutput(true);
                try (OutputStream output = connection.getOutputStream()) {
                    output.write(parameters.getBytes(CHARSET_NAME));
                }
            }

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, CHARSET_NAME));
            String line;
            response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\n');
            }
            rd.close();
        } catch (MalformedURLException e) {
            LOG.error("", e);
        } catch (IOException e) {
            LOG.error("", e);
        }
        return response.toString();
    }
}
