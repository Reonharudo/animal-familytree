package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.DeletedDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseAncestryDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.mapper.HorseMapper;
import at.ac.tuwien.sepm.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepm.assignment.individual.service.HorseService;
import at.ac.tuwien.sepm.assignment.individual.service.OwnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class HorseServiceImpl implements HorseService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final HorseDao dao;
  private final HorseMapper mapper;
  private final HorseValidator validator;
  private final OwnerService ownerService;

  public HorseServiceImpl(HorseDao dao, HorseMapper mapper, HorseValidator validator, OwnerService ownerService) {
    this.dao = dao;
    this.mapper = mapper;
    this.validator = validator;
    this.ownerService = ownerService;
  }

  @Override
  public Stream<HorseListDto> allHorses() {
    LOG.trace("allHorses()");
    var horses = dao.getAll();
    var ownerIds = horses.stream()
        .map(Horse::getOwnerId)
        .filter(Objects::nonNull)
        .collect(Collectors.toUnmodifiableSet());
    Map<Long, OwnerDto> ownerMap;
    try {
      ownerMap = ownerService.getAllById(ownerIds);
    } catch (NotFoundException e) {
      throw new FatalException("Horse, that is already persisted, refers to non-existing owner", e);
    }
    return horses.stream()
        .map(horse -> mapper.entityToListDto(horse, ownerMap));
  }


  @Override
  public HorseDetailDto update(HorseDetailDto horse) throws ConflictException, ValidationException, NotFoundException {
    LOG.trace("update({})", horse);
    validator.validateForUpdate(horse);
    var updatedHorse = dao.update(horse);
    return mapper.entityToDetailDto(
        updatedHorse,
        ownerMapForSingleId(updatedHorse.getOwnerId()));
  }

  @Override
  public DeletedDto deleteById(long id) throws NotFoundException {
    LOG.trace("deleteById({})", id);
    return dao.deleteById(id);
  }

  @Override
  public Stream<HorseListDto> searchHorses(HorseSearchDto searchParameters) throws ValidationException, NotFoundException {
    LOG.trace("searchHorses({})", searchParameters);
    validator.validateSearch(searchParameters);
    if (!searchParameters.isEmpty()) {
      var horses = dao.search(searchParameters);
      var ownerIds = horses.stream()
          .map(Horse::getOwnerId)
          .filter(Objects::nonNull)
          .collect(Collectors.toUnmodifiableSet());
      Map<Long, OwnerDto> ownerMap;
      try {
        ownerMap = ownerService.getAllById(ownerIds);
      } catch (NotFoundException e) {
        throw new FatalException("Horse, that is already persisted, refers to non-existing owner", e);
      }
      return horses.stream()
          .map(horse -> mapper.entityToListDto(horse, ownerMap));
    } else {
      return allHorses();
    }
  }

  @Override
  public Stream<HorseListDto> ancestryOf(HorseAncestryDto ancestryDto) throws ValidationException, ConflictException, NotFoundException {
    LOG.trace("ancestryOf({})", ancestryDto);
    validator.validateForAncestry(ancestryDto);

    LOG.debug("Check if passed id of horse does exist {}", ancestryDto.id());
    var horses = dao.ancestryOf(ancestryDto);
    var ownerIds = horses.stream()
        .map(Horse::getOwnerId)
        .filter(Objects::nonNull)
        .collect(Collectors.toUnmodifiableSet());
    Map<Long, OwnerDto> ownerMap;
    try {
      ownerMap = ownerService.getAllById(ownerIds);
    } catch (NotFoundException e) {
      throw new FatalException("Horse, that is already persisted, refers to non-existing owner", e);
    }
    return horses.stream().map(horse -> mapper.entityToListDto(horse, ownerMap));
  }


  @Override
  public HorseDetailDto getById(long id) throws NotFoundException {
    LOG.trace("details({})", id);
    Horse horse = dao.getById(id);
    return mapper.entityToDetailDto(
        horse,
        ownerMapForSingleId(horse.getOwnerId()));
  }

  @Override
  public HorseDetailDto create(HorseCreateDto newHorse) throws ValidationException, ConflictException {
    LOG.trace("create({}", newHorse.toString());
    validator.validateForInsert(newHorse);
    var result = dao.create(newHorse);
    try {
      return getById(result.getId());
    } catch (NotFoundException e) {
      LOG.error("Horse should have been created, but was not {}", e.getMessage());
      throw new FatalException("Horse should have been created, but was not " + e.getMessage());
    }
  }


  private Map<Long, OwnerDto> ownerMapForSingleId(Long ownerId) {
    LOG.trace("ownerMapForSingleId({})", ownerId);
    try {
      return ownerId == null
          ? null
          : Collections.singletonMap(ownerId, ownerService.getById(ownerId));
    } catch (NotFoundException e) {
      throw new FatalException("Owner %d referenced by horse not found".formatted(ownerId));
    }
  }

}
