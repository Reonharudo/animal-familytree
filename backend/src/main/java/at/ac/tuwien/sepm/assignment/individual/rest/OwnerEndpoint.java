package at.ac.tuwien.sepm.assignment.individual.rest;

import at.ac.tuwien.sepm.assignment.individual.dto.OwnerCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerSearchDto;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.service.OwnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

@RestController
@RequestMapping(OwnerEndpoint.BASE_PATH)
public class OwnerEndpoint {
  static final String BASE_PATH = "/owners";
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final OwnerService service;

  public OwnerEndpoint(OwnerService service) {
    this.service = service;
  }

  @GetMapping
  public Stream<OwnerDto> search(OwnerSearchDto searchParameters) {
    LOG.info("GET " + BASE_PATH + " query parameters: {}", searchParameters);
    return service.search(searchParameters);
  }

  @PostMapping
  public ResponseEntity<OwnerDto> create(@RequestBody OwnerCreateDto ownerCreateDto) {
    LOG.info("POST " + BASE_PATH + " create parameters: {}", ownerCreateDto);
    try {
      return new ResponseEntity<>(service.create(ownerCreateDto), HttpStatus.CREATED);
    } catch (ValidationException e) {
      HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }
}
