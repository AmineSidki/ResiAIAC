package org.aminesidki.resiaiac.controller;

import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.service.SeaweedFsService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('RESPONSABLE')")
@RequestMapping("/api/v1/seaweed-fs/bucket")
public class SeaweedFsController {
  private final SeaweedFsService seaweedFsService;

  @PostMapping("/{bucketName}")
  @ResponseStatus(HttpStatus.OK)
  public void createBucket(@PathVariable String bucketName) {
    seaweedFsService.createBucket(bucketName);
  }

  @DeleteMapping("/{bucketName}")
  @ResponseStatus(HttpStatus.OK)
  public void deleteBucket(@PathVariable String bucketName) {
    seaweedFsService.deleteBucket(bucketName);
  }
}
