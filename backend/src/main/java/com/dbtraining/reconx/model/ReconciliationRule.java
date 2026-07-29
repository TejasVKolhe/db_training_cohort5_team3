package com.dbtraining.reconx.model;

import java.math.BigDecimal;

/**
 * ============================================================================
 * TICKET-ADV026 — ReconciliationRule enum with configurable thresholds
 *
 * WHAT:    Each enum value carries its own price tolerance (%) and quantity
 *          tolerance (absolute units). {@link #matches} returns true if the
 *          internal vs external trade pair is within tolerance.
 * HOW:     Enum-with-state pattern — instance fields + a behaviour method.
 * WHY:     Putting the rule on the enum keeps "what is a match" co-located
 *          with the rule's name, so the reconciliation engine is just:
 *          `if (rule.matches(internal, external)) ... matched ...`.
 * OBSERVE: PRICE_TOLERANCE_1PCT.matches(p, p*1.005) is true; *1.02 is false.
 * ============================================================================
 */
public enum ReconciliationRule {

    EXACT(BigDecimal.ZERO, BigDecimal.ZERO),
    PRICE_TOLERANCE_1PCT(new BigDecimal("0.01"), BigDecimal.ZERO),
    PRICE_TOLERANCE_50BPS(new BigDecimal("0.005"), BigDecimal.ZERO),
    QTY_TOLERANCE_5UNITS(BigDecimal.ZERO, new BigDecimal("5")),
    LOOSE(new BigDecimal("0.05"), new BigDecimal("10"));

    private final BigDecimal priceTolerancePct;
    private final BigDecimal qtyToleranceAbs;

    ReconciliationRule(BigDecimal priceTolerancePct, BigDecimal qtyToleranceAbs) {
        this.priceTolerancePct = priceTolerancePct;
        this.qtyToleranceAbs   = qtyToleranceAbs;
    }

    /**
 * Returns the maximum permitted price difference expressed as a percentage.
 *
 * @return price tolerance percentage for this reconciliation rule
 */
public BigDecimal priceTolerancePct() {
    return priceTolerancePct;
}

/**
 * Returns the maximum permitted absolute quantity difference.
 *
 * @return quantity tolerance in absolute units
 */
public BigDecimal qtyToleranceAbs() {
    return qtyToleranceAbs;
}

/**
 * Determines whether the supplied prices and quantities satisfy this
 * reconciliation rule.
 *
 * @param internalPrice price from the internal trade
 * @param internalQty quantity from the internal trade
 * @param externalPrice price from the external trade
 * @param externalQty quantity from the external trade
 * @return {@code true} if both the price difference and quantity difference
 *         are within this rule's configured tolerances; {@code false} otherwise
 */
public boolean matches(BigDecimal internalPrice, BigDecimal internalQty,
                       BigDecimal externalPrice, BigDecimal externalQty) {
    BigDecimal priceDiff = internalPrice.subtract(externalPrice).abs();
    BigDecimal priceDiffPct = internalPrice.signum() == 0
            ? BigDecimal.ZERO
            : priceDiff.divide(internalPrice, 6, java.math.RoundingMode.HALF_UP);
    BigDecimal qtyDiff = internalQty.subtract(externalQty).abs();

    boolean priceOk = priceDiffPct.compareTo(priceTolerancePct) <= 0;
    boolean qtyOk = qtyDiff.compareTo(qtyToleranceAbs) <= 0;
    return priceOk && qtyOk;
}
}