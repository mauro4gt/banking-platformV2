package com.example.accounts.api;

import com.example.accounts.api.dto.AccountStatementReport;
import com.example.accounts.application.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@RequestMapping("/reports")
public class ReportsController {

    private final ReportService reportService;

    public ReportsController(ReportService reportService) {
        this.reportService = reportService;
    }

        @GetMapping("/{clientId}")
        public Mono<ResponseEntity<AccountStatementReport>> getReport(
            @PathVariable("clientId") String clientId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return reportService.generate(clientId, startDate, endDate)
            .map(ResponseEntity::ok);
    }

}
