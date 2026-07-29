package com.dbtraining.reconx.model;

import java.time.LocalDate;
import java.util.Comparator;

/**
 * ============================================================================
 * Sealed interface TradeType
 *
 * WHAT:    Sealed root of the trade hierarchy. Only the four named
 *          permitted classes can implement it. Any new asset class needs an
 *          explicit code change here — by design.
 * HOW:     `sealed ... permits ...` on Java 25.
 * WHY:     Without sealing, anyone could write their own `Trade` subclass and
 *          slip through the reconciliation engine's pattern-matching switch.
 *          Sealing turns the engine's switch into an exhaustive one — the
 *          compiler enforces that every case is handled.
 * OBSERVE: Removing `permits BondTrade` causes a compile error in
 *          ReconciliationEngine's switch expression.
 * HINT:    See Day 2 trainer guide, Workshop 2A, sealed hierarchy, for the
 *          design discussion.
 * ============================================================================
 *
 * Natural ordering sorts most-recent trade first; equals/hashCode are based
 * on {@code tradeRef} (the natural key). The comparator lives on this sealed
 * interface so every implementation shares one ordering rule — there is no
 * per-class {@code compareTo} override to forget to update when a new field
 * is added.
 */
public sealed interface TradeType
        extends Comparable<TradeType>
        permits EquityTrade, FXTrade, BondTrade, DerivativeTrade {

    /**
     * Returns the stable natural key that identifies this trade across the
     * system and drives {@code equals}/{@code hashCode} for every
     * implementing type.
     *
     * @return the trade's unique reference; never {@code null}.
     */
    TradeRef tradeRef();

    /**
     * Returns the notional value of this trade, used when aggregating
     * reconciliation summaries across counterparties and instruments.
     *
     * @return the trade's notional amount and currency; never {@code null}.
     */
    Money notional();

    /**
     * Returns the business date this trade was struck on, independent of
     * settlement date.
     *
     * @return the trade date; never {@code null}.
     */
    LocalDate tradeDate();

    /**
     * Returns the discriminator used by exhaustive switch expressions and by
     * the persistence layer to map this trade to its concrete table.
     *
     * @return the asset class of this trade; never {@code null}.
     */
    AssetClass assetClass();

    /**
     * Shared ordering rule for all {@code TradeType} implementations: most
     * recent {@link #tradeDate()} first, with {@link #tradeRef()} as a
     * stable tie-breaker so equal-dated trades sort deterministically.
     */
    Comparator<TradeType> NATURAL = Comparator
            .comparing(TradeType::tradeDate).reversed()
            .thenComparing(t -> t.tradeRef().value());

    /**
     * Compares this trade to another using the shared {@link #NATURAL}
     * ordering — most-recent trade date first, then trade reference.
     *
     * @param other the trade to compare against.
     * @return a negative, zero, or positive value per {@link Comparator}
     *         semantics, following {@link #NATURAL}.
     */
    @Override
    default int compareTo(TradeType other) {
        return NATURAL.compare(this, other);
    }

    /**
     * WHAT: The closed set of asset classes a {@code TradeType} can belong
     * to, matching the four permitted implementations of this interface.
     * HOW:  Used as the discriminator in persistence mapping and as the
     * switch key in {@code ReconciliationEngine}.
     */
    enum AssetClass { EQUITY, FX, BOND, DERIVATIVE }
}