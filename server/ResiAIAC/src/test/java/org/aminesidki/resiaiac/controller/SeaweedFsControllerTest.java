package org.aminesidki.resiaiac.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.aminesidki.resiaiac.service.SeaweedFsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Unit tests for {@link SeaweedFsController}, exercised through {@link MockMvc} against a sliced
 * web context.
 *
 * <p>{@link SeaweedFsService} is mocked; only request routing and status codes are under test here
 * — actual bucket handling is covered separately by {@code SeaweedFsServiceTest}.
 *
 * <p>Security filters are disabled ({@code addFilters = false}), so the class-level
 * {@code @PreAuthorize("hasAnyRole('RESPONSABLE')")} restriction is not exercised here.
 */
@WebMvcTest(SeaweedFsController.class)
@AutoConfigureMockMvc(addFilters = false)
class SeaweedFsControllerTest {

  private static final String BASE_PATH = "/api/v1/seaweed-fs/bucket";

  @Autowired private MockMvc mockMvc;

  @MockitoBean private SeaweedFsService seaweedFsService;

  // ---------- createBucket ----------

  @Test
  void createBucket_shouldReturnOkAndInvokeService() throws Exception {
    mockMvc.perform(post(BASE_PATH + "/{bucketName}", "cin")).andExpect(status().isOk());

    verify(seaweedFsService, times(1)).createBucket("cin");
    verifyNoMoreInteractions(seaweedFsService);
  }

  // ---------- deleteBucket ----------

  @Test
  void deleteBucket_shouldReturnOkAndInvokeService() throws Exception {
    mockMvc.perform(delete(BASE_PATH + "/{bucketName}", "cin")).andExpect(status().isOk());

    verify(seaweedFsService, times(1)).deleteBucket("cin");
    verifyNoMoreInteractions(seaweedFsService);
  }
}
