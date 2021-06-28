package portal;

import config.BrowserConfig;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import vbpo.st.collector.common.service.LogService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class BkavPortal {

    public List<File> downloadInvoiceFiles(String lookupAddress, String lookupCode, String savePath, LogService logService) {

        File file = new File(savePath);
        if (!file.exists()) {
            logService.logInfo("Khởi tạo thư mục lưu trữ: " + savePath);
            file.mkdirs();
        }
        Arrays.stream(Objects.requireNonNull(file.listFiles())).forEach(File::delete);

        try {
            logService.logInfo("Setup property for chrome driver");
            System.setProperty("webdriver.chrome.driver", copyChromeDriver());

            logService.logInfo("Setup options for chrome driver");
            WebDriver driver = new ChromeDriver(BrowserConfig.getChromeOptions(savePath));

            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

            logService.logInfo("Open the browser and access the invoice lookup link: " + lookupAddress);
            driver.get(lookupAddress);

            logService.logInfo("Fill invoice lookup code: " + lookupCode);
            driver.findElement(By.id("txtInvoiceCode")).sendKeys(lookupCode);

            logService.logInfo("Submit invoice lookup code: " + lookupCode);
            driver.findElement(By.id("btnSearch")).click();

            logService.logInfo("Invoice viewing frame is displayed");
            driver.switchTo().frame(driver.findElement(By.id("frameViewInvoice")));

            logService.logInfo("Click download invoice");
            driver.findElement(By.cssSelector("#form1 > div.divHeader > div.divHeader_Buttons > div:nth-child(3) > input")).click();

            logService.logInfo("Click download file XML");
            driver.findElement(By.id("LinkDownXML")).click();

            logService.logInfo("Click download invoice");
            driver.findElement(By.cssSelector("#form1 > div.divHeader > div.divHeader_Buttons > div:nth-child(3) > input")).click();

            logService.logInfo("Click download file PDF");
            driver.findElement(By.id("LinkDownPDF")).click();

            logService.logInfo("Wait download file");
            waitFileDownloadComplete(driver, savePath);

            logService.logInfo("Exit the browser");
            driver.quit();
        } catch (Exception e) {
            logService.logError(e.getMessage());
        }
        return getListFile(savePath, logService);
    }

    private String copyChromeDriver() throws IOException {
        String chromeDriveFilePath = "browser/chromedriver.exe";

        File destinationFile = new File(chromeDriveFilePath);
        if (destinationFile.exists()) {
            return destinationFile.getAbsolutePath();
        }

        URL chromeDriverSourceUrl = getClass().getClassLoader().getResource(chromeDriveFilePath);
        if (chromeDriverSourceUrl == null) {
            throw new IOException("Không tìm thấy file chrome driver.");
        }

        FileUtils.copyURLToFile(chromeDriverSourceUrl, destinationFile);

        return destinationFile.getAbsolutePath();
    }

    private void waitFileDownloadComplete(WebDriver driver, String savePath) {
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.of(30, ChronoUnit.SECONDS))
                .pollingEvery(Duration.of(3, ChronoUnit.SECONDS))
                .ignoring(NoSuchElementException.class);

        wait.until(driverWait -> {
            File[] files = new File(savePath).listFiles();
            if (files != null) {
                boolean isXmlFileDownloaded = Arrays.stream(files).anyMatch(file -> FilenameUtils.getExtension(file.getName()).toLowerCase(Locale.ROOT).contains("xml"));
                boolean isPdfFileDownloaded = Arrays.stream(files).anyMatch(file -> FilenameUtils.getExtension(file.getName()).toLowerCase(Locale.ROOT).contains("pdf"));
                return isXmlFileDownloaded && isPdfFileDownloaded;
            }
            return false;
        });
    }

    private List<File> getListFile(String savePath, LogService logService) {
        logService.logInfo("Get list file downloaded");

        File[] files = new File(savePath).listFiles();
        List<File> listFile = new ArrayList<>();
        if (files != null) {
            Arrays.stream(files).forEach(file -> listFile.add(file.getAbsoluteFile()));
        }

        return listFile;
    }
}
