package at.ac.tuwien.sepm.assignment.individual.dto;

import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;

/**
 * Class for Horse DTOs
 * Contains all common properties
 *
 * @param id             of horse
 * @param name           of horse
 * @param description    of horse
 * @param dateOfBirth    date of birth of horse
 * @param sex            binary biological sex of horse
 * @param owner          of horse
 * @param femaleParentId id of female parent horse
 * @param maleParentId   id of male parent horse
 */
public record HorseListDto(
    Long id,
    String name,
    String description,
    LocalDate dateOfBirth,
    Sex sex,
    OwnerDto owner,
    Long femaleParentId,
    Long maleParentId
) {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /**
   * Returns id of owner if present, otherwise null
   *
   * @return id of owner if present, otherwise null
   */
  public Long ownerId() {
    LOG.trace("ownerId()");
    return owner == null
        ? null
        : owner.id();
  }
}
