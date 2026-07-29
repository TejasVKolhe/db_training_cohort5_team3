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
 * Creates a new builder for constructing {@code FXTrade} instances.
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
 * @return {@link AssetClass#FX}
 */
@Override
public AssetClass assetClass() {
    return AssetClass.FX;
}

/**
 * Returns the notional value of the FX trade in the quote currency.
 *
 * @return monetary value calculated by converting the notional amount
 *         in the base currency using the FX rate
 */
@Override
public Money notional() {
        // TODO(TICKET-ADV020): return new Money(notionalCcy1 * fxRate, ccy2).
        throw new UnsupportedOperationException("TICKET-ADV020");
    }

    /**
 * Returns the base currency of the FX trade.
 *
 * @return first currency in the traded currency pair
 */
public Currency ccy1() {
    return ccy1;
}

/**
 * Returns the quote currency of the FX trade.
 *
 * @return second currency in the traded currency pair
 */
public Currency ccy2() {
    return ccy2;
}

/**
 * Returns the notional amount in the base currency.
 *
 * @return notional amount denominated in the first currency
 */
public BigDecimal notionalCcy1() {
    return notionalCcy1;
}

/**
 * Returns the exchange rate used for the FX trade.
 *
 * @return exchange rate between the base and quote currencies
 */
public BigDecimal fxRate() {
    return fxRate;
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
    // TODO(TICKET-ADV028): pattern-match on FXTrade and compare tradeRef.
    throw new UnsupportedOperationException("TICKET-ADV028");
}

/**
 * Returns a hash code consistent with {@link #equals(Object)}.
 *
 * @return hash code derived from the trade reference
 */
@Override
public int hashCode() {
    // TODO(TICKET-ADV028): hash from tradeRef.
    throw new UnsupportedOperationException("TICKET-ADV028");
}

/**
 * Returns a human-readable representation of this FX trade.
 *
 * @return formatted string containing the key trade details
 */
@Override
public String toString() {
    // TODO(TICKET-ADV030): "FXTrade[ref=..., CCY1/CCY2, notional=... CCY1, rate=..., side=...]"
    throw new UnsupportedOperationException("TICKET-ADV030");
}

/**
 * Builder for creating immutable {@link FXTrade} instances.
 */
public static final class Builder {
    private TradeRef tradeRef;
    private Currency ccy1, ccy2;
    private BigDecimal notionalCcy1, fxRate;
    private Side side;
    private LocalDate tradeDate;
    private long counterpartyId;

    public Builder tradeRef(TradeRef v)       { this.tradeRef = v; return this; }
    public Builder ccy1(String code)          { this.ccy1 = Currency.getInstance(code); return this; }
    public Builder ccy2(String code)          { this.ccy2 = Currency.getInstance(code); return this; }
    public Builder notionalCcy1(BigDecimal v) { this.notionalCcy1 = v; return this; }
    public Builder fxRate(BigDecimal v)       { this.fxRate = v; return this; }
    public Builder side(Side v)               { this.side = v; return this; }
    public Builder tradeDate(LocalDate v)     { this.tradeDate = v; return this; }
    public Builder counterpartyId(long v)     { this.counterpartyId = v; return this; }

    /**
     * Creates an immutable {@code FXTrade} from the configured values.
     *
     * @return fully constructed FX trade
     * @throws NullPointerException if any required field has not been provided
     * @throws IllegalStateException if the currencies are identical or the
     *         exchange rate is not positive
     */
    public FXTrade build() {
        // TODO(TICKET-ADV020):
        //   - Objects.requireNonNull each required field.
        //   - ccy1 must differ from ccy2 (IllegalStateException otherwise).
        //   - fxRate must be > 0.
        //   - return new FXTrade(this).
        throw new UnsupportedOperationException("TICKET-ADV020");
    }
    }
}
