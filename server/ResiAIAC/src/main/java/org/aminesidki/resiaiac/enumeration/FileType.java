package org.aminesidki.resiaiac.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileType {
  IMAGE("images"),
  CIN("CINs"),
  DIPLOMA("diplomes");

  private final String bucketName;
}
