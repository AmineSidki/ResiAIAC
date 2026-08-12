package org.aminesidki.resiaiac.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.aminesidki.resiaiac.entity.Batiment;
import org.aminesidki.resiaiac.exception.ResourceNotFoundException;
import org.aminesidki.resiaiac.repository.BatimentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link ResourceFetcher}.
 *
 * <p>Exercised against {@link BatimentRepository} (a concrete {@code JpaRepository<Batiment,
 * UUID>}) since the utility itself is generic and has no state of its own to isolate.
 */
@ExtendWith(MockitoExtension.class)
class ResourceFetcherTest {

  @Mock private BatimentRepository batimentRepository;

  private UUID id;
  private Batiment entity;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    entity = Batiment.builder().id(id).nom("Batiment A").build();
  }

  @Test
  void fetchResource_shouldReturnEntityWhenFound() {
    when(batimentRepository.findById(id)).thenReturn(Optional.of(entity));

    Batiment result = ResourceFetcher.fetchResource(id, batimentRepository, "Batiment");

    assertThat(result).isSameAs(entity);
  }

  @Test
  void fetchResource_shouldThrowResourceNotFoundExceptionWhenAbsent() {
    when(batimentRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> ResourceFetcher.fetchResource(id, batimentRepository, "Batiment"))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Resource not found: Batiment with id " + id);
  }

  @Test
  void fetchResource_shouldIncludeGivenResourceTypeAndIdInMessage() {
    UUID otherId = UUID.randomUUID();
    when(batimentRepository.findById(otherId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> ResourceFetcher.fetchResource(otherId, batimentRepository, "Chambre"))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Resource not found: Chambre with id " + otherId);
  }
}
