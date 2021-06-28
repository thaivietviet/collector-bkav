package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test {
    public static void main(String[] args) throws IOException {
        //        String invoiceDownloadInfoBkav = "{\"collectJobId\":2,\"lookupAddress\":\"https://van.ehoadon.vn/TCHD\",\"lookupCode\":\"S2MSHWEG3A7\",\"savedFolder\":\"download/bkav\"}";
//        String invoiceDownloadInfoBkav = "{\"environment\":null,\"collectJobId\":1,\"lookupAddress\":\"https://van.ehoadon.vn/TCHD\",\"lookupCode\":\"S2MSHWEG3A7\",\"sellerTaxCode\":null,\"buyerTaxCode\":null,\"templateCode\":null,\"invoiceSeries\":null,\"invoiceNumber\":null,\"savedFolder\":\"download/bkav\"}";
        String invoiceDownloadInfoBkav = "{\"debugging\":true,\"invoiceDownloadInfo\":{\"collectJobId\":1,\"lookupAddress\":\"https://van.ehoadon.vn/TCHD\",\"lookupCode\":\"S2MSHWEG3A7\",\"sellerTaxCode\":null,\"buyerTaxCode\":null,\"templateCode\":null,\"invoiceSeries\":null,\"invoiceNumber\":null,\"savedFolder\":\"download/bkav\"},\"collectorConfiguration\":{\"tokenUrl\":\"http://10.8.0.11:9999/oauth/token\",\"restEndPoint\":\"http://10.8.0.11:8082/api\",\"username\":\"super_admin\",\"password\":\"admin\",\"grantType\":\"password\",\"clientId\":\"web_app\",\"clientSecret\":\"changeit\"}}";
//        String invoiceDownloadInfoMisa = "{\"collectJobId\":2,\"lookupAddress\":null,\"lookupCode\":\"NPUKF29R4W7\",\"savedFolder\":\"download/misa\"}";
        String invoiceDownloadInfoMisa = "{\"invoiceDownloadInfo\":{\"collectJobId\":2,\"lookupAddress\":null,\"lookupCode\":\"NPUKF29R4W7\",\"savedFolder\":\"download/misa\"}}";

        String pathMisaFileJar = "data/misa-collector-1.0.0.jar";
        String pathBkavFileJar = "robots/bkav-collector-1.0.0.jar";

        ProcessBuilder pb = new ProcessBuilder("java", "-jar", pathBkavFileJar, invoiceDownloadInfoBkav.replace("\"", "\\\""));
        Process p = pb.start();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
//        bufferedReader.readLine();
        String result;
        while ((result = bufferedReader.readLine()) != null) {
            System.out.println(result);
        }
    }
}
