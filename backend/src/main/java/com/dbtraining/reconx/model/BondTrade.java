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
    * Creates a new builder for constructing {@code BondTrade} instances.
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
    * @return {@link AssetClass#BOND}
    */
    @Override
    public AssetClass assetClass() {
        return AssetClass.BOND;
    }
    

    /**
     * Returns the notional value of the bond trade.
    *
    * @return monetary value containing the face value and trade currency
    */
    @Override public Money notional() {
        return new Money(faceValue, currency);
    }

    /**
    * Returns the International Securities Identification Number (ISIN) of the bond.
    *
    * @return ISIN identifying the traded bond
    */
    public String isin() {
        return isin;
    }

    /**
    * Returns the face value of the bond.
    *
    * @return nominal value of the traded bond
    */
    public BigDecimal faceValue() {
        return faceValue;
    }

    /**
    * Returns the annual coupon rate of the bond.
    *
    * @return coupon rate associated with the bond
    */
    public BigDecimal couponRate() {
        return couponRate;
    }

    /**
    * Returns the maturity date of the bond.
    *
    *  @return date on which the bond matures
    */
    public LocalDate maturityDate() {
        return maturityDate;
    }

    /**
    * Returns the currency in which the bond trade is denominated.
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
        return (o instanceof BondTrade other) && tradeRef.equals(other.tradeRef);
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
    * Returns a human-readable representation of this bond trade.
    *
    * @return formatted string containing the key trade details
    */
    @Override
    public String toString() {
        return "BondTrade[ref=%s, isin=%s, face=%s %s, coupon=%s, maturity=%s, side=%s]"
            .formatted(
                    tradeRef,
                    isin,
                    faceValue,
                    currency.getCurrencyCode(),
                    couponRate,
                    maturityDate,
                    side);
    }

    /**
    * Builder for creating immutable {@link BondTrade} instances.
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
    * Creates an immutable {@code BondTrade} from the configured values.
    *
    * @return fully constructed bond trade
    * @throws NullPointerException if any required field has not been provided
    * @throws IllegalStateException if the maturity date is before the trade date
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