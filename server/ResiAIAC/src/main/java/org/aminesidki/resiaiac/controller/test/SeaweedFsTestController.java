package org.aminesidki.resiaiac.controller.test;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.aminesidki.resiaiac.service.SeaweedFsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/seaweed-fs-test")
public class SeaweedFsTestController {
  private final SeaweedFsService seaweedFsService;

  @PostMapping("/{value}")
  @ResponseStatus(HttpStatus.OK)
  public void post(@PathVariable String value) {
    seaweedFsService.createBucket(value);
  }

  @GetMapping("/{bucket}/{key}")
  public ResponseEntity<?> get(@PathVariable String bucket, @PathVariable String key) {
    return ResponseEntity.ok(seaweedFsService.getFileUrl(bucket, key, 300));
  }

  @PostMapping("/{bucket}/{key}")
  @ResponseStatus(HttpStatus.OK)
  public void post(
      @PathVariable String bucket,
      @PathVariable String key,
      @RequestParam("file") MultipartFile file)
      throws IOException {
    seaweedFsService.uploadFile(bucket, key, file);
  }

  @DeleteMapping("/{bucket}/{key}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable String bucket, @PathVariable String key) {
    seaweedFsService.deleteFile(bucket, key);
  }
}
