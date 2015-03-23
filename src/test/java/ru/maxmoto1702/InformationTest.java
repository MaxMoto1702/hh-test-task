package ru.maxmoto1702;

import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import ru.maxmoto1702.fw.HhApi;
import ru.maxmoto1702.fw.HhSite;
import ru.maxmoto1702.fw.UserProperties;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by m on 23.03.2015.
 */
public class InformationTest {
    private static HhApi api;
    private static HhSite site;
    private static UserProperties user;

    @BeforeClass
    public static void setUpClass() throws IOException {
        user = new UserProperties();
        api = new HhApi(user);
        site = new HhSite(user);
    }

    @Test
    public void getInformationTest() throws IOException {
        boolean status = true;
        String lastName = "Иванов";
        String firstName = "Иван";
        String middleName = "Иванович";

        site.setEmployerInformation(lastName, firstName, middleName, status);

        String response = api.executeGet("https://api.hh.ru/me");
        LoggerFactory.getLogger(InformationTest.class).info("Response " + response);
        JSONObject responseJSON = new JSONObject(response);
        assertThat(responseJSON.getString("last_name"), is(lastName));
        assertThat(responseJSON.getString("first_name"), is(firstName));
        assertThat(responseJSON.getString("middle_name"), is(middleName));
        assertThat(responseJSON.getBoolean("is_admin"), is(false));
        assertThat(responseJSON.getBoolean("is_applicant"), is(true));
        assertThat(responseJSON.getBoolean("is_employer"), is(false));
        assertThat(responseJSON.getJSONObject("counters").getInt("unread_negotiations"), is(0));
        assertThat(responseJSON.getJSONObject("counters").getInt("new_resume_views"), is(0));
        assertThat(responseJSON.getBoolean("is_in_search"), is(status));
        assertThat(responseJSON.getString("resumes_url"), is("https://api.hh.ru/resumes/mine"));
        assertThat(responseJSON.getString("negotiations_url"), is("https://api.hh.ru/negotiations"));
    }

    @Test
    public void changeFio() throws IOException {
        String validSymbols[] = {"ааааа", "яяяяяя", "АААААА", "ЯЯЯЯЯ", "aaaaaa", "zzzzz", "AAAAAA", "ZZZZZZ", "aaaa-aaa"};

        for (String validSymbol : validSymbols) {
            String lastName = validSymbol;
            String firstName = validSymbol;
            String middleName = validSymbol;
            String parameters = "last_name=" + lastName + "&" +
                    "first_name=" + firstName + "&" +
                    "middle_name=" + middleName;

            api.executePost("https://api.hh.ru/me", parameters);

            String response = api.executeGet("https://api.hh.ru/me");
            JSONObject responseJSON = new JSONObject(response);

            assertThat(responseJSON.getString("last_name"), is(lastName));
            assertThat(responseJSON.getString("first_name"), is(firstName));
            assertThat(responseJSON.getString("middle_name"), is(middleName));
        }
    }

    @Test
    public void changeStatus() throws IOException {
        boolean status = false;
        String parameters = "is_in_search=" + status;

        api.executePost("https://api.hh.ru/", parameters);

        String response = api.executeGet("https://api.hh.ru/me");
        JSONObject responseJSON = new JSONObject(response);

        assertThat(responseJSON.getBoolean("is_in_search"), is(status));

        status = true;
        parameters = "is_in_search=" + status;

        api.executePost("https://api.hh.ru/", parameters);

        response = api.executeGet("https://api.hh.ru/me");
        responseJSON = new JSONObject(response);

        assertThat(responseJSON.getBoolean("is_in_search"), is(status));
    }

    @Test
    public void notChangeFioAndStatus() {
        Exception error = null;

        boolean status = false;
        String lastName = "Иванов-А";
        String firstName = "Иван-А";
        String middleName = "Иванович-А";
        String parameters = "last_name=" + lastName + "&" +
                "first_name=" + firstName + "&" +
                "middle_name=" + middleName + "&" +
                "is_in_search=" + status;

        try {
            api.executePost("https://api.hh.ru/me", parameters);
        } catch (IOException e) {
            error = e;
        }
        assertThat(error, notNullValue());
        assertThat(error.getMessage(), is("Server returned HTTP response code: 400 for URL: https://api.hh.ru/me"));
    }

    @Test
    public void notChangeFioWithoutLastName() {
        Exception error = null;

        String firstName = "Иван-А";
        String middleName = "Иванович-А";
        String parameters = "first_name=" + firstName + "&" +
                "middle_name=" + middleName;

        try {
            api.executePost("https://api.hh.ru/me", parameters);
        } catch (IOException e) {
            error = e;
        }
        assertThat(error, notNullValue());
        assertThat(error.getMessage(), is("Server returned HTTP response code: 400 for URL: https://api.hh.ru/me"));
    }

    @Test
    public void notChangeFioWithoutFirstName() {
        Exception error = null;

        String lastName = "Иванов-А";
        String middleName = "Иванович-А";
        String parameters = "last_name=" + lastName + "&" +
                "middle_name=" + middleName;

        try {
            api.executePost("https://api.hh.ru/me", parameters);
        } catch (IOException e) {
            error = e;
        }
        assertThat(error, notNullValue());
        assertThat(error.getMessage(), is("Server returned HTTP response code: 400 for URL: https://api.hh.ru/me"));
    }

    @Test
    public void notChangeFioWithoutMiddleName() {
        Exception error = null;

        String lastName = "Иванов-А";
        String firstName = "Иван-А";
        String parameters = "last_name=" + lastName + "&" +
                "first_name=" + firstName;

        try {
            api.executePost("https://api.hh.ru/me", parameters);
        } catch (IOException e) {
            error = e;
        }
        assertThat(error, notNullValue());
        assertThat(error.getMessage(), is("Server returned HTTP response code: 400 for URL: https://api.hh.ru/me"));
    }

    @Test
    public void notChangeFioWithIllegalSymbol() throws IOException {
        Exception error = null;

        String response = api.executeGet("https://api.hh.ru/me");
        JSONObject responseJSON = new JSONObject(response);

        String lastName = responseJSON.getString("last_name");
        String firstName = responseJSON.getString("first_name");
        String middleName = responseJSON.getString("middle_name");

        char illegalSymbols[] = {'!', '@', '#', '$', '%', '^', '&', '*', '(', ')',
                '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
                '`', '\'', '"', '№', ';', ':', '?', '_', '\\', '/',
                '\t', '\r', '\n', ' ', ',', '.', '{', '}', '[', ']'};

        for (char illegalSymbol : illegalSymbols) {
            String parameters = "last_name=" + lastName + "&" +
                    "first_name=" + firstName + "&" +
                    "middle_name=" + middleName;

            try {
                api.executePost("https://api.hh.ru/me", parameters);
            } catch (IOException e) {
                error = e;
            }
            assertThat(error, notNullValue());
            assertThat(error.getMessage(), is("Server returned HTTP response code: 400 for URL: https://api.hh.ru/me"));

            response = api.executeGet("https://api.hh.ru/me");
            responseJSON = new JSONObject(response);

            assertThat(responseJSON.getString("last_name"), is(lastName));
            assertThat(responseJSON.getString("first_name"), is(firstName));
            assertThat(responseJSON.getString("middle_name"), is(middleName));
        }
    }

    @Test
    public void notChangeId() {
        Exception error = null;

        String id = "12345";
        String parameters = "id=" + id;

        try {
            api.executePost("https://api.hh.ru/me", parameters);
        } catch (IOException e) {
            error = e;
        }
        assertThat(error, notNullValue());
        assertThat(error.getMessage(), is("Server returned HTTP response code: 400 for URL: https://api.hh.ru/me"));
    }

    @Test
    public void notChangeEmail() {
        Exception error = null;

        String email = "user@gmail.com";
        String parameters = "email=" + email;

        try {
            api.executePost("https://api.hh.ru/me", parameters);
        } catch (IOException e) {
            error = e;
        }
        assertThat(error, notNullValue());
        assertThat(error.getMessage(), is("Server returned HTTP response code: 400 for URL: https://api.hh.ru/me"));
    }

    @Test
    public void notChangeFlagAdmin() {
        Exception error = null;

        boolean isAdmin = false;
        String parameters = "is_admin=" + isAdmin;

        try {
            api.executePost("https://api.hh.ru/me", parameters);
        } catch (IOException e) {
            error = e;
        }
        assertThat(error, notNullValue());
        assertThat(error.getMessage(), is("Server returned HTTP response code: 400 for URL: https://api.hh.ru/me"));
    }

    @Test
    public void notChangeFlagApplicant() {
        Exception error = null;

        boolean isApplicant = false;
        String parameters = "is_applicant=" + isApplicant;

        try {
            api.executePost("https://api.hh.ru/me", parameters);
        } catch (IOException e) {
            error = e;
        }
        assertThat(error, notNullValue());
        assertThat(error.getMessage(), is("Server returned HTTP response code: 400 for URL: https://api.hh.ru/me"));
    }

    @Test
    public void notChangeFlagEmployer() {
        Exception error = null;

        boolean isEmployer = false;
        String parameters = "is_employer=" + isEmployer;

        try {
            api.executePost("https://api.hh.ru/me", parameters);
        } catch (IOException e) {
            error = e;
        }
        assertThat(error, notNullValue());
        assertThat(error.getMessage(), is("Server returned HTTP response code: 400 for URL: https://api.hh.ru/me"));
    }

    @Test
    public void notChangeResumesUrl() {
        Exception error = null;

        String resumesUrl = "http://ya.ru";
        String parameters = "resumes_url=" + resumesUrl;

        try {
            api.executePost("https://api.hh.ru/me", parameters);
        } catch (IOException e) {
            error = e;
        }
        assertThat(error, notNullValue());
        assertThat(error.getMessage(), is("Server returned HTTP response code: 400 for URL: https://api.hh.ru/me"));
    }

    @Test
    public void notChangenegotiations_url() {
        Exception error = null;

        String negotiationsUrl = "http://ya.ru";
        String parameters = "negotiations_url=" + negotiationsUrl;

        try {
            api.executePost("https://api.hh.ru/me", parameters);
        } catch (IOException e) {
            error = e;
        }
        assertThat(error, notNullValue());
        assertThat(error.getMessage(), is("Server returned HTTP response code: 400 for URL: https://api.hh.ru/me"));
    }
}
