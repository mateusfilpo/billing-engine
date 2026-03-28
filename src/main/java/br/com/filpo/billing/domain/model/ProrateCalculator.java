package br.com.filpo.billing.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ProrateCalculator {

    private ProrateCalculator() {
    }

    public static BigDecimal calculateCredit(BigDecimal oldPlanPrice, LocalDate changeDate, LocalDate periodStart,
            LocalDate periodEnd) {
        return calculateProportionalValue(oldPlanPrice, changeDate, periodStart, periodEnd);
    }

    public static BigDecimal calculateDebit(BigDecimal newPlanPrice, LocalDate changeDate, LocalDate periodStart,
            LocalDate periodEnd) {
        return calculateProportionalValue(newPlanPrice, changeDate, periodStart, periodEnd);
    }

    public static BigDecimal calculateAdjustment(BigDecimal credit, BigDecimal debit) {
        return debit.subtract(credit);
    }

    private static BigDecimal calculateProportionalValue(BigDecimal price, LocalDate changeDate, LocalDate periodStart,
            LocalDate periodEnd) {
        long totalDays = ChronoUnit.DAYS.between(periodStart, periodEnd);
        long remainingDays = ChronoUnit.DAYS.between(changeDate, periodEnd);

        // Se a troca ocorreu no último dia ou após, ou se as datas estão incorretas,
        // não há valor proporcional
        if (totalDays <= 0 || remainingDays <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        // Calcula o valor da diária usando 4 casas decimais para não perder precisão no
        // meio do cálculo
        BigDecimal dailyRate = price.divide(BigDecimal.valueOf(totalDays), 4, RoundingMode.HALF_UP);

        // Multiplica pelos dias restantes e arredonda para o padrão de moeda (2 casas)
        return dailyRate.multiply(BigDecimal.valueOf(remainingDays)).setScale(2, RoundingMode.HALF_UP);
    }
}