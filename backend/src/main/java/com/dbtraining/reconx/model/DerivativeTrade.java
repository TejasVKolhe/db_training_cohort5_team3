package com.dbtraining.reconx.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Objects;

/**
 * ============================================================================
 * TICKET-ADV022 — DerivativeTrade with Builder pattern
 *
 * WHAT:    Option/derivative trade — underlying, strike, expiry, optionType.
 * HOW:     Same builder pattern. notional() = strike * quantity in the
 *          trade's currency (simplified — real derivatives use delta-adjusted).
 * ============================================================================
 *
 * Equality and hashing are derived from {@code tradeRef} alone; {@code
 * toString} omits counterparty identity to avoid leaking PII into logs.
 */
public final class DerivativeTrade implements TradeType {

    /**
     * WHAT: Distinguishes a call from a put on the derivative's underlying.
     * WHY:  Drives payoff direction downstream; kept separate from
     * {@link Side} because option type and buy/sell are independent axes
     * (e.g. a short call and a long put are both valid combinations).
     */
    public enum OptionType { CALL, PUT }

    private final TradeRef tradeRef;
    private final String underlying;
    private final BigDecimal strike;
    private final BigDecimal quantity;
    private final LocalDate expiry;
    private final OptionType optionType;
    private final Currency currency;
    private final Side side;
    private final LocalDate tradeDate;
    private final long counterpartyId;

    private DerivativeTrade(Builder b) {
        this.tradeRef       = b.tradeRef;
        this.underlying     = b.underlying;
        this.strike         = b.strike;
        this.quantity       = b.quantity;
        this.expiry         = b.expiry;
        this.optionType     = b.optionType;
        this.currency       = b.currency;
        this.side           = b.side;
        this.tradeDate      = b.tradeDate;
        this.counterpartyId = b.counterpartyId;
    }

    /**
     * Creates a new, empty {@link Builder} for assembling a
     * {@code DerivativeTrade} field by field.
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
     * @return the business date this derivative trade was struck on.
     */
    @Override public LocalDate tradeDate()   { return tradeDate; }

    /**
     * {@inheritDoc}
     *
     * @return always {@link AssetClass#DERIVATIVE} for this implementation.
     */
    @Override public AssetClass assetClass() { return AssetClass.DERIVATIVE; }

    /**
     * Computes a simplified notional as strike multiplied by quantity, in
     * the trade's currency. This intentionally omits delta-adjustment used
     * by real option pricing — it exists only to give reconciliation
     * summaries a comparable scale across asset classes.
     *
     * @return {@code strike * quantity} in {@link #currency()}; never
     *         {@code null}.
     */
    @Override public Money notional() {
        return new Money(strike.multiply(quantity), currency);
    }

    /**
     * Returns the ticker or identifier of the instrument this derivative is
     * written against.
     *
     * @return the underlying instrument's symbol; never {@code null}.
     */
    public String underlying()       { return underlying; }

    /**
     * Returns the strike price at which this option can be exercised.
     *
     * @return a strictly positive strike price.
     */
    public BigDecimal strike()       { return strike; }

    /**
     * Returns the number of contracts or units traded.
     *
     * @return a strictly positive quantity.
     */
    public BigDecimal quantity()     { return quantity; }

    /**
     * Returns the date this option expires and can no longer be exercised.
     *
     * @return the expiry date; never {@code null}, and never before
     *         {@link #tradeDate()}.
     */
    public LocalDate expiry()        { return expiry; }

    /**
     * Returns whether this derivative is a call or a put.
     *
     * @return the option type; never {@code null}.
     */
    public OptionType optionType()   { return optionType; }

    /**
     * Returns the currency the strike and notional are denominated in.
     *
     * @return the trade currency; never {@code null}.
     */
    public Currency currency()       { return currency; }

    /**
     * Returns whether this trade is a buy or a sell of the derivative
     * contract itself (independent of {@link #optionType()}).
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
     * Compares by {@code tradeRef} only — two {@code DerivativeTrade}
     * instances are equal iff they share the same natural key, regardless
     * of any other field differing.
     *
     * @param o the object to compare against.
     * @return {@code true} if {@code o} is a {@code DerivativeTrade} with
     *         an equal {@code tradeRef}.
     */
    @Override public boolean equals(Object o) {
        return (o instanceof DerivativeTrade other) && tradeRef.equals(other.tradeRef);
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
     *         {@code "DerivativeTrade[ref=..., CALL|PUT underlying on tradeDate, strike=... CCY, qty=..., expiry=..., side=...]"}.
     */
    @Override public String toString() {
        return "DerivativeTrade[ref=%s, %s %s on %s, strike=%s %s, qty=%s, expiry=%s, side=%s]"
                .formatted(tradeRef, optionType, underlying, tradeDate, strike,
                        currency.getCurrencyCode(), quantity, expiry, side);
    }

    /**
     * WHAT: Fluent builder for {@link DerivativeTrade}.
     * HOW:  Each setter returns {@code this}; {@link #build()} is the single
     * chokepoint that validates every required field and invariant.
     * WHY:  Keeps {@code DerivativeTrade} immutable while avoiding a
     * nine-argument constructor at the call site.
     */
    public static final class Builder {
        private TradeRef tradeRef;
        private String underlying;
        private BigDecimal strike, quantity;
        private LocalDate expiry, tradeDate;
        private OptionType optionType;
        private Currency currency;
        private Side side;
        private long counterpartyId;

        public Builder tradeRef(TradeRef v)        { this.tradeRef = v; return this; }
        public Builder underlying(String v)        { this.underlying = v; return this; }
        public Builder strike(BigDecimal v)        { this.strike = v; return this; }
        public Builder quantity(BigDecimal v)      { this.quantity = v; return this; }
        public Builder expiry(LocalDate v)         { this.expiry = v; return this; }
        public Builder optionType(OptionType v)    { this.optionType = v; return this; }
        public Builder currency(String code)       { this.currency = Currency.getInstance(code); return this; }
        public Builder side(Side v)                { this.side = v; return this; }
        public Builder tradeDate(LocalDate v)      { this.tradeDate = v; return this; }
        public Builder counterpartyId(long v)      { this.counterpartyId = v; return this; }

        /**
         * Build the immutable {@link DerivativeTrade}, validating that every
         * required field is set and that all invariants hold.
         *
         * @return a fully-constructed, validated {@code DerivativeTrade} —
         *         never {@code null}.
         * @throws NullPointerException  if any required field
         *                               ({@code tradeRef}, {@code underlying},
         *                               {@code strike}, {@code quantity},
         *                               {@code expiry}, {@code optionType},
         *                               {@code currency}, {@code side}, or
         *                               {@code tradeDate}) was not set.
         * @throws IllegalStateException if {@code strike} is not strictly
         *                               positive, {@code quantity} is not
         *                               strictly positive, or {@code expiry}
         *                               is before {@code tradeDate}.
         */
        public DerivativeTrade build() {
            Objects.requireNonNull(tradeRef,   "tradeRef");
            Objects.requireNonNull(underlying, "underlying");
            Objects.requireNonNull(strike,     "strike");
            Objects.requireNonNull(quantity,   "quantity");
            Objects.requireNonNull(expiry,     "expiry");
            Objects.requireNonNull(optionType, "optionType");
            Objects.requireNonNull(currency,   "currency");
            Objects.requireNonNull(side,       "side");
            Objects.requireNonNull(tradeDate,  "tradeDate");
            if (strike.signum() <= 0)   throw new IllegalStateException("strike must be > 0");
            if (quantity.signum() <= 0) throw new IllegalStateException("quantity must be > 0");
            if (expiry.isBefore(tradeDate))
                throw new IllegalStateException("expiry cannot be before tradeDate");
            return new DerivativeTrade(this);
        }
    }
}