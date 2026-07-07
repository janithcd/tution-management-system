package lk.janith.tuitionmanagement.controller;

import lk.janith.tuitionmanagement.enums.PaymentMethod;
import lk.janith.tuitionmanagement.service.EnrollmentService;
import lk.janith.tuitionmanagement.service.PaymentService;
import lk.janith.tuitionmanagement.service.PdfReceiptService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import lk.janith.tuitionmanagement.entity.Payment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    @GetMapping("/receipt/{id}")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable Long id) {

        Payment payment = paymentService.getPaymentById(id);

        byte[] pdfBytes = pdfReceiptService.generatePaymentReceipt(payment);

        String fileName = "payment-receipt-" + payment.getId() + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + fileName)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
    @GetMapping
    public String listPayments(Model model) {
        model.addAttribute("payments", paymentService.getAllPayments());
        return "payments/list";
    }

    @GetMapping("/new")
    public String showPaymentForm(Model model) {
        model.addAttribute("enrollments", enrollmentService.getActiveEnrollments());
        model.addAttribute("methods", PaymentMethod.values());
        model.addAttribute("currentMonth", LocalDate.now().getMonthValue());
        model.addAttribute("currentYear", LocalDate.now().getYear());
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
            Model model
    ) {
        if (paymentMonth == null) {
            paymentMonth = LocalDate.now().getMonthValue();
        }

        if (paymentYear == null) {
            paymentYear = LocalDate.now().getYear();
        }

        model.addAttribute("selectedMonth", paymentMonth);
        model.addAttribute("selectedYear", paymentYear);
        model.addAttribute("dues", paymentService.getPaymentDuesForMonth(paymentMonth, paymentYear));

        return "payments/dues";
    }
}