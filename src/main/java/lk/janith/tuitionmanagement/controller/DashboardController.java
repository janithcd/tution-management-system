package lk.janith.tuitionmanagement.controller;

import lk.janith.tuitionmanagement.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {

        model.addAttribute("totalStudents", dashboardService.getTotalStudents());
        model.addAttribute("activeStudents", dashboardService.getActiveStudents());
        model.addAttribute("totalBatches", dashboardService.getTotalBatches());
        model.addAttribute("activeBatches", dashboardService.getActiveBatches());
        model.addAttribute("activeEnrollments", dashboardService.getActiveEnrollments());
        model.addAttribute("todayAttendanceCount", dashboardService.getTodayAttendanceCount());
        model.addAttribute("thisMonthIncome", dashboardService.getThisMonthIncome());
        model.addAttribute("pendingPayments", dashboardService.getThisMonthPendingPayments());

        Map<String, Long> paymentStatusData = dashboardService.getPaymentStatusChartData();
        model.addAttribute("paymentStatusLabels", new ArrayList<>(paymentStatusData.keySet()));
        model.addAttribute("paymentStatusValues", new ArrayList<>(paymentStatusData.values()));

        Map<String, BigDecimal> incomeByBatchData = dashboardService.getMonthlyIncomeByBatchChartData();
        model.addAttribute("incomeBatchLabels", new ArrayList<>(incomeByBatchData.keySet()));
        model.addAttribute("incomeBatchValues", new ArrayList<>(incomeByBatchData.values()));

        Map<String, Long> attendanceStatusData = dashboardService.getTodayAttendanceChartData();
        model.addAttribute("attendanceStatusLabels", new ArrayList<>(attendanceStatusData.keySet()));
        model.addAttribute("attendanceStatusValues", new ArrayList<>(attendanceStatusData.values()));

        return "dashboard";
    }
}