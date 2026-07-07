package lk.janith.tuitionmanagement.controller;

import jakarta.servlet.http.HttpServletResponse;
import lk.janith.tuitionmanagement.dto.PaymentDueDto;
import lk.janith.tuitionmanagement.entity.Payment;
import lk.janith.tuitionmanagement.enums.PaymentMethod;
import lk.janith.tuitionmanagement.enums.PaymentStatus;
import lk.janith.tuitionmanagement.service.EnrollmentService;
import lk.janith.tuitionmanagement.service.PaymentService;
import lk.janith.tuitionmanagement.service.PdfReceiptService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final EnrollmentService enrollmentService;
    private final PdfReceiptService pdfReceiptService;

    public PaymentController(
            PaymentService paymentService,
            EnrollmentService enrollmentService,
            PdfReceiptService pdfReceiptService
    ) {
        this.paymentService = paymentService;
        this.enrollmentService = enrollmentService;
        this.pdfReceiptService = pdfReceiptService;
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
        PaymentStatus selectedStatus = null;

        if (status != null && !status.isBlank()) {
            selectedStatus = PaymentStatus.valueOf(status);
        }

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
        model.addAttribute("enrollments", enrollmentService.getActiveEnrollments());


        model.addAttribute("methods", PaymentMethod.values());
        model.addAttribute("paymentMethods", PaymentMethod.values());

        model.addAttribute("currentMonth", LocalDate.now().getMonthValue());
        model.addAttribute("currentYear", LocalDate.now().getYear());
        model.addAttribute("today", LocalDate.now());

        return "payments/form";
    }


    @PostMapping("/save")
    public String savePayment(
            @RequestParam Long enrollmentId,
            @RequestParam Integer paymentMonth,
            @RequestParam Integer paymentYear,
            @RequestParam BigDecimal paidAmount,
            @RequestParam PaymentMethod paymentMethod,
            @RequestParam(required = false) String remarks
    ) {
        paymentService.recordPayment(
                enrollmentId,
                paymentMonth,
                paymentYear,
                paidAmount,
                paymentMethod,
                remarks
        );

        return "redirect:/payments";
    }


    @GetMapping("/dues")
    public String showPaymentDues(
            @RequestParam(required = false) Integer paymentMonth,
            @RequestParam(required = false) Integer paymentYear,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            Model model
    ) {
        Integer selectedMonth = paymentMonth != null ? paymentMonth : month;
        Integer selectedYear = paymentYear != null ? paymentYear : year;

        if (selectedMonth == null) {
            selectedMonth = LocalDate.now().getMonthValue();
        }

        if (selectedYear == null) {
            selectedYear = LocalDate.now().getYear();
        }

        List<PaymentDueDto> dues = paymentService.getPaymentDuesForMonth(
                selectedMonth,
                selectedYear
        );

        model.addAttribute("selectedMonth", selectedMonth);
        model.addAttribute("selectedYear", selectedYear);
        model.addAttribute("dues", dues);

        return "payments/dues";
    }


    @GetMapping({"/receipt/{id}", "/{id}/receipt"})
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable Long id) {
        Payment payment = paymentService.getPaymentById(id);

        byte[] pdfBytes = pdfReceiptService.generatePaymentReceipt(payment);

        String fileName = "payment-receipt-" + payment.getId() + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + fileName)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }


    @GetMapping("/export")
    public void exportPaymentsToCsv(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer paymentMonth,
            @RequestParam(required = false) Integer paymentYear,
            @RequestParam(required = false) String status,
            HttpServletResponse response
    ) throws IOException {

        PaymentStatus selectedStatus = null;

        if (status != null && !status.isBlank()) {
            selectedStatus = PaymentStatus.valueOf(status);
        }

        List<Payment> payments = paymentService.searchPaymentsForExport(
                keyword,
                paymentMonth,
                paymentYear,
                selectedStatus
        );

        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=payments-report.csv");

        PrintWriter writer = response.getWriter();

        writer.println("ID,Student Code,Student Name,Batch,Subject,Month,Year,Expected Amount,Paid Amount,Balance,Status,Method,Payment Date,Remarks");

        for (Payment payment : payments) {
            writer.println(
                    csv(payment.getId()) + "," +
                            csv(payment.getEnrollment().getStudent().getStudentCode()) + "," +
                            csv(payment.getEnrollment().getStudent().getFullName()) + "," +
                            csv(payment.getEnrollment().getBatch().getBatchName()) + "," +
                            csv(payment.getEnrollment().getBatch().getSubject()) + "," +
                            csv(payment.getPaymentMonth()) + "," +
                            csv(payment.getPaymentYear()) + "," +
                            csv(payment.getExpectedAmount()) + "," +
                            csv(payment.getPaidAmount()) + "," +
                            csv(payment.getBalanceAmount()) + "," +
                            csv(payment.getStatus()) + "," +
                            csv(payment.getPaymentMethod()) + "," +
                            csv(payment.getPaymentDate()) + "," +
                            csv(payment.getRemarks())
            );
        }

        writer.flush();
    }

    private String csv(Object value) {
        if (value == null) {
            return "";
        }

        String text = value.toString();
        text = text.replace("\"", "\"\"");

        return "\"" + text + "\"";
    }
}