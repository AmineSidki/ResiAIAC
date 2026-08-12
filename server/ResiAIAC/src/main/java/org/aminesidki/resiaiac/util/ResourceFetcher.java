package org.aminesidki.resiaiac.util;

import org.aminesidki.resiaiac.exception.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public class ResourceFetcher {
  public static <T, ID> T fetchResource(
      ID id, JpaRepository<T, ID> repository, String resourceType) {
    return repository
        .findById(id)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    "Resource not found: " + resourceType + " with id " + id));
  }
}
