package com.shareexpenses.server.currency;

public enum Currency {
  EUR("EUR"),
  CHF("CHF");

  public final String label;

  private Currency(String label) {
    this.label = label;
  }
}
