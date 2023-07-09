package at.ac.tuwien.sepm.assignment.individual.persistence;

import at.ac.tuwien.sepm.assignment.individual.dto.DeletedDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseAncestryDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;

import java.util.List;

/**
 * Data Access Object for horses.
 * Implements access functionality to the application's persistent data store regarding horses.
 */
public interface HorseDao {
  /**
   * Get all horses stored in the persistent data store.
   *
   * @return a list of all stored horses
   */
  List<Horse> getAll();


  /**
   * Update the horse with the ID given in {@code horse}
   * with the data given in {@code horse}
   * in the persistent data store.
   *
   * @param horse the horse to update
   * @return the updated horse
   * @throws NotFoundException if the Horse with the given ID does not exist in the persistent data store
   */
  Horse update(HorseDetailDto horse) throws NotFoundException;

  /**
   * Get a horse by its ID from the persistent data store.
   *
   * @param id the ID of the horse to get
   * @return the horse
   * @throws NotFoundException if the Horse with the given ID does not exist in the persistent data store
   */
  Horse getById(long id) throws NotFoundException;

  /**
   * Create a horse with given properties in {@code newHorse}.
   *
   * @param newHorse horse that should be persisted
   * @return created horse entity
   */
  Horse create(HorseCreateDto newHorse);

  /**
   * Deletes horse of given id.
   *
   * @param id of horse that will be deleted
   * @return id of deleted horse
   * @throws NotFoundException when a horse with given id does not exist
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
   * @return a list containing horse entities matching the criteria in {@code searchParameters}
   */
  List<Horse> search(HorseSearchDto searchParameters);

  /**
   * Get all forbears of given horse. A List of forbears of specified horse is returned
   *
   * @param horseAncestryDto of horse from which an ancestry lists should be retrieved
   * @return list of forebears of specified horse
   * @throws NotFoundException when a horse with given id {@code ancestryDto} does not exist
   */
  List<Horse> ancestryOf(HorseAncestryDto horseAncestryDto) throws NotFoundException;

  /**
   * Get all horses where femaleParent equals to given femaleParentId
   *
   * @param femaleParentId id of femaleParent to match against
   * @return matched horses where femaleparentId matches
   */
  List<Horse> listWhereFemaleParentIs(long femaleParentId);

  /**
   * Get all horses where maleParentId equals to given maleParentId
   *
   * @param maleParentId id of femaleParent to match against
   * @return matched horses where maleParentId matches
   */
  List<Horse> listWhereMaleParentIs(long maleParentId);
}
