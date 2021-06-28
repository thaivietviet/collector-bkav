import com.fasterxml.jackson.databind.ObjectMapper;
import portal.BkavPortal;
import vbpo.st.collector.common.dto.*;
import vbpo.st.collector.common.enumration.ResultStatus;
import vbpo.st.collector.common.service.CollectorApiService;
import vbpo.st.collector.common.service.LogService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BkavCollector {
    public static void main(String[] args) {

        LogService logService = new LogService();

        logService.logInfo("Start Bkav Collector.");
        System.out.println("Start Bkav Collector.");
        RunCollectorCommand runCollectorCommand = null;

        try {
            runCollectorCommand = new ObjectMapper().readValue(args[0], RunCollectorCommand.class);
            logService.logInfo("Download info: " + runCollectorCommand.getInvoiceDownloadInfo());
        } catch (Exception e) {
            System.out.println("Error when parse input data: " + args[0]);
            System.exit(0);
        }

        InvoiceDownloadInfo invoiceDownloadInfo = runCollectorCommand.getInvoiceDownloadInfo();
        logService.logInfo("Start download invoice of collect job id: " + invoiceDownloadInfo.getCollectJobId());

        CollectorApiService collectorApiService = new CollectorApiService(runCollectorCommand.getCollectorConfiguration());
        BkavPortal bkavPortal = new BkavPortal();

        InvoiceDownloadResult invoiceDownloadResult = new InvoiceDownloadResult();
        List<File> downloadedFiles = new ArrayList<>();

        try {
            logService.logInfo("Run chrome driver");
            downloadedFiles = bkavPortal.downloadInvoiceFiles(invoiceDownloadInfo.getLookupAddress(), invoiceDownloadInfo.getLookupCode(), invoiceDownloadInfo.getSavedFolder(), logService);
            logService.logError("Number of downloaded invoice file: " + downloadedFiles.size());
        } catch (Exception e) {
            logService.logError(e.getMessage());
        }

        InvoiceDownloadResultInfo invoiceDownloadResultInfo = new InvoiceDownloadResultInfo();
        invoiceDownloadResultInfo.setLogs(logService.getLogs());
        invoiceDownloadResultInfo.setSavedFolder(invoiceDownloadInfo.getSavedFolder());
        invoiceDownloadResultInfo.setCollectJobId(invoiceDownloadInfo.getCollectJobId());

        invoiceDownloadResult.setFiles(downloadedFiles);
        invoiceDownloadResult.setInvoiceDownloadResultInfo(invoiceDownloadResultInfo);

        if (downloadedFiles.size() != 2) {
            invoiceDownloadResult.getInvoiceDownloadResultInfo().setResultStatus(ResultStatus.ERROR);
        } else {
            invoiceDownloadResult.getInvoiceDownloadResultInfo().setResultStatus(ResultStatus.SUCCESS);
        }

        collectorApiService.completeDownload(invoiceDownloadResult);

        if (runCollectorCommand.getDebugging() != null && runCollectorCommand.getDebugging()) {
            for (InvoiceDownloadLog invoiceDownloadLog : logService.getLogs()) {
                System.out.println("CollectJobId: " + invoiceDownloadInfo.getCollectJobId() + " - " + invoiceDownloadLog.getMessage());
            }
        }
    }
}
