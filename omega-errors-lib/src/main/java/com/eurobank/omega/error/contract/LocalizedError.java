package com.eurobank.omega.error.contract;

import java.util.Locale;

/**
 * Holds client-safe text and the actual negotiated locale.
 *
 * @param locale selected supported locale
 * @param title short localised title
 * @param detail safe fixed detail
 * @param resolution safe fixed remediation
 */
public record LocalizedError(Locale locale, String title, String detail, String resolution) {}
