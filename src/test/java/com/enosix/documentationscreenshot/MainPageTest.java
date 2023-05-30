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
    String urlsPaths = "src/test/resources/docs-enosix-io.txt";
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
    public void captureNewScreenshots() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(urlsPaths));
            String line;
            List<String> brokenUrls = new LinkedList<>();

            while((line = br.readLine()) != null) {
                var originalLine = line;
                line = line.replace("docs.enosix.io","salmon-beach-039a75d0f.3.azurestaticapps.net");
//                System.out.println(line);
                open(line);
                HttpURLConnection cn = (HttpURLConnection)new URL(webdriver().driver().getWebDriver().getCurrentUrl()).openConnection();
                cn.setRequestMethod("HEAD");
                // connection initiate
                cn.connect();
                //get response code
                int res = cn.getResponseCode();
                if(res == 404) {
//                    System.out.println(line);
                    brokenUrls.add(line);
                    String screenshot = screenshot("/downloads/screenshots/"
                            + line.substring("https://salmon-beach-039a75d0f.3.azurestaticapps.net/".length())
                            .replace(':', '_')
                            .replace('-', '_')
                            .replace('/', '-')
                            + "-salmon-beach-039a75d0f-3-azurestaticapps-net");
                    open(originalLine);
                    screenshot = screenshot("/downloads/screenshots/"
                            + line.substring("https://salmon-beach-039a75d0f.3.azurestaticapps.net/".length())
                            .replace(':', '_')
                            .replace('-', '_')
                            .replace('/', '-')
                            + "-docs-enosix-io");

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
