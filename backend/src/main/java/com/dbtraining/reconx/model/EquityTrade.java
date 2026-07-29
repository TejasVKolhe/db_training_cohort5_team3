package com.dbtraining.reconx.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Objects;

/**
 * ============================================================================
 * TICKET-ADV019 — EquityTrade with Builder pattern
 *
 * WHAT: Concrete TradeType for equity (cash share) trades.
 * HOW: Final class, all fields final, no setters. Construction is via the
 * nested {@link Builder} which validates in {@link Builder#build()}.
 * WHY: Eight required fields on a single constructor is unreadable at
 * the call site. Builder gives named arguments, makes the validity
 * check a single chokepoint, and the object stays immutable.
 * OBSERVE: Calling build() with a missing required field throws
 * IllegalStateException — verified by EquityTradeTest.
 * HINT: Same shape applied to FXTrade/BondTrade/DerivativeTrade.
 * ============================================================================
 *
 * Equality and hashing are derived from {@code tradeRef} alone (see
 * {@link #equals(Object)}); {@code toString} intentionally omits
 * counterparty identity to avoid leaking PII into logs.
 */
public final class EquityTrade implements TradeType {

    private final TradeRef tradeRef;
    private final String instrumentSymbol;
    private final BigDecimal quantity;
    private final BigDecimal price;
    private final Currency currency;
    private final Side side;
    private final LocalDate tradeDate;
    private final long counterpartyId;

    private EquityTrade(Builder b) {
        this.tradeRef = b.tradeRef;
        this.instrumentSymbol = b.instrumentSymbol;
        this.quantity = b.quantity;
        this.price = b.price;
        this.currency = b.currency;
        this.side = b.side;
        this.tradeDate = b.tradeDate;
        this.counterpartyId = b.counterpartyId;
    }

    /**
     * Creates a new, empty {@link Builder} for assembling an
     * {@code EquityTrade} field by field.
     *
     * @return a fresh builder with no fields set; never {@code null}.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * {@inheritDoc}
     *
     * @return this trade's unique reference, the natural key used for
     *         {@link #equals(Object)} and {@link #hashCode()}.
     */
    @Override
    public TradeRef tradeRef() {
        return tradeRef;
    }

    /**
     * {@inheritDoc}
     *
     * @return the business date this equity trade was struck on.
     */
    @Override
    public LocalDate tradeDate() {
        return tradeDate;
    }

    /**
     * {@inheritDoc}
     *
     * @return always {@link AssetClass#EQUITY} for this implementation.
     */
    @Override
    public AssetClass assetClass() {
        return AssetClass.EQUITY;
    }

    /**
     * Computes the notional value of this trade as quantity multiplied by
     * price, denominated in the trade's currency.
     *
     * @return the notional amount ({@code quantity * price}) in
     *         {@link #currency()}; never {@code null}.
     */
    @Override
    public Money notional() {
        return new Money(quantity.multiply(price), currency);
    }

    /**
     * Returns the exchange ticker or symbol identifying the traded
     * instrument (e.g. {@code "AAPL"}).
     *
     * @return the instrument symbol; never {@code null} or blank.
     */
    public String instrumentSymbol() {
        return instrumentSymbol;
    }

    /**
     * Returns the number of shares traded.
     *
     * @return a strictly positive quantity.
     */
    public BigDecimal quantity() {
        return quantity;
    }

    /**
     * Returns the per-share execution price.
     *
     * @return a strictly positive price.
     */
    public BigDecimal price() {
        return price;
    }

    /**
     * Returns the currency this trade's price and notional are denominated
     * in.
     *
     * @return the trade currency; never {@code null}.
     */
    public Currency currency() {
        return currency;
    }

    /**
     * Returns whether this trade is a buy or a sell.
     *
     * @return the trade side; never {@code null}.
     */
    public Side side() {
        return side;
    }

    /**
     * Returns the internal identifier of the counterparty on the other side
     * of this trade.
     *
     * @return the counterparty's database identifier.
     */
    public long counterpartyId() {
        return counterpartyId;
    }

    /**
     * Compares by {@code tradeRef} only — two {@code EquityTrade} instances
     * are equal iff they share the same natural key, regardless of any
     * other field differing.
     *
     * @param o the object to compare against.
     * @return {@code true} if {@code o} is an {@code EquityTrade} with an
     *         equal {@code tradeRef}.
     */
    @Override
    public boolean equals(Object o) {
        return (o instanceof EquityTrade other) && tradeRef.equals(other.tradeRef);
    }

    /**
     * Consistent with {@link #equals(Object)}: derived solely from
     * {@code tradeRef}.
     *
     * @return the hash code of this trade's {@code tradeRef}.
     */
    @Override
    public int hashCode() {
        return tradeRef.hashCode();
    }

    /**
     * Renders a compact, human-readable summary for logs, deliberately
     * excluding {@code counterpartyId} to avoid printing identifying
     * relationship data.
     *
     * @return a string of the form
     *         {@code "EquityTrade[ref=..., symbol=..., qty=..., price=... CCY, side=...]"}.
     */
    @Override
    public String toString() {
        return "EquityTrade[ref=%s, symbol=%s, qty=%s, price=%s %s, side=%s]"
                .formatted(tradeRef, instrumentSymbol, quantity, price,
                        currency.getCurrencyCode(), side);
    }

    /**
     * WHAT: Fluent builder for {@link EquityTrade}.
     * HOW:  Each setter returns {@code this}; {@link #build()} is the single
     * chokepoint that validates every required field and invariant.
     * WHY:  Keeps {@code EquityTrade} immutable while avoiding an
     * eight-argument constructor at the call site.
     */
    public static final class Builder {
        private TradeRef tradeRef;
        private String instrumentSymbol;
        private BigDecimal quantity;
        private BigDecimal price;
        private Currency currency;
        private Side side;
        private LocalDate tradeDate;
        private long counterpartyId;

        public Builder tradeRef(TradeRef v) {
            this.tradeRef = v;
            return this;
        }

        public Builder instrumentSymbol(String v) {
            this.instrumentSymbol = v;
            return this;
        }

        public Builder quantity(BigDecimal v) {
            this.quantity = v;
            return this;
        }

        public Builder price(BigDecimal v) {
            this.price = v;
            return this;
        }

        public Builder currency(Currency v) {
            this.currency = v;
            return this;
        }

        public Builder currency(String code) {
            return currency(Currency.getInstance(code));
        }

        public Builder side(Side v) {
            this.side = v;
            return this;
        }

        public Builder tradeDate(LocalDate v) {
            this.tradeDate = v;
            return this;
        }

        public Builder counterpartyId(long v) {
            this.counterpartyId = v;
            return this;
        }

        /**
         * Build the immutable {@link EquityTrade}, validating that every required
         * field is set and that all invariants hold.
         *
         * @return a fully-constructed, validated {@code EquityTrade} — never
         *         {@code null}.
         * @throws NullPointerException  if any required field
         *                               ({@code tradeRef}, {@code instrumentSymbol},
         *                               {@code quantity}, {@code price},
         *                               {@code currency}, {@code side}, or
         *                               {@code tradeDate}) was not set.
         * @throws IllegalStateException if {@code quantity} is not strictly
         *                               positive, or {@code price} is not
         *                               strictly positive.
         */
        public EquityTrade build() {
            Objects.requireNonNull(tradeRef, "tradeRef");
            Objects.requireNonNull(instrumentSymbol, "instrumentSymbol");
            Objects.requireNonNull(quantity, "quantity");
            Objects.requireNonNull(price, "price");
            Objects.requireNonNull(currency, "currency");
            Objects.requireNonNull(side, "side");
            Objects.requireNonNull(tradeDate, "tradeDate");
            if (quantity.signum() <= 0)
                throw new IllegalStateException("quantity must be > 0");
            if (price.signum() <= 0)
                throw new IllegalStateException("price must be > 0");
            return new EquityTrade(this);
        }
    }
}