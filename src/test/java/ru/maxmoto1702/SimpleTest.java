package ru.maxmoto1702;

import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.LoggerFactory;
import ru.maxmoto1702.fw.HhApi;
import ru.maxmoto1702.fw.UserProperties;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by m on 23.03.2015.
 */
public class SimpleTest {
    private static HhApi api;
    private static UserProperties user;

    @BeforeClass
    public static void setUpClass() {
        user = new UserProperties();
        api = new HhApi(user);
    }

    @Test
    public void simpleTest() {
        WebDriver driver = new PhantomJSDriver(new DesiredCapabilities());
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().setSize(new Dimension(1300, 768));
        driver.get("http://hh.ru");
        driver.findElement(By.xpath("//form[@data-qa='promo-account-login-form']//input[@name='username']")).clear();
        driver.findElement(By.xpath("//form[@data-qa='promo-account-login-form']//input[@name='username']")).sendKeys(user.getUsername());
        driver.findElement(By.xpath("//form[@data-qa='promo-account-login-form']//input[@name='password']")).clear();
        driver.findElement(By.xpath("//form[@data-qa='promo-account-login-form']//input[@name='password']")).sendKeys(user.getPassword());
        driver.findElement(By.xpath("//form[@data-qa='promo-account-login-form']//input[@type='submit']")).click();
        driver.get("http://hh.ru/applicant/settings");
        WebElement clickableStatus = driver.findElement(By.xpath("//button[contains(@class, 'HH-SettingsFreeze-Button')]"));
        if (clickableStatus.getText().equals("Сейчас я ищу работу")) {
            clickableStatus.click();
        }
        driver.findElement(By.name("lastName")).clear();
        driver.findElement(By.name("lastName")).sendKeys("Иванов");
        driver.findElement(By.name("firstName")).clear();
        driver.findElement(By.name("firstName")).sendKeys("Иван");
        driver.findElement(By.name("middleName")).clear();
        driver.findElement(By.name("middleName")).sendKeys("Иванович");
        driver.findElement(By.xpath("//input[contains(@class, ' HH-ApplicantSettingsFio-Submit')]")).click();
        driver.quit();

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
