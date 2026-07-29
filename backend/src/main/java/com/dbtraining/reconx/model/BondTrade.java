package com.dbtraining.reconx.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Objects;

/**
 * ============================================================================
 * TICKET-ADV021 — BondTrade with Builder pattern
 *
 * WHAT:    Fixed-income trade — couponRate, maturityDate, faceValue, isin.
 * HOW:     Same builder pattern. notional() = faceValue (in the bond's ccy).
 * WHY:     Bonds need couponRate/maturity for downstream cashflow modelling.
 *          Modelling them on the trade is the simplest path for the demo.
 * ============================================================================
 *
 * Equality and hashing are derived from {@code tradeRef} alone; {@code
 * toString} omits counterparty identity to avoid leaking PII into logs.
 */
public final class BondTrade implements TradeType {

    private final TradeRef tradeRef;
    private final String isin;
    private final BigDecimal faceValue;
    private final BigDecimal couponRate;
    private final LocalDate maturityDate;
    private final Currency currency;
    private final Side side;
    private final LocalDate tradeDate;
    private final long counterpartyId;

    private BondTrade(Builder b) {
        this.tradeRef       = b.tradeRef;
        this.isin           = b.isin;
        this.faceValue      = b.faceValue;
        this.couponRate     = b.couponRate;
        this.maturityDate   = b.maturityDate;
        this.currency       = b.currency;
        this.side           = b.side;
        this.tradeDate      = b.tradeDate;
        this.counterpartyId = b.counterpartyId;
    }

    /**
     * Creates a new, empty {@link Builder} for assembling a {@code BondTrade}
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
     * @return the business date this bond trade was struck on.
     */
    @Override public LocalDate tradeDate()   { return tradeDate; }

    /**
     * {@inheritDoc}
     *
     * @return always {@link AssetClass#BOND} for this implementation.
     */
    @Override public AssetClass assetClass() { return AssetClass.BOND; }

    /**
     * Returns the notional value of this bond trade, defined as its face
     * value rather than a market or dirty price.
     *
     * @return the {@link #faceValue()} in {@link #currency()}; never
     *         {@code null}.
     */
    @Override public Money notional() {
        return new Money(faceValue, currency);
    }

    /**
     * Returns the International Securities Identification Number that
     * uniquely identifies the traded bond issue.
     *
     * @return the ISIN; never {@code null}.
     */
    public String isin()              { return isin; }

    /**
     * Returns the par (redemption) value of the bond traded, used as this
     * trade's notional.
     *
     * @return the face value; never {@code null}.
     */
    public BigDecimal faceValue()     { return faceValue; }

    /**
     * Returns the bond's annual coupon rate, used for downstream cashflow
     * modelling rather than for pricing this trade itself.
     *
     * @return the coupon rate, expressed as a decimal (e.g. {@code 0.05}
     *         for 5%); never {@code null}.
     */
    public BigDecimal couponRate()    { return couponRate; }

    /**
     * Returns the date the bond redeems at face value.
     *
     * @return the maturity date; never {@code null}, and never before
     *         {@link #tradeDate()}.
     */
    public LocalDate maturityDate()   { return maturityDate; }

    /**
     * Returns the currency the bond's face value and coupon are denominated
     * in.
     *
     * @return the trade currency; never {@code null}.
     */
    public Currency currency()        { return currency; }

    /**
     * Returns whether this trade is a buy or a sell of the bond.
     *
     * @return the trade side; never {@code null}.
     */
    public Side side()                { return side; }

    /**
     * Returns the internal identifier of the counterparty on the other side
     * of this trade.
     *
     * @return the counterparty's database identifier.
     */
    public long counterpartyId()      { return counterpartyId; }

    /**
     * Compares by {@code tradeRef} only — two {@code BondTrade} instances
     * are equal iff they share the same natural key, regardless of any
     * other field differing.
     *
     * @param o the object to compare against.
     * @return {@code true} if {@code o} is a {@code BondTrade} with an
     *         equal {@code tradeRef}.
     */
    @Override public boolean equals(Object o) {
        return (o instanceof BondTrade other) && tradeRef.equals(other.tradeRef);
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
     *         {@code "BondTrade[ref=..., isin=..., face=... CCY, coupon=..., maturity=..., side=...]"}.
     */
    @Override public String toString() {
        return "BondTrade[ref=%s, isin=%s, face=%s %s, coupon=%s, maturity=%s, side=%s]"
                .formatted(tradeRef, isin, faceValue, currency.getCurrencyCode(),
                        couponRate, maturityDate, side);
    }

    /**
     * WHAT: Fluent builder for {@link BondTrade}.
     * HOW:  Each setter returns {@code this}; {@link #build()} is the single
     * chokepoint that validates every required field and invariant.
     * WHY:  Keeps {@code BondTrade} immutable while avoiding a
     * nine-argument constructor at the call site.
     */
    public static final class Builder {
        private TradeRef tradeRef;
        private String isin;
        private BigDecimal faceValue, couponRate;
        private LocalDate maturityDate, tradeDate;
        private Currency currency;
        private Side side;
        private long counterpartyId;

        public Builder tradeRef(TradeRef v)        { this.tradeRef = v; return this; }
        public Builder isin(String v)              { this.isin = v; return this; }
        public Builder faceValue(BigDecimal v)     { this.faceValue = v; return this; }
        public Builder couponRate(BigDecimal v)    { this.couponRate = v; return this; }
        public Builder maturityDate(LocalDate v)   { this.maturityDate = v; return this; }
        public Builder currency(String code)       { this.currency = Currency.getInstance(code); return this; }
        public Builder side(Side v)                { this.side = v; return this; }
        public Builder tradeDate(LocalDate v)      { this.tradeDate = v; return this; }
        public Builder counterpartyId(long v)      { this.counterpartyId = v; return this; }

        /**
         * Build the immutable {@link BondTrade}, validating that every required
         * field is set and that all invariants hold.
         *
         * @return a fully-constructed, validated {@code BondTrade} — never
         *         {@code null}.
         * @throws NullPointerException  if any required field
         *                               ({@code tradeRef}, {@code isin},
         *                               {@code faceValue}, {@code couponRate},
         *                               {@code maturityDate}, {@code currency},
         *                               {@code side}, or {@code tradeDate})
         *                               was not set.
         * @throws IllegalStateException if {@code maturityDate} is before
         *                               {@code tradeDate}.
         */
        public BondTrade build() {
            Objects.requireNonNull(tradeRef,     "tradeRef");
            Objects.requireNonNull(isin,         "isin");
            Objects.requireNonNull(faceValue,    "faceValue");
            Objects.requireNonNull(couponRate,   "couponRate");
            Objects.requireNonNull(maturityDate, "maturityDate");
            Objects.requireNonNull(currency,     "currency");
            Objects.requireNonNull(side,         "side");
            Objects.requireNonNull(tradeDate,    "tradeDate");
            if (maturityDate.isBefore(tradeDate))
                throw new IllegalStateException("maturityDate cannot be before tradeDate");
            return new BondTrade(this);
        }
    }
}