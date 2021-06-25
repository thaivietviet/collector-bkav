import com.fasterxml.jackson.databind.ObjectMapper;
import portal.BkavPortal;
import vbpo.st.collector.common.config.CollectorConfiguration;
import vbpo.st.collector.common.dto.InvoiceDownloadInfo;
import vbpo.st.collector.common.dto.InvoiceDownloadResult;
import vbpo.st.collector.common.enumration.ResultStatus;
import vbpo.st.collector.common.service.CollectorApiService;
import vbpo.st.collector.common.service.LogService;

import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        System.out.println("Bat dau chay file jar ");

        CollectorApiService collectorApiService = new CollectorApiService();

        InvoiceDownloadInfo invoiceDownloadInfo = new InvoiceDownloadInfo();
        
        LogService logService = new LogService();

        logService.logInfo("Start download Invoice");

        BkavPortal bkavPortal = new BkavPortal();

        try {
            invoiceDownloadInfo = new ObjectMapper().readValue(args[0], InvoiceDownloadInfo.class);
            System.out.println(invoiceDownloadInfo);
        } catch (Exception e) {
            System.out.println("error: convert String to Object");
            System.exit(0);
        }

        InvoiceDownloadResult invoiceDownloadResult = new InvoiceDownloadResult();

        List<File> listFile = null;
        
        try {
            collectorApiService.initCollectorConfiguration(invoiceDownloadInfo.getEnvironment());
            System.out.println(CollectorConfiguration.getSettingData());

            logService.logInfo("Run chrome driver");
            listFile = bkavPortal.downloadInvoiceFiles(invoiceDownloadInfo.getLookupAddress(), invoiceDownloadInfo.getLookupCode(), invoiceDownloadInfo.getSavedFolder(), logService);
            System.out.println(logService.getLogs());
        } catch (Exception e) {
            logService.logError(e.getMessage());
            System.out.println(logService.getLogs());
        }
        
        invoiceDownloadResult.setFiles(listFile);
        invoiceDownloadResult.getInvoiceDownloadResultInfo().setLogs(logService.getLogs());
        invoiceDownloadResult.getInvoiceDownloadResultInfo().setSavedFolder(invoiceDownloadInfo.getSavedFolder());
        invoiceDownloadResult.getInvoiceDownloadResultInfo().setCollectJobId(invoiceDownloadInfo.getCollectJobId());

        if (listFile != null && listFile.size() == 2) {
            invoiceDownloadResult.getInvoiceDownloadResultInfo().setResultStatus(ResultStatus.SUCCESS);
        } else {
            invoiceDownloadResult.getInvoiceDownloadResultInfo().setResultStatus(ResultStatus.ERROR);
        }

        try {
            collectorApiService.completeDownload(invoiceDownloadResult);
        } catch (Exception ignored) {

        }
    }
}
