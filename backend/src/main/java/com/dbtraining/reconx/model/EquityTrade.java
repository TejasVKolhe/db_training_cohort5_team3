package com.dbtraining.reconx.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Objects;

/**
 * ============================================================================
 * TICKET-ADV019 — EquityTrade with Builder pattern
 *
 * WHAT:    Concrete TradeType for equity (cash share) trades.
 * HOW:     Final class, all fields final, no setters. Construction is via the
 *          nested {@link Builder} which validates in {@link Builder#build()}.
 * WHY:     Eight required fields on a single constructor is unreadable at
 *          the call site. Builder gives named arguments, makes the validity
 *          check a single chokepoint, and the object stays immutable.
 * OBSERVE: Calling build() with a missing required field throws
 *          IllegalStateException — verified by EquityTradeTest.
 * HINT:    Same shape applied to FXTrade/BondTrade/DerivativeTrade.
 * ============================================================================
 *
 * TICKET-ADV028 — equals/hashCode from tradeRef (Object methods on a regular class)
 * TICKET-ADV030 — toString() omits PII, prints reference/symbol/qty/price/side
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
        this.tradeRef         = b.tradeRef;
        this.instrumentSymbol = b.instrumentSymbol;
        this.quantity         = b.quantity;
        this.price            = b.price;
        this.currency         = b.currency;
        this.side             = b.side;
        this.tradeDate        = b.tradeDate;
        this.counterpartyId   = b.counterpartyId;
    }

    /**
 * Creates a new builder for constructing {@code EquityTrade} instances.
 *
 * @return a new builder configured for fluent trade creation
 */
public static Builder builder() {
    return new Builder();
}

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
 * @return {@link AssetClass#EQUITY}
 */
@Override
public AssetClass assetClass() {
    return AssetClass.EQUITY;
}

/**
 * Returns the notional value of the equity trade.
 *
 * @return monetary value calculated as the quantity multiplied by
 *         the trade price in the trade currency
 */
@Override
public Money notional() {
    return new Money(quantity.multiply(price), currency);
}


/**
 * Returns the traded instrument symbol.
 *
 * @return instrument symbol of the equity security
 */
public String instrumentSymbol() {
    return instrumentSymbol;
}

/**
 * Returns the quantity of shares traded.
 *
 * @return number of shares traded
 */
public BigDecimal quantity() {
    return quantity;
}

/**
 * Returns the execution price per share.
 *
 * @return trade price per share
 */
public BigDecimal price() {
    return price;
}

/**
 * Returns the currency in which the trade is denominated.
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
    return (o instanceof EquityTrade other)
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
 * Returns a human-readable representation of this equity trade.
 *
 * @return formatted string containing the key trade details
 */
@Override
public String toString() {
    return "EquityTrade[ref=%s, symbol=%s, qty=%s, price=%s %s, side=%s]"
            .formatted(
                    tradeRef,
                    instrumentSymbol,
                    quantity,
                    price,
                    currency.getCurrencyCode(),
                    side);
}

    /**
 * Builder for creating immutable {@link EquityTrade} instances.
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

    public Builder tradeRef(TradeRef v)           { this.tradeRef = v; return this; }
    public Builder instrumentSymbol(String v)     { this.instrumentSymbol = v; return this; }
    public Builder quantity(BigDecimal v)         { this.quantity = v; return this; }
    public Builder price(BigDecimal v)            { this.price = v; return this; }
    public Builder currency(Currency v)           { this.currency = v; return this; }
    public Builder currency(String code)          { return currency(Currency.getInstance(code)); }
    public Builder side(Side v)                   { this.side = v; return this; }
    public Builder tradeDate(LocalDate v)         { this.tradeDate = v; return this; }
    public Builder counterpartyId(long v)         { this.counterpartyId = v; return this; }

    /**
     * Creates an immutable {@code EquityTrade} from the configured values.
     *
     * @return fully constructed equity trade
     * @throws NullPointerException if any required field has not been provided
     * @throws IllegalStateException if the quantity or price is not positive
     */
    public EquityTrade build() {
        Objects.requireNonNull(tradeRef,         "tradeRef");
        Objects.requireNonNull(instrumentSymbol, "instrumentSymbol");
        Objects.requireNonNull(quantity,         "quantity");
        Objects.requireNonNull(price,            "price");
        Objects.requireNonNull(currency,         "currency");
        Objects.requireNonNull(side,             "side");
        Objects.requireNonNull(tradeDate,        "tradeDate");

        if (quantity.signum() <= 0) {
            throw new IllegalStateException("quantity must be > 0");
        }
        if (price.signum() <= 0) {
            throw new IllegalStateException("price must be > 0");
        }

        return new EquityTrade(this);
    }
    }
}