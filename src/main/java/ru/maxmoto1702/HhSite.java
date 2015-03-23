package ru.maxmoto1702;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import ru.maxmoto1702.fw.UserProperties;

import java.util.concurrent.TimeUnit;

/**
 * Created by m on 23.03.2015.
 */
public class HhSite {

    private UserProperties userProperties;

    public HhSite(UserProperties userProperties) {
        this.userProperties = userProperties;
    }

    public void setEmployerInformation(String lastName, String firstName, String middleName, boolean status) {
        WebDriver driver = new PhantomJSDriver(new DesiredCapabilities());
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().setSize(new Dimension(1300, 768));
        driver.get("http://hh.ru");
        driver.findElement(By.xpath("//form[@data-qa='promo-account-login-form']//input[@name='username']")).clear();
        driver.findElement(By.xpath("//form[@data-qa='promo-account-login-form']//input[@name='username']")).sendKeys(userProperties.getUsername());
        driver.findElement(By.xpath("//form[@data-qa='promo-account-login-form']//input[@name='password']")).clear();
        driver.findElement(By.xpath("//form[@data-qa='promo-account-login-form']//input[@name='password']")).sendKeys(userProperties.getPassword());
        driver.findElement(By.xpath("//form[@data-qa='promo-account-login-form']//input[@type='submit']")).click();
        driver.get("http://hh.ru/applicant/settings");
        WebElement clickableStatus = driver.findElement(By.xpath("//button[contains(@class, 'HH-SettingsFreeze-Button')]"));
        if (status) {
            if (clickableStatus.getText().equals("Сейчас я ищу работу")) {
                clickableStatus.click();
            }
        } else {
            if (!clickableStatus.getText().equals("Сейчас я ищу работу")) {
                clickableStatus.click();
            }
        }
        try {
            Thread.sleep(1500);
        } catch (Exception e) {
        }
        driver.findElement(By.name("lastName")).clear();
        driver.findElement(By.name("lastName")).sendKeys(lastName);
        driver.findElement(By.name("firstName")).clear();
        driver.findElement(By.name("firstName")).sendKeys(firstName);
        driver.findElement(By.name("middleName")).clear();
        driver.findElement(By.name("middleName")).sendKeys(middleName);
        driver.findElement(By.xpath("//input[contains(@class, ' HH-ApplicantSettingsFio-Submit')]")).click();
        driver.quit();
    }
}
