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
 * 
 *  WHY:     Encapsulates derivative-specific trade attributes while providing
 *          a consistent immutable TradeType implementation for reconciliation.
 * ============================================================================
 */
public final class DerivativeTrade implements TradeType {

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
    *  Creates a new builder for constructing {@code DerivativeTrade} instances.
    *
    * @return a new builder configured for fluent trade creation
    */
    public static Builder builder() { return new Builder(); }

    /**
    * Returns the unique reference assigned to this trade.
    *
    * @return trade reference used to identify the trade
    */
    @Override
    public TradeRef tradeRef() {
        return tradeRef;
    }

    /**
    * Returns the execution date of the trade.
    *
    * @return date on which the trade was executed
    */
    @Override
    public LocalDate tradeDate() {
        return tradeDate;
    }

    /**
    * Returns the asset class represented by this trade.
     *
    * @return {@link AssetClass#DERIVATIVE}
    */
    @Override
    public AssetClass assetClass() {
        return AssetClass.DERIVATIVE;
    }
    /**
    * Returns the notional value of the derivative trade.
    *
    * @return monetary value calculated as the strike price multiplied by
    *         the quantity in the trade currency
    */
    @Override
    public Money notional() {
        return new Money(strike.multiply(quantity), currency);
    }

    /**
 * Returns the underlying asset of the derivative.
 *
 * @return underlying instrument name or symbol
 */
public String underlying() {
    return underlying;
}

/**
 * Returns the strike price of the derivative.
 *
 * @return strike price of the derivative contract
 */
public BigDecimal strike() {
    return strike;
}

/**
 * Returns the quantity of derivative contracts.
 *
 * @return number of contracts traded
 */
public BigDecimal quantity() {
    return quantity;
}

/**
 * Returns the expiry date of the derivative contract.
 *
 * @return contract expiry date
 */
public LocalDate expiry() {
    return expiry;
}

/**
 * Returns the option type of the derivative.
 *
 * @return whether the contract is a call or put option
 */
public OptionType optionType() {
    return optionType;
}

/**
 * Returns the currency in which the derivative trade is denominated.
 *
 * @return trade currency
 */
public Currency currency() {
    return currency;
}

/**
 * Returns the direction of the trade.
 *
 * @return whether the trade is a buy or sell
 */
public Side side() {
    return side;
}

/**
 * Returns the identifier of the trade counterparty.
 *
 * @return unique counterparty identifier
 */
public long counterpartyId() {
    return counterpartyId;
}

/**
 * Compares this trade with another object for equality.
 *
 * @param o object to compare with this trade
 * @return {@code true} if the supplied object represents the same trade;
 *         {@code false} otherwise
 */
@Override
public boolean equals(Object o) {
    return (o instanceof DerivativeTrade other)
            && tradeRef.equals(other.tradeRef);
}

/**
 * Returns a hash code consistent with {@link #equals(Object)}.
 *
 * @return hash code derived from the trade reference
 */
@Override
public int hashCode() {
    return tradeRef.hashCode();
}

/**
 * Returns a human-readable representation of this derivative trade.
 *
 * @return formatted string containing the key trade details
 */
@Override
public String toString() {
    return "DerivativeTrade[ref=%s, %s %s on %s, strike=%s %s, qty=%s, expiry=%s, side=%s]"
            .formatted(
                    tradeRef,
                    optionType,
                    underlying,
                    tradeDate,
                    strike,
                    currency.getCurrencyCode(),
                    quantity,
                    expiry,
                    side);
}

/**
 * Builder for creating immutable {@link DerivativeTrade} instances.
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
        * Creates an immutable {@code DerivativeTrade} from the configured values.
        *
        * @return fully constructed derivative trade
        * @throws NullPointerException if any required field has not been provided
        * @throws IllegalStateException if the strike or quantity is not positive,
        *         or if the expiry date is before the trade date
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