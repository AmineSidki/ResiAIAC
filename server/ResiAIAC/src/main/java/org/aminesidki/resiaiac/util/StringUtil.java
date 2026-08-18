package org.aminesidki.resiaiac.util;

import org.aminesidki.resiaiac.exception.InvalidNameException;

public class StringUtil {
  public static String nameCasing(String str) {
    if (str == null) throw new InvalidNameException("Given name is null");
    return str.substring(0, 1).toUpperCase() + str.substring(1);
  }

  public static String nameToUsername(String nom, String prenom) {
    if (nom == null) throw new InvalidNameException("Given attribute nom is null");
    if (prenom == null) throw new InvalidNameException("Given attribute prenom is null");

    return nameCasing(nom.toLowerCase()) + nameCasing(prenom.toLowerCase());
  }
}
