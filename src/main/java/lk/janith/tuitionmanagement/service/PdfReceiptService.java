package lk.janith.tuitionmanagement.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lk.janith.tuitionmanagement.entity.Payment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PdfReceiptService {

    public byte[] generatePaymentReceipt(Payment payment) {

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);

            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);

            Paragraph title = new Paragraph("EduTrack Tuition Management System", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph receiptTitle = new Paragraph("Payment Receipt", subTitleFont);
            receiptTitle.setAlignment(Element.ALIGN_CENTER);
            receiptTitle.setSpacingAfter(20);
            document.add(receiptTitle);

            Paragraph receiptInfo = new Paragraph(
                    "Receipt No: PAY-" + String.format("%05d", payment.getId()) + "\n" +
                            "Generated At: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    normalFont
            );
            receiptInfo.setSpacingAfter(15);
            document.add(receiptInfo);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{35, 65});

            addRow(table, "Student Code", payment.getEnrollment().getStudent().getStudentCode(), boldFont, normalFont);
            addRow(table, "Student Name", payment.getEnrollment().getStudent().getFullName(), boldFont, normalFont);
            addRow(table, "Batch", payment.getEnrollment().getBatch().getBatchName(), boldFont, normalFont);
            addRow(table, "Subject", payment.getEnrollment().getBatch().getSubject(), boldFont, normalFont);
            addRow(table, "Payment Month", String.valueOf(payment.getPaymentMonth()), boldFont, normalFont);
            addRow(table, "Payment Year", String.valueOf(payment.getPaymentYear()), boldFont, normalFont);
            addRow(table, "Expected Amount", "Rs. " + payment.getExpectedAmount(), boldFont, normalFont);
            addRow(table, "Paid Amount", "Rs. " + payment.getPaidAmount(), boldFont, normalFont);
            addRow(table, "Balance Amount", "Rs. " + payment.getBalanceAmount(), boldFont, normalFont);
            addRow(table, "Payment Status", String.valueOf(payment.getStatus()), boldFont, normalFont);
            addRow(table, "Payment Method", String.valueOf(payment.getPaymentMethod()), boldFont, normalFont);
            addRow(table, "Payment Date", String.valueOf(payment.getPaymentDate()), boldFont, normalFont);

            if (payment.getRemarks() != null && !payment.getRemarks().isBlank()) {
                addRow(table, "Remarks", payment.getRemarks(), boldFont, normalFont);
            }

            document.add(table);

            Paragraph footer = new Paragraph(
                    "\nThank you for your payment.\nThis is a computer-generated receipt.",
                    normalFont
            );
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(25);
            document.add(footer);

            document.close();

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate payment receipt", e);
        }
    }

    private void addRow(
            PdfPTable table,
            String label,
            String value,
            Font labelFont,
            Font valueFont
    ) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setPadding(8);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setPadding(8);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }
}