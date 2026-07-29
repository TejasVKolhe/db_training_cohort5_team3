package com.dbtraining.reconx.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Objects;

/**
 * ============================================================================
 * TICKET-ADV020 — FXTrade with Builder pattern
 *
 * WHAT:    FX spot/forward trade — two currencies, a notional in ccy1, and
 *          an fxRate.
 * HOW:     Same builder pattern as EquityTrade. notional() converts to ccy2
 *          via fxRate so reconciliation rolls up in the trade's quote ccy.
 * WHY:     FX has two natural sides — a EUR/USD trade is BOTH a buy of EUR
 *          AND a sell of USD. Modelling that with two distinct currency
 *          fields makes settlement-side reasoning explicit.
 * OBSERVE: notional().currency() == ccy2; .amount() == notionalCcy1 * fxRate.
 * ============================================================================
 *
 * Equality and hashing are derived from {@code tradeRef} alone; {@code
 * toString} omits counterparty identity to avoid leaking PII into logs.
 */
public final class FXTrade implements TradeType {

    private final TradeRef tradeRef;
    private final Currency ccy1;
    private final Currency ccy2;
    private final BigDecimal notionalCcy1;
    private final BigDecimal fxRate;
    private final Side side;
    private final LocalDate tradeDate;
    private final long counterpartyId;

    private FXTrade(Builder b) {
        this.tradeRef       = b.tradeRef;
        this.ccy1           = b.ccy1;
        this.ccy2           = b.ccy2;
        this.notionalCcy1   = b.notionalCcy1;
        this.fxRate         = b.fxRate;
        this.side           = b.side;
        this.tradeDate      = b.tradeDate;
        this.counterpartyId = b.counterpartyId;
    }

    /**
     * Creates a new, empty {@link Builder} for assembling an {@code FXTrade}
     * field by field.
     *
     * @return a fresh builder with no fields set; never {@code null}.
     */
    public static Builder builder() { return new Builder(); }

    /**
     * {@inheritDoc}
     *
     * @return this trade's unique reference, the natural key used for
     *         {@link #equals(Object)} and {@link #hashCode()}.
     */
    @Override public TradeRef tradeRef()     { return tradeRef; }

    /**
     * {@inheritDoc}
     *
     * @return the business date this FX trade was struck on.
     */
    @Override public LocalDate tradeDate()   { return tradeDate; }

    /**
     * {@inheritDoc}
     *
     * @return always {@link AssetClass#FX} for this implementation.
     */
    @Override public AssetClass assetClass() { return AssetClass.FX; }

    /**
     * Converts the ccy1-denominated notional into ccy2 using {@code fxRate},
     * so reconciliation summaries roll up in the trade's quote currency.
     *
     * @return {@code notionalCcy1 * fxRate}, denominated in {@link #ccy2()};
     *         never {@code null}.
     */
    @Override public Money notional() {
        return new Money(notionalCcy1.multiply(fxRate), ccy2);
    }

    /**
     * Returns the base currency of the pair — the currency the notional is
     * expressed in before conversion.
     *
     * @return the base (dealt) currency; never {@code null}.
     */
    public Currency ccy1()           { return ccy1; }

    /**
     * Returns the quote currency of the pair — the currency the notional
     * converts into via {@link #fxRate()}.
     *
     * @return the quote currency; never {@code null}, and always different
     *         from {@link #ccy1()}.
     */
    public Currency ccy2()           { return ccy2; }

    /**
     * Returns the notional amount expressed in {@link #ccy1()}, before
     * conversion.
     *
     * @return a strictly positive notional in the base currency.
     */
    public BigDecimal notionalCcy1() { return notionalCcy1; }

    /**
     * Returns the exchange rate used to convert {@link #notionalCcy1()}
     * into {@link #ccy2()}.
     *
     * @return a strictly positive conversion rate.
     */
    public BigDecimal fxRate()       { return fxRate; }

    /**
     * Returns whether this trade is a buy or a sell of the base currency.
     *
     * @return the trade side; never {@code null}.
     */
    public Side side()               { return side; }

    /**
     * Returns the internal identifier of the counterparty on the other side
     * of this trade.
     *
     * @return the counterparty's database identifier.
     */
    public long counterpartyId()     { return counterpartyId; }

    /**
     * Compares by {@code tradeRef} only — two {@code FXTrade} instances are
     * equal iff they share the same natural key, regardless of any other
     * field differing.
     *
     * @param o the object to compare against.
     * @return {@code true} if {@code o} is an {@code FXTrade} with an equal
     *         {@code tradeRef}.
     */
    @Override public boolean equals(Object o) {
        return (o instanceof FXTrade other) && tradeRef.equals(other.tradeRef);
    }

    /**
     * Consistent with {@link #equals(Object)}: derived solely from
     * {@code tradeRef}.
     *
     * @return the hash code of this trade's {@code tradeRef}.
     */
    @Override public int hashCode() {
        return tradeRef.hashCode();
    }

    /**
     * Renders a compact, human-readable summary for logs, deliberately
     * excluding {@code counterpartyId} to avoid printing identifying
     * relationship data.
     *
     * @return a string of the form
     *         {@code "FXTrade[ref=..., CCY1/CCY2, notional=... CCY1, rate=..., side=...]"}.
     */
    @Override public String toString() {
        return "FXTrade[ref=%s, %s/%s, notional=%s %s, rate=%s, side=%s]"
                .formatted(tradeRef, ccy1.getCurrencyCode(), ccy2.getCurrencyCode(),
                        notionalCcy1, ccy1.getCurrencyCode(), fxRate, side);
    }

    /**
     * WHAT: Fluent builder for {@link FXTrade}.
     * HOW:  Each setter returns {@code this}; {@link #build()} is the single
     * chokepoint that validates every required field and invariant.
     * WHY:  Keeps {@code FXTrade} immutable while avoiding an
     * eight-argument constructor at the call site.
     */
    public static final class Builder {
        private TradeRef tradeRef;
        private Currency ccy1, ccy2;
        private BigDecimal notionalCcy1, fxRate;
        private Side side;
        private LocalDate tradeDate;
        private long counterpartyId;

        public Builder tradeRef(TradeRef v)        { this.tradeRef = v; return this; }
        public Builder ccy1(String code)           { this.ccy1 = Currency.getInstance(code); return this; }
        public Builder ccy2(String code)           { this.ccy2 = Currency.getInstance(code); return this; }
        public Builder notionalCcy1(BigDecimal v)  { this.notionalCcy1 = v; return this; }
        public Builder fxRate(BigDecimal v)        { this.fxRate = v; return this; }
        public Builder side(Side v)                { this.side = v; return this; }
        public Builder tradeDate(LocalDate v)      { this.tradeDate = v; return this; }
        public Builder counterpartyId(long v)      { this.counterpartyId = v; return this; }

        /**
         * Build the immutable {@link FXTrade}, validating that every required
         * field is set and that all invariants hold.
         *
         * @return a fully-constructed, validated {@code FXTrade} — never
         *         {@code null}.
         * @throws NullPointerException  if any required field
         *                               ({@code tradeRef}, {@code ccy1},
         *                               {@code ccy2}, {@code notionalCcy1},
         *                               {@code fxRate}, {@code side}, or
         *                               {@code tradeDate}) was not set.
         * @throws IllegalStateException if {@code ccy1} equals {@code ccy2},
         *                               or {@code fxRate} is not strictly
         *                               positive.
         */
        public FXTrade build() {
            Objects.requireNonNull(tradeRef, "tradeRef");
            Objects.requireNonNull(ccy1, "ccy1");
            Objects.requireNonNull(ccy2, "ccy2");
            Objects.requireNonNull(notionalCcy1, "notionalCcy1");
            Objects.requireNonNull(fxRate, "fxRate");
            Objects.requireNonNull(side, "side");
            Objects.requireNonNull(tradeDate, "tradeDate");
            if (ccy1.equals(ccy2))
                throw new IllegalStateException("ccy1 must differ from ccy2");
            if (fxRate.signum() <= 0)
                throw new IllegalStateException("fxRate must be > 0");
            return new FXTrade(this);
        }
    }
}