package config;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.chrome.ChromeOptions;

import java.awt.*;
import java.io.File;
import java.util.HashMap;

public class BrowserConfig {

    public static ChromeOptions getChromeOptions(String savePath) {
        HashMap<String, Object> chromePreferences = new HashMap<String, Object>();
        ChromeOptions options = new ChromeOptions();

        // ChromePreferences 0 = default, 1 = allow, 2 = block
        chromePreferences.put("profile.managed_default_content_settings.popups", 0);
        chromePreferences.put("profile.managed_default_content_settings.notifications", 2);
        chromePreferences.put("profile.managed_default_content_settings.cookies", 0);
        chromePreferences.put("profile.managed_default_content_settings.images", 1);
        chromePreferences.put("profile.managed_default_content_settings.stylesheets", 0);
        chromePreferences.put("profile.managed_default_content_settings.javascript", 0);
        chromePreferences.put("profile.managed_default_content_settings.plugins", 2);
        chromePreferences.put("profile.managed_default_content_settings.geolocation", 2);
        chromePreferences.put("profile.managed_default_content_settings.media_stream", 2);

        chromePreferences.put("download.default_directory", System.getProperty("user.dir") + File.separator + savePath.replace("/", "\\"));
        chromePreferences.put("download.prompt_for_download", false);
        chromePreferences.put("safebrowsing.enabled", true);

        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        options.addArguments("--headless", "--disable-gpu", "--window-size=" + size.width + "," + size.height);
        options.addArguments("--start-maximized");

        options.addArguments("--safebrowsing-disable-download-protection");
        options.addArguments("--safebrowsing-disable-extension-blacklist");
        options.addArguments("--enable-automation");
        options.addArguments("--no-sandbox");
        options.addArguments("--dns-prefetch-disable");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--incognito");
        options.addArguments("--enable-precise-memory-info");
        options.addArguments("--disable-default-apps");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-infobars");

        options.setExperimentalOption("prefs", chromePreferences);
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

        return options;
    }
}
