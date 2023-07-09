package at.ac.tuwien.sepm.assignment.individual.service;

import at.ac.tuwien.sepm.assignment.individual.dto.DeletedDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseAncestryDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;

import java.util.stream.Stream;

/**
 * Service for working with horses.
 */
public interface HorseService {
  /**
   * Lists all horses stored in the system.
   *
   * @return list of all stored horses
   */
  Stream<HorseListDto> allHorses();

  /**
   * Updates the horse with the ID given in {@code horse}
   * with the data given in {@code horse}
   * in the persistent data store.
   *
   * @param horse the horse to update
   * @return he updated horse
   * @throws NotFoundException   if the horse with given ID does not exist in the persistent data store
   * @throws ValidationException if the update data given for the horse is in itself incorrect (description too long, no name, …)
   * @throws ConflictException   if the update data given for the horse is in conflict the data currently in the system (owner does not exist, …)
   */
  HorseDetailDto update(HorseDetailDto horse) throws NotFoundException, ValidationException, ConflictException;

  /**
   * Get the horse with given ID, with more detail information.
   * This includes the owner of the horse, and its parents.
   * The parents of the parents are not included.
   *
   * @param id the ID of the horse to get
   * @return the horse with ID {@code id}
   * @throws NotFoundException if the horse with the given ID does not exist in the persistent data store
   */
  HorseDetailDto getById(long id) throws NotFoundException;

  /**
   * Create a new horse in the persistent data store.
   *
   * @param newHorse the data for the new horse
   * @return the horse, that was just newly created in the persistent data store
   * @throws ValidationException if man
   */
  HorseDetailDto create(HorseCreateDto newHorse) throws ValidationException, ConflictException;

  /**
   * Deletes horse by given id
   *
   * @param id of horse
   * @return id of deleted horse
   * @throws NotFoundException when horse with given id does not exist
   */
  DeletedDto deleteById(long id) throws NotFoundException;

  /**
   * Search for horses matching the criteria in {@code searchParameters}.
   * <p>
   * A horse is considered matched, if its equal to given values in {@code searchParameters}. If string
   * properties are matched against each other only the substring is matched in a case unsensitive way.
   * The returned stream of horses never contains more than {@code searchParameters.limit} elements,
   * even if there would be more matches in the persistent data store.
   * </p>
   *
   * @param searchParameters object containing the search parameters to match
   * @return a stream containing horses matching the criteria in {@code searchParameters}
   */
  Stream<HorseListDto> searchHorses(HorseSearchDto searchParameters) throws ValidationException, NotFoundException;

  /**
   * Get all forbears of given horse. A List of forbears of specified horse is returned
   *
   * @param horseAncestryDto of horse from which an ancestry lists should be retrieved
   * @return list of forebears of specified horse
   * @throws ConflictException   when a horse with given id {@code ancestryDto} does not exist
   * @throws ValidationException when properties of {@code ancestryDto} has not valid values
   * @throws NotFoundException   if the horse with the given ID does not exist in the persistent data store
   */
  Stream<HorseListDto> ancestryOf(HorseAncestryDto horseAncestryDto) throws ConflictException, ValidationException, NotFoundException;
}
