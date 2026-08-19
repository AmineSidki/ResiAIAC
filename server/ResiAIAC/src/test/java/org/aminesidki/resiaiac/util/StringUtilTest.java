package org.aminesidki.resiaiac.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.aminesidki.resiaiac.exception.InvalidNameException;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link StringUtil}. */
class StringUtilTest {

  // ---------- nameCasing ----------

  @Test
  void nameCasing_shouldCapitalizeFirstLetterOnly() {
    assertThat(StringUtil.nameCasing("sidki")).isEqualTo("Sidki");
  }

  @Test
  void nameCasing_shouldLeaveAlreadyCapitalizedFirstLetterUnchanged() {
    assertThat(StringUtil.nameCasing("Sidki")).isEqualTo("Sidki");
  }

  @Test
  void nameCasing_shouldNotAlterCasingOfRemainingLetters() {
    // only the first letter is touched — an all-caps or mixed-case tail passes through as-is
    assertThat(StringUtil.nameCasing("sIDKI")).isEqualTo("SIDKI");
  }

  @Test
  void nameCasing_shouldHandleSingleCharacterInput() {
    assertThat(StringUtil.nameCasing("a")).isEqualTo("A");
  }

  @Test
  void nameCasing_shouldThrowOnEmptyString() {
    assertThatExceptionOfType(StringIndexOutOfBoundsException.class)
        .isThrownBy(() -> StringUtil.nameCasing(""));
  }

  @Test
  void nameCasing_shouldThrowOnNull() {
    assertThatExceptionOfType(InvalidNameException.class)
        .isThrownBy(() -> StringUtil.nameCasing(null));
  }

  // ---------- nameToUsername ----------

  @Test
  void nameToUsername_shouldConcatenateCasedNomAndPrenom() {
    assertThat(StringUtil.nameToUsername("Sidki", "Amine")).isEqualTo("SidkiAmine");
  }

  @Test
  void nameToUsername_shouldNormalizeCasingRegardlessOfInputCase() {
    assertThat(StringUtil.nameToUsername("SIDKI", "amine")).isEqualTo("SidkiAmine");
  }

  @Test
  void nameToUsername_shouldThrowOnBlankNom() {
    assertThatExceptionOfType(StringIndexOutOfBoundsException.class)
        .isThrownBy(() -> StringUtil.nameToUsername("", "Amine"));
  }

  @Test
  void nameToUsername_shouldThrowOnBlankPrenom() {
    assertThatExceptionOfType(StringIndexOutOfBoundsException.class)
        .isThrownBy(() -> StringUtil.nameToUsername("Sidki", ""));
  }

  @Test
  void nameToUsername_shouldThrowOnNullNom() {
    assertThatExceptionOfType(InvalidNameException.class)
        .isThrownBy(() -> StringUtil.nameToUsername(null, "Amine"));
  }
}
