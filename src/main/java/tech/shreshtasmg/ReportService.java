package tech.shreshtasmg;

import io.quarkus.runtime.Startup;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.NotFoundException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Singleton
public class ReportService {

    @Inject
    JsonFileReader jsonFileReader;

    private List<ReportConfig> readReportConfig() {
        var reportGenConfigCL = ReportService.class.getClassLoader();
        List<ReportConfig> reportConfigs;
        try (InputStream reportConfigStream = reportGenConfigCL.getResourceAsStream("report-config.json")) {
            var reportConfigStr = new String(Objects.requireNonNull(reportConfigStream).readAllBytes());
            reportConfigs = jsonFileReader.readFile(reportConfigStr, ReportConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return reportConfigs;
    }

    public ReportConfig findReportConfigByResourceKey(String resourceKey) {
        var foundResourceKey = Arrays.stream(ResourceKey.values()).filter(y -> y.name().equals(resourceKey)).findFirst().orElseThrow(NotFoundException::new);
        return readReportConfig().stream().filter(x -> foundResourceKey.equals(x.resourceKey())).findFirst().orElseThrow(NotFoundException::new);
    }

    private HttpRequest prepareHttpRequest() {
        return HttpRequest
                .newBuilder()
                .build();
    }

    public Map<String, Object> getFamilyAddressInfo(String familyId) {
        return Map.of("familyId", familyId);
    }

    public byte[] generatePdfReport(GenericReportRequest reportRequest, ReportType reportType) {
        var reportGenConfigCL = ReportService.class.getClassLoader();
        var reportConfig = findReportConfigByResourceKey(reportRequest.resourceKey());
        var reportDTO = (FamilyAddressDto) reportRequest;
        var dataSource = new JRBeanCollectionDataSource(List.of(reportDTO));
        try (InputStream templateStream = reportGenConfigCL.getResourceAsStream("templates/"+reportConfig.reportTemplatePath())) {
            var report = JasperCompileManager.compileReport(templateStream);
            var jasperPrint = JasperFillManager.fillReport(report, Map.of(), dataSource);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (IOException | JRException e) {
            throw new RuntimeException(e);
        }
    }


}
