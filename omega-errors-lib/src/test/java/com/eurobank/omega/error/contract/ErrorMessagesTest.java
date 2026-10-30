package com.eurobank.omega.error.contract;

import java.util.Locale;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/** Checks complete safe English/Greek resources and stateless language negotiation. */
class ErrorMessagesTest {
  private final ErrorMessages messages = new ErrorMessages();

  @ParameterizedTest
  @EnumSource(OmegaBusinessError.class)
  void everyCodeHasBothLocales(OmegaBusinessError error) {
    for (String language : new String[] {"en-GB", "el-GR"}) {
      LocalizedError result = messages.resolve(error, language);
      Assertions.assertEquals(Locale.forLanguageTag(language), result.locale());
      Assertions.assertFalse(result.title().isBlank());
      Assertions.assertFalse(result.detail().isBlank());
      Assertions.assertFalse(result.resolution().isBlank());
      Assertions.assertFalse(result.detail().contains("{0}"));
    }
    Assertions.assertTrue(
        messages
            .resolve(error, "el-GR")
            .title()
            .codePoints()
            .anyMatch(value -> Character.UnicodeScript.of(value) == Character.UnicodeScript.GREEK));
  }

  @Test
  void negotiatesPreferenceAndFallsBackForUnknownOrMalformedInput() {
    OmegaBusinessError error = OmegaBusinessError.ONBOARDING_SPECIFICATION_INVALID;
    Assertions.assertEquals(
        "el-GR",
        messages.resolve(error, "fr-FR, el-GR;q=0.8, en-GB;q=0.5").locale().toLanguageTag());
    Assertions.assertEquals("en-GB", messages.resolve(error, "fr-FR").locale().toLanguageTag());
    Assertions.assertEquals(
        "en-GB", messages.resolve(error, "not a valid header ;;").locale().toLanguageTag());
    Assertions.assertEquals("en-GB", messages.resolve(error, null).locale().toLanguageTag());
    Assertions.assertEquals("en-GB", messages.resolve(error, " ").locale().toLanguageTag());
    Assertions.assertEquals("el-GR", messages.resolve(error, "el").locale().toLanguageTag());
    Assertions.assertEquals(
        "en-GB", messages.resolve(error, "el-GR;q=0, en-GB;q=1").locale().toLanguageTag());
  }
}
