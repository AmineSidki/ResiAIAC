package org.aminesidki.resiaiac.util;

public class StringUtil {
  public static String nameCasing(String str) {
    return str.substring(0, 1).toUpperCase() + str.substring(1);
  }

  public static String nameToUsername(String nom, String prenom) {
    return nameCasing(nom.toLowerCase()) + nameCasing(prenom.toLowerCase());
  }
}
