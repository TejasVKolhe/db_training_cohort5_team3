package com.dbtraining.reconx.model;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

/**
 * ============================================================================
 * TICKET-ADV024 — Immutable value object: Money
 *
 * WHAT:    Record bundling a {@link BigDecimal} amount with a {@link Currency}.
 *          Used everywhere a monetary value crosses a boundary (DTO, event,
 *          metric).
 * HOW:     Compact constructor enforces: non-null amount, non-null currency,
 *          non-negative amount. {@link BigDecimal} (not double) prevents
 *          accumulating floating-point error on aggregations.
 * WHY:     Passing raw BigDecimal around loses currency context — a USD 100
 *          can be silently added to a EUR 100. Money makes the mismatch
 *          fail at the type level: {@code plus()} throws if currencies differ.
 * OBSERVE: {@code Money.of("100.00","USD").plus(Money.of("50","EUR"))} throws.
 *          {@code Money.of("100","USD").plus(Money.of("50","USD"))} returns 150 USD.
 *
 * @param amount monetary amount represented by this value object
 * @param currency currency associated with the monetary amount
 * ============================================================================
 */
public record Money(BigDecimal amount, Currency currency) {

    /**
     * Creates a validated money value.
     *
     * @throws NullPointerException if the amount or currency is {@code null}
     * @throws IllegalArgumentException if the amount is negative
     */
    public Money {
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(currency, "currency");
        if (amount.signum() < 0) {
            throw new IllegalArgumentException(
                    "Money amount cannot be negative: " + amount);
        }
    }


    /**
 * Creates a {@code Money} instance from a string amount and currency code.
 *
 * @param amount monetary amount represented as a string
 * @param currencyCode ISO 4217 currency code
 * @return new money instance
 */
public static Money of(String amount, String currencyCode) {
    return new Money(new BigDecimal(amount), Currency.getInstance(currencyCode));
}

/**
 * Creates a {@code Money} instance from a numeric amount and currency code.
 *
 * @param amount monetary amount
 * @param currencyCode ISO 4217 currency code
 * @return new money instance
 */
public static Money of(BigDecimal amount, String currencyCode) {
    return new Money(amount, Currency.getInstance(currencyCode));
}

    /**
 * Returns the sum of this money and another money value.
 *
 * @param other money value to add
 * @return new money instance representing the sum
 * @throws NullPointerException if {@code other} is {@code null}
 * @throws IllegalArgumentException if the currencies differ
 */
public Money plus(Money other) {
    Objects.requireNonNull(other, "other");

    if (!currency.equals(other.currency())) {
        throw new IllegalArgumentException(
                "Cannot add %s to %s — currency mismatch"
                        .formatted(other.currency(), this.currency));
    }

    return new Money(amount.add(other.amount()), currency);
}

/**
 * Multiplies this monetary amount by the supplied multiplier.
 *
 * @param multiplier value by which to multiply the amount
 * @return new money instance containing the multiplied amount
 * @throws NullPointerException if {@code multiplier} is {@code null}
 */
public Money times(BigDecimal multiplier) {
    Objects.requireNonNull(multiplier, "multiplier");
    return new Money(amount.multiply(multiplier), currency);
}
}
