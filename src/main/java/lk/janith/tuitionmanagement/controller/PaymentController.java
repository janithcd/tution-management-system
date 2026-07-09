package lk.janith.tuitionmanagement.controller;

import jakarta.servlet.http.HttpServletResponse;
import lk.janith.tuitionmanagement.entity.Payment;
import lk.janith.tuitionmanagement.enums.PaymentMethod;
import lk.janith.tuitionmanagement.enums.PaymentStatus;
import lk.janith.tuitionmanagement.service.EnrollmentService;
import lk.janith.tuitionmanagement.service.PaymentReceiptPdfService;
import lk.janith.tuitionmanagement.service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final EnrollmentService enrollmentService;
    private final PaymentReceiptPdfService paymentReceiptPdfService;

    public PaymentController(
            PaymentService paymentService,
            EnrollmentService enrollmentService,
            PaymentReceiptPdfService paymentReceiptPdfService
    ) {
        this.paymentService = paymentService;
        this.enrollmentService = enrollmentService;
        this.paymentReceiptPdfService = paymentReceiptPdfService;
    }

    @GetMapping
    public String listPayments(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer paymentMonth,
            @RequestParam(required = false) Integer paymentYear,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        PaymentStatus selectedStatus = parsePaymentStatus(status);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Page<Payment> paymentPage = paymentService.searchPayments(
                keyword,
                paymentMonth,
                paymentYear,
                selectedStatus,
                pageable
        );

        int totalPages = paymentPage.getTotalPages();

        int startPage = Math.max(0, page - 2);
        int endPage = Math.min(totalPages - 1, page + 2);

        if (totalPages > 0 && endPage - startPage < 4) {
            if (startPage == 0) {
                endPage = Math.min(totalPages - 1, startPage + 4);
            } else if (endPage == totalPages - 1) {
                startPage = Math.max(0, endPage - 4);
            }
        }

        model.addAttribute("payments", paymentPage.getContent());
        model.addAttribute("paymentPage", paymentPage);

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedPaymentMonth", paymentMonth);
        model.addAttribute("selectedPaymentYear", paymentYear);
        model.addAttribute("selectedStatus", status);


        model.addAttribute("statuses", PaymentStatus.values());
        model.addAttribute("paymentStatuses", PaymentStatus.values());


        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", paymentPage.getTotalElements());

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "payments/list";
    }

    @GetMapping("/new")
    public String showPaymentForm(Model model) {
        LocalDate today = LocalDate.now();

        model.addAttribute("enrollments", enrollmentService.getActiveEnrollments());

        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("methods", PaymentMethod.values());

        model.addAttribute("currentMonth", today.getMonthValue());
        model.addAttribute("currentYear", today.getYear());

        if (!model.containsAttribute("selectedPaymentMonth")) {
            model.addAttribute("selectedPaymentMonth", today.getMonthValue());
        }

        if (!model.containsAttribute("selectedPaymentYear")) {
            model.addAttribute("selectedPaymentYear", today.getYear());
        }

        return "payments/form";
    }

    @PostMapping("/save")
    public String savePayment(
            @RequestParam(required = false) Long enrollmentId,
            @RequestParam(required = false) Integer paymentMonth,
            @RequestParam(required = false) Integer paymentYear,
            @RequestParam(required = false) BigDecimal paidAmount,
            @RequestParam(required = false) PaymentMethod paymentMethod,
            @RequestParam(required = false) String remarks,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Payment payment = paymentService.recordPayment(
                    enrollmentId,
                    paymentMonth,
                    paymentYear,
                    paidAmount,
                    paymentMethod,
                    remarks
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Payment recorded successfully. Receipt No: REC-" + String.format("%05d", payment.getId())
            );

            return "redirect:/payments";

        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

            keepPaymentFormValues(
                    redirectAttributes,
                    enrollmentId,
                    paymentMonth,
                    paymentYear,
                    paidAmount,
                    paymentMethod,
                    remarks
            );

            return "redirect:/payments/new";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Payment could not be saved. Please check the selected enrollment and payment details."
            );

            keepPaymentFormValues(
                    redirectAttributes,
                    enrollmentId,
                    paymentMonth,
                    paymentYear,
                    paidAmount,
                    paymentMethod,
                    remarks
            );

            return "redirect:/payments/new";
        }
    }

    @GetMapping("/dues")
    public String paymentDues(
            @RequestParam(required = false) Integer paymentMonth,
            @RequestParam(required = false) Integer paymentYear,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            Model model
    ) {
        LocalDate today = LocalDate.now();

        int selectedMonth = paymentMonth != null
                ? paymentMonth
                : month != null ? month : today.getMonthValue();

        int selectedYear = paymentYear != null
                ? paymentYear
                : year != null ? year : today.getYear();

        var dues = paymentService.getPaymentDuesForMonth(selectedMonth, selectedYear);

        model.addAttribute("dues", dues);

        model.addAttribute("paymentMonth", selectedMonth);
        model.addAttribute("paymentYear", selectedYear);

        model.addAttribute("selectedMonth", selectedMonth);
        model.addAttribute("selectedYear", selectedYear);

        model.addAttribute("selectedPaymentMonth", selectedMonth);
        model.addAttribute("selectedPaymentYear", selectedYear);

        model.addAttribute("currentYear", today.getYear());

        return "payments/dues";
    }

    @GetMapping("/receipt/{id}")
    public void downloadReceipt(
            @PathVariable Long id,
            HttpServletResponse response
    ) throws IOException {
        Payment payment = paymentService.getPaymentById(id);

        response.setContentType("application/pdf");
        response.setHeader(
                "Content-Disposition",
                "inline; filename=payment-receipt-" + payment.getId() + ".pdf"
        );

        paymentReceiptPdfService.generateReceipt(payment, response.getOutputStream());
    }

    @GetMapping("/{id}/receipt")
    public void downloadReceiptAlternative(
            @PathVariable Long id,
            HttpServletResponse response
    ) throws IOException {
        downloadReceipt(id, response);
    }

    @GetMapping("/export")
    public void exportPayments(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer paymentMonth,
            @RequestParam(required = false) Integer paymentYear,
            @RequestParam(required = false) String status,
            HttpServletResponse response
    ) throws IOException {
        PaymentStatus selectedStatus = parsePaymentStatus(status);

        List<Payment> payments = paymentService.searchPaymentsForExport(
                keyword,
                paymentMonth,
                paymentYear,
                selectedStatus
        );

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=payments.csv");

        StringBuilder csv = new StringBuilder();

        csv.append("Receipt No,Student Code,Student Name,Batch,Subject,Month,Year,Expected Amount,Paid Amount,Balance Amount,Status,Method,Payment Date,Remarks\n");

        for (Payment payment : payments) {
            csv.append(csv("REC-" + String.format("%05d", payment.getId()))).append(",");
            csv.append(csv(payment.getEnrollment().getStudent().getStudentCode())).append(",");
            csv.append(csv(payment.getEnrollment().getStudent().getFullName())).append(",");
            csv.append(csv(payment.getEnrollment().getBatch().getBatchName())).append(",");
            csv.append(csv(payment.getEnrollment().getBatch().getSubject())).append(",");
            csv.append(csv(String.valueOf(payment.getPaymentMonth()))).append(",");
            csv.append(csv(String.valueOf(payment.getPaymentYear()))).append(",");
            csv.append(csv(String.valueOf(payment.getExpectedAmount()))).append(",");
            csv.append(csv(String.valueOf(payment.getPaidAmount()))).append(",");
            csv.append(csv(String.valueOf(payment.getBalanceAmount()))).append(",");
            csv.append(csv(String.valueOf(payment.getStatus()))).append(",");
            csv.append(csv(String.valueOf(payment.getPaymentMethod()))).append(",");
            csv.append(csv(String.valueOf(payment.getPaymentDate()))).append(",");
            csv.append(csv(payment.getRemarks())).append("\n");
        }

        response.getWriter().write(csv.toString());
    }

    private void keepPaymentFormValues(
            RedirectAttributes redirectAttributes,
            Long enrollmentId,
            Integer paymentMonth,
            Integer paymentYear,
            BigDecimal paidAmount,
            PaymentMethod paymentMethod,
            String remarks
    ) {
        redirectAttributes.addFlashAttribute("selectedEnrollmentId", enrollmentId);
        redirectAttributes.addFlashAttribute("selectedPaymentMonth", paymentMonth);
        redirectAttributes.addFlashAttribute("selectedPaymentYear", paymentYear);
        redirectAttributes.addFlashAttribute("selectedPaidAmount", paidAmount);
        redirectAttributes.addFlashAttribute("selectedPaymentMethod", paymentMethod);
        redirectAttributes.addFlashAttribute("selectedRemarks", remarks);
    }

    private PaymentStatus parsePaymentStatus(String value) {
        try {
            return value == null || value.isBlank() ? null : PaymentStatus.valueOf(value);
        } catch (Exception e) {
            return null;
        }
    }

    private String csv(String value) {
        if (value == null) {
            return "";
        }

        String escapedValue = value.replace("\"", "\"\"");
        return "\"" + escapedValue + "\"";
    }
}