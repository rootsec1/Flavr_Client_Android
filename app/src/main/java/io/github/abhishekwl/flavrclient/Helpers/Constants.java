package io.github.abhishekwl.flavrclient.Helpers;

import java.util.Currency;
import java.util.Locale;

public class Constants {

  public static final String currencyCode = Currency.getInstance(Locale.getDefault()).getCurrencyCode();

}
