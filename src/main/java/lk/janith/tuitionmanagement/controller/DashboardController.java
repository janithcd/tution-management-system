package lk.janith.tuitionmanagement.controller;

import lk.janith.tuitionmanagement.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

        return "dashboard";
    }
}