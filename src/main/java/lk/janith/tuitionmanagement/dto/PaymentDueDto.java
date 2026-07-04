package lk.janith.tuitionmanagement.dto;

import lk.janith.tuitionmanagement.entity.Enrollment;
import lk.janith.tuitionmanagement.entity.Payment;
import lk.janith.tuitionmanagement.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDueDto {

    private Enrollment enrollment;

    private Payment payment;

    private BigDecimal expectedAmount;

    private BigDecimal paidAmount;

    private BigDecimal balanceAmount;

    private PaymentStatus status;
}