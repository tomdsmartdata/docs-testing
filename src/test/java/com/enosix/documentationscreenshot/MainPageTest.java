package com.enosix.documentationscreenshot;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.fail;

public class MainPageTest {
    public static final String DOCS_ENOSIX_IO_HOST = "docs.enosix.io";
    public static final String SALMON_BEACH_HOST = "salmon-beach-039a75d0f.3.azurestaticapps.net";
    String oldSiteUrlsPaths = "src/test/resources/docs-enosix-io.txt";
    String newSiteUrlsPaths = "src/test/resources/salmon-beach.txt";
    @BeforeAll
    public static void setUpAll() {
        Configuration.browserSize = "1920x1080";
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    public void setUp() {
        // Fix the issue https://github.com/SeleniumHQ/selenium/issues/11750
        Configuration.browserCapabilities = new ChromeOptions().addArguments("--remote-allow-origins=*");
    }
    @Test
    public void captureScreenshotsThatExistInOldButNotNew() {
        try {

            BufferedReader br = new BufferedReader(new FileReader(oldSiteUrlsPaths));
            String line;
            List<String> brokenUrls = new LinkedList<>();

            // For every URL scraped from the old documentation site
            while((line = br.readLine()) != null) {
                // the original URL in case we need to get a screenshot for comparison
                var originalLine = line;

                // Change the host to the new documentation site, not the old
                line = line.replace(DOCS_ENOSIX_IO_HOST, SALMON_BEACH_HOST);

                // Open the page on the new documentation site
                open(line);

                // Issue a HEAD request  to confirm the page exists on the new site
                HttpURLConnection cn = (HttpURLConnection)new URL(webdriver().driver().getWebDriver().getCurrentUrl()).openConnection();
                cn.setRequestMethod("HEAD");
                // connection initiate
                cn.connect();
                // get response code
                int res = cn.getResponseCode();

                // If we get anything other than a 200 SUCCESS from the url on the new site
                if(res != 200) {
                    // Add this to our list of broken URLs
                    brokenUrls.add(line);

                    // Take a screenshot of this broken page (likely should be a 404 page or an error page)
                    String screenshotUrlName = line.substring(("https://"+SALMON_BEACH_HOST+"/").length())
                            .replace(':', '_')
                            .replace('-', '_')
                            .replace('/', '-');
                    String screenshot = screenshot("/screenshots/"
                            + screenshotUrlName
                            + "-"
                            + SALMON_BEACH_HOST);

                    // Take a screenshot of the page from the old documentation site for comparison
                    open(originalLine);
                    screenshot = screenshot("/screenshots/"
                            + screenshotUrlName
                            + "-"
                            + DOCS_ENOSIX_IO_HOST);
                }
            }
            if(!brokenUrls.isEmpty()) {
                fail(brokenUrls.stream().collect(Collectors.joining(System.lineSeparator())));
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
