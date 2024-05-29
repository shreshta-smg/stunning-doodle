package tech.shreshtasmg;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/report")
public class ReportResource {

    @Inject
    ReportService reportService;

    @POST
    @Path("/generate")
    @Produces("application/pdf")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response generateSingleAddressLabel(GenericReportRequest reportRequest) {
        return Response
                .ok(reportService.generatePdfReport(reportRequest, ReportType.ADDRESS_LABEL), "application/pdf")
                .header("Content-Disposition", "inline; filename=\"Report.pdf\"")
                .build();
    }
}
