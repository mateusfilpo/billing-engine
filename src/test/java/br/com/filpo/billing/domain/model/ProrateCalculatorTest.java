package br.com.filpo.billing.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProrateCalculatorTest {

    @Test
    @DisplayName("Deve calcular crédito de 50% quando a troca ocorre exatamente no meio do ciclo de 30 dias")
    void shouldCalculateExactlyHalfCredit() {
        BigDecimal oldPrice = new BigDecimal("100.00");
        LocalDate start = LocalDate.of(2026, 4, 1);
        LocalDate end = LocalDate.of(2026, 5, 1); // 30 dias de ciclo
        LocalDate changeDate = LocalDate.of(2026, 4, 16); // Faltam 15 dias

        BigDecimal credit = ProrateCalculator.calculateCredit(oldPrice, changeDate, start, end);

        assertEquals(new BigDecimal("50.00"), credit);
    }

    @Test
    @DisplayName("Deve retornar crédito zero se a data de troca for no final do período")
    void shouldReturnZeroCreditAtEndOfPeriod() {
        BigDecimal oldPrice = new BigDecimal("100.00");
        LocalDate start = LocalDate.of(2026, 4, 1);
        LocalDate end = LocalDate.of(2026, 5, 1);
        LocalDate changeDate = LocalDate.of(2026, 5, 1); // Trocou no último dia

        BigDecimal credit = ProrateCalculator.calculateCredit(oldPrice, changeDate, start, end);

        assertEquals(new BigDecimal("0.00"), credit);
    }

    @Test
    @DisplayName("Deve calcular ajuste corretamente para um upgrade")
    void shouldCalculateAdjustmentForUpgrade() {
        BigDecimal credit = new BigDecimal("50.00"); // Crédito do plano antigo
        BigDecimal debit = new BigDecimal("150.00"); // Débito proporcional do novo plano

        BigDecimal adjustment = ProrateCalculator.calculateAdjustment(credit, debit);

        assertEquals(new BigDecimal("100.00"), adjustment); // Fatura a pagar: R$ 100
    }
}