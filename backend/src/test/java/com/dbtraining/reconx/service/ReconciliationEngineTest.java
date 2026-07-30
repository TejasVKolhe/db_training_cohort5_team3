package com.dbtraining.reconx.service;

import com.dbtraining.reconx.dto.ReconResult;
import com.dbtraining.reconx.model.*;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TICKET-ADV040 / ADV041 / ADV042 — TDD: write the test FIRST, then the implementation.
 */
class ReconciliationEngineTest {

    private final ReconciliationEngine engine = new ReconciliationEngine();

    // single internal trade with no external feed -> one BREAK with MISSING_EXTERNAL
@Test
void testReconcile_singleInternalNoExternal_returnsBreak() {
    EquityTrade internal = equity("EQU-20260603-0001", "100.00", "1000");

    List<ReconResult> out = engine.reconcile(List.of(internal), List.of(), ReconciliationRule.EXACT);

    assertThat(out).hasSize(1);
    assertThat(out.get(0).status()).isEqualTo(ReconResult.Status.BREAK);
    assertThat(out.get(0).discrepancyType()).isEqualTo("MISSING_EXTERNAL");
}

// all-mismatched -> ReconSummaryCollector reports total == broken, matched == 0
@Test
void testReconcile_allMismatched_summaryShowsZeroMatched() {
    List<TradeType> internals = List.of(
            equity("EQU-20260603-0001", "100.00", "1000"),
            equity("EQU-20260603-0002", "100.00", "1000"),
            equity("EQU-20260603-0003", "100.00", "1000"));
    List<TradeType> externals = List.of(
            equity("EQU-20260603-0001", "200.00", "1000"),
            equity("EQU-20260603-0002", "200.00", "1000"),
            equity("EQU-20260603-0003", "200.00", "1000"));

    List<ReconResult> out = engine.reconcile(internals, externals, ReconciliationRule.EXACT);
    ReconSummary summary = out.stream().collect(new ReconSummaryCollector());

    assertThat(summary.total()).isEqualTo(3);
    assertThat(summary.matched()).isEqualTo(0);
    assertThat(summary.broken()).isEqualTo(3);
}


    @DisplayName("Exact matching trades should return MATCHED")
@Test
void testReconcile_exactMatch_returnsMatched() {

    // given
    TradeType internal = equity("ABC-20260603-0001", "100.00", "10");
    TradeType external = equity("ABC-20260603-0001", "100.00", "10");

    // when
    List<ReconResult> out = engine.reconcile(
            List.of(internal),
            List.of(external),
            ReconciliationRule.EXACT);

    // then
    assertThat(out).hasSize(1);
    assertThat(out.get(0).status())
            .isEqualTo(ReconResult.Status.MATCHED);
}

    @DisplayName("Price differences within 1% should match")
@ParameterizedTest(name = "price diff {0} stays within 1% tolerance -> MATCHED")
@ValueSource(strings = {"0.10", "0.50", "0.99"})
void testReconcile_priceTolerance_withinThreshold(String diff) {

    // given
    BigDecimal externalPrice =
            new BigDecimal("100.00").add(new BigDecimal(diff));

    TradeType internal = equity("ABC-20260603-0001", "100.00", "10");
    TradeType external = equity(
            "ABC-20260603-0001",
            externalPrice.toPlainString(),
            "10");

    // when
    List<ReconResult> out = engine.reconcile(
            List.of(internal),
            List.of(external),
            ReconciliationRule.PRICE_TOLERANCE_1PCT);

    // then
    assertThat(out).hasSize(1);
    assertThat(out.get(0).status())
            .isEqualTo(ReconResult.Status.MATCHED);
}


    @DisplayName("Missing external trade should return BREAK")
@Test
void testReconcile_missingCounterpartyTrade_returnsBreak() {

    // given
    TradeType internal = equity("ABC-20260603-0001", "100.00", "10");

    // when
    List<ReconResult> out = engine.reconcile(
            List.of(internal),
            List.of(),
            ReconciliationRule.EXACT);

    // then
    assertThat(out).hasSize(1);

    assertThat(out.get(0).status())
            .isEqualTo(ReconResult.Status.BREAK);

    assertThat(out.get(0).discrepancyType())
            .isEqualTo("MISSING_EXTERNAL");
}

    @DisplayName("Empty inputs should return empty output")
@Test
void testReconcile_emptyInternal_returnsEmpty() {

    // given

    // when
    List<ReconResult> out = engine.reconcile(
            List.of(),
            List.of(),
            ReconciliationRule.EXACT);

    // then
    assertThat(out).isEmpty();
}

    private EquityTrade equity(String ref, String price, String qty) {
        return EquityTrade.builder()
                .tradeRef(TradeRef.of(ref))
                .instrumentSymbol("SAP.DE")
                .price(new BigDecimal(price))
                .quantity(new BigDecimal(qty))
                .currency("EUR").side(Side.BUY)
                .tradeDate(LocalDate.of(2026, 6, 3))
                .counterpartyId(1L)
                .build();
    }
}
