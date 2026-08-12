package org.aminesidki.resiaiac.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.aminesidki.resiaiac.dto.ServiceDto;
import org.aminesidki.resiaiac.entity.Service;
import org.aminesidki.resiaiac.mapper.ServiceMapper;
import org.aminesidki.resiaiac.repository.ServiceRepository;
import org.aminesidki.resiaiac.service.impl.ServiceServiceImpl;
import org.aminesidki.resiaiac.util.ResourceFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link ServiceService}, exercised through its {@link ServiceServiceImpl}
 * implementation.
 *
 * <p>The service is wired manually (rather than via {@code @InjectMocks}) so the field under test
 * can be declared against the interface type.
 *
 * <p>ResourceFetcher.fetchResource is a static method, mocked per-test with Mockito's mockStatic
 * (requires Mockito 5+ / mockito-inline).
 */
@ExtendWith(MockitoExtension.class)
class ServiceServiceTest {

  @Mock private ServiceRepository serviceRepository;

  @Mock private ServiceMapper serviceMapper;

  private ServiceService serviceService;

  private Long id;
  private Service entity;
  private ServiceDto dto;

  @BeforeEach
  void setUp() {
    serviceService = new ServiceServiceImpl(serviceRepository, serviceMapper);

    id = 1L;
    entity = Service.builder().id(id).nom("Plomberie").build();
    dto = new ServiceDto(id, "Plomberie", List.of());
  }

  // ---------- save ----------

  @Test
  void save_shouldMapPersistAndReturnDto() {
    ServiceDto inputDto = new ServiceDto(null, "Electricite", List.of());
    Service mappedEntity = Service.builder().nom("Electricite").build();
    Service savedEntity = Service.builder().id(id).nom("Electricite").build();
    ServiceDto resultDto = new ServiceDto(id, "Electricite", List.of());

    when(serviceMapper.toEntity(inputDto)).thenReturn(mappedEntity);
    when(serviceRepository.save(mappedEntity)).thenReturn(savedEntity);
    when(serviceMapper.toDto(savedEntity)).thenReturn(resultDto);

    ServiceDto result = serviceService.save(inputDto);

    assertThat(result).isEqualTo(resultDto);
    verify(serviceMapper).toEntity(inputDto);
    verify(serviceRepository).save(mappedEntity);
    verify(serviceMapper).toDto(savedEntity);
    verifyNoMoreInteractions(serviceRepository, serviceMapper);
  }

  // ---------- getById ----------

  @Test
  void getById_shouldFetchAndReturnDto() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, serviceRepository, "Service"))
          .thenReturn(entity);
      when(serviceMapper.toDto(entity)).thenReturn(dto);

      ServiceDto result = serviceService.getById(id);

      assertThat(result).isEqualTo(dto);
      fetcher.verify(() -> ResourceFetcher.fetchResource(id, serviceRepository, "Service"));
      verify(serviceMapper).toDto(entity);
      verifyNoMoreInteractions(serviceMapper);
    }
  }

  @Test
  void getById_shouldPropagateExceptionWhenNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Service not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, serviceRepository, "Service"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> serviceService.getById(id)).isSameAs(notFound);

      verifyNoMoreInteractions(serviceMapper);
    }
  }

  // ---------- update ----------

  @Test
  void update_shouldFetchMutateSaveAndReturnDto() {
    Service savedEntity = Service.builder().id(id).nom("Plomberie - renamed").build();
    ServiceDto resultDto = new ServiceDto(id, "Plomberie - renamed", List.of());

    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, serviceRepository, "Service"))
          .thenReturn(entity);
      when(serviceRepository.save(entity)).thenReturn(savedEntity);
      when(serviceMapper.toDto(savedEntity)).thenReturn(resultDto);

      ServiceDto result = serviceService.update(id, dto);

      assertThat(result).isEqualTo(resultDto);
      fetcher.verify(() -> ResourceFetcher.fetchResource(id, serviceRepository, "Service"));
      verify(serviceMapper).updateEntityFromDto(dto, entity);
      verify(serviceRepository).save(entity);
      verify(serviceMapper).toDto(savedEntity);
      verifyNoMoreInteractions(serviceRepository, serviceMapper);
    }
  }

  @Test
  void update_shouldNotSaveWhenResourceNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Service not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, serviceRepository, "Service"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> serviceService.update(id, dto)).isSameAs(notFound);

      verify(serviceRepository, never()).save(any());
      verifyNoMoreInteractions(serviceMapper);
    }
  }

  // ---------- delete ----------

  @Test
  void delete_shouldFetchAndDeleteEntity() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, serviceRepository, "Service"))
          .thenReturn(entity);

      serviceService.delete(id);

      fetcher.verify(() -> ResourceFetcher.fetchResource(id, serviceRepository, "Service"));
      verify(serviceRepository, times(1)).delete(entity);
      verifyNoMoreInteractions(serviceRepository);
    }
  }

  @Test
  void delete_shouldNotDeleteWhenResourceNotFound() {
    try (MockedStatic<ResourceFetcher> fetcher = mockStatic(ResourceFetcher.class)) {
      RuntimeException notFound = new RuntimeException("Service not found");
      fetcher
          .when(() -> ResourceFetcher.fetchResource(id, serviceRepository, "Service"))
          .thenThrow(notFound);

      assertThatThrownBy(() -> serviceService.delete(id)).isSameAs(notFound);

      verify(serviceRepository, never()).delete(any());
    }
  }
}
