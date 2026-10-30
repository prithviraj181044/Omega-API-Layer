package com.eurobank.omega.error.contract;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Resolves fixed code-space text without retaining request state.
 *
 * <p>Create this immutable resolver at application bootstrap so classpath resources are read before
 * event-loop request handling. It is not a replacement for a WebFlux Problem response handler.
 */
public final class ErrorMessages {
  private static final Locale EN_GB = Locale.forLanguageTag("en-GB");
  private static final Locale EL_GR = Locale.forLanguageTag("el-GR");
  private static final List<Locale> SUPPORTED = List.of(EN_GB, EL_GR);
  private static final String BUNDLE = "com.eurobank.omega.error.messages";
  private final Map<Locale, ResourceBundle> bundles;

  /** Loads both immutable locale bundles for subsequent non-I/O lookups. */
  public ErrorMessages() {
    ResourceBundle.Control control =
        ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES);
    bundles =
        Map.of(
            EN_GB,
            ResourceBundle.getBundle(BUNDLE, EN_GB, control),
            EL_GR,
            ResourceBundle.getBundle(BUNDLE, EL_GR, control));
  }

  /**
   * Resolves catalogue text for the negotiated supported language, defaulting to en-GB.
   *
   * @param error typed catalogue constant
   * @param acceptLanguage optional Accept-Language header
   * @return safe text and its actual locale
   */
  public LocalizedError resolve(OmegaBusinessError error, String acceptLanguage) {
    Objects.requireNonNull(error, "error");
    Locale locale = negotiate(acceptLanguage);
    ResourceBundle bundle = bundles.get(locale);
    String key = error.name();
    return new LocalizedError(
        locale,
        bundle.getString(key + ".title"),
        bundle.getString(key + ".detail"),
        bundle.getString(key + ".resolution"));
  }

  private static Locale negotiate(String acceptLanguage) {
    if (acceptLanguage == null || acceptLanguage.isBlank()) {
      return EN_GB;
    }
    try {
      List<Locale> matches = Locale.filter(Locale.LanguageRange.parse(acceptLanguage), SUPPORTED);
      return matches.isEmpty() ? EN_GB : matches.getFirst();
    } catch (IllegalArgumentException invalidHeader) {
      return EN_GB;
    }
  }
}
