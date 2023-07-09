package at.ac.tuwien.sepm.assignment.individual.rest;

import at.ac.tuwien.sepm.assignment.individual.dto.DeletedDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseAncestryDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.service.HorseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

@RestController
@RequestMapping(path = HorseEndpoint.BASE_PATH)
public class HorseEndpoint {
  static final String BASE_PATH = "/horses";
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final HorseService service;

  public HorseEndpoint(HorseService service) {
    this.service = service;
  }

  @GetMapping
  public Stream<HorseListDto> search(HorseSearchDto searchParameters) {
    LOG.info("GET" + BASE_PATH + searchParameters);
    try {
      return service.searchHorses(searchParameters);
    } catch (ValidationException e) {
      HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
      logClientError(status, "Search of Horses failed", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Search of Horses failed", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  @GetMapping("{id}/ancestry")
  public Stream<HorseListDto> ancestryOf(HorseAncestryDto searchParameters) {
    LOG.info("GET" + BASE_PATH + searchParameters);
    try {
      return service.ancestryOf(searchParameters);
    } catch (ConflictException e) {
      HttpStatus status = HttpStatus.CONFLICT;
      logClientError(status, "Ancestry of Horse failed due to conflicts", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (ValidationException e) {
      HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
      logClientError(status, "Ancestry of Horse failed due to invalid properties", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Ancestry of Horses failed due to not existing ressource", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  @GetMapping("{id}")
  public HorseDetailDto getById(@PathVariable long id) {
    LOG.info("GET " + BASE_PATH + "/{}", id);
    try {
      return service.getById(id);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse to get details of not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  @PostMapping
  public ResponseEntity<HorseDetailDto> create(@RequestBody HorseCreateDto horseDetailDto) {
    LOG.info("POST " + BASE_PATH + "/{}", horseDetailDto);
    try {
      return new ResponseEntity<>(service.create(horseDetailDto), HttpStatus.CREATED);
    } catch (ValidationException e) {
      HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
      logClientError(status, "Horse could not be created, because validation failed", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (ConflictException e) {
      HttpStatus status = HttpStatus.CONFLICT;
      logClientError(status, "Horse to create has conflicting properties", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  @PutMapping("{id}")
  public HorseDetailDto update(@PathVariable long id, @RequestBody HorseDetailDto toUpdate) {
    LOG.info("PUT " + BASE_PATH + "/{}", toUpdate);
    try {
      return service.update(toUpdate.withId(id));
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse to update not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (ConflictException e) {
      HttpStatus status = HttpStatus.CONFLICT;
      logClientError(status, "Horse to update has conflicting properties", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (ValidationException e) {
      HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
      logClientError(status, "Horse to update failed due to validation", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  @DeleteMapping("{id}")
  public DeletedDto delete(@PathVariable long id) {
    LOG.info("DELETE " + BASE_PATH + "/{}", id);
    try {
      return service.deleteById(id);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse to delete not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  private void logClientError(HttpStatus status, String message, Exception e) {
    LOG.warn("{} {}: {}: {}", status.value(), message, e.getClass().getSimpleName(), e.getMessage());
  }
}
