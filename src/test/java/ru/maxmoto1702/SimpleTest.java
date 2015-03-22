package ru.maxmoto1702;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import ru.maxmoto1702.fw.HhApi;
import ru.maxmoto1702.fw.UserProperties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by m on 23.03.2015.
 */
public class SimpleTest {
    private HhApi api;
    private UserProperties user;

    @Before
    public void setUpClass() {
        user = new UserProperties();
        api = new HhApi(user);
    }

    @Test
    public void simpleTest() {
        String response = api.executeGet("https://api.hh.ru/me");
        LoggerFactory.getLogger(SimpleTest.class).info("Response " + response);
        JSONObject responseJSON = new JSONObject(response);
        assertThat(responseJSON.getString("last_name"), is("Иванов"));
        assertThat(responseJSON.getString("first_name"), is("Иван"));
        assertThat(responseJSON.getString("middle_name"), is("Иванович"));
        assertThat(responseJSON.getBoolean("is_admin"), is(false));
        assertThat(responseJSON.getBoolean("is_applicant"), is(true));
        assertThat(responseJSON.getBoolean("is_employer"), is(false));
        assertThat(responseJSON.getJSONObject("counters").getInt("unread_negotiations"), is(0));
        assertThat(responseJSON.getJSONObject("counters").getInt("new_resume_views"), is(0));
        assertThat(responseJSON.getBoolean("is_in_search"), is(true));
        assertThat(responseJSON.getString("resumes_url"), is("https://api.hh.ru/resumes/mine"));
        assertThat(responseJSON.getString("negotiations_url"), is("https://api.hh.ru/negotiations"));
    }
}
