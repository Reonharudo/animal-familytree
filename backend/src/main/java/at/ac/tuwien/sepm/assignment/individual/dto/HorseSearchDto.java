package at.ac.tuwien.sepm.assignment.individual.dto;

import at.ac.tuwien.sepm.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.time.LocalDate;

/**
 * DTO to bundle the query parameters used in searching horses.
 * Each field can be null, in which case this field is not filtered by.
 *
 * @param name             substring case-insensitive of horse name
 * @param description      substring case-insensitive of horse description
 * @param bornBefore       date to filter horses that were born before specified date
 * @param bornAfter        date to filter horses that were born after specified date
 * @param sex              binary biological sex of horse
 * @param ownerName        substring case-insensitive of owner firstname and/or lastname
 * @param excludeHorseById id of horse that is omitted from the result
 * @param limit            maximum number of horses
 */
public record HorseSearchDto(
    String name,
    String description,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate bornBefore,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate bornAfter,
    Sex sex,
    String ownerName,
    Long excludeHorseById,
    Integer limit
) {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /**
   * Returns true if all the fields of this record has null as value
   *
   * @return true if only null values are present in the fields otherwise false is returned
   */
  public boolean isEmpty() {
    LOG.trace("isEmpty()");
    for (Field field : this.getClass().getDeclaredFields()) {
      try {
        if (field.get(this) != null && (!field.getName().equals("LOG"))) {
          LOG.debug("public field {} is not null", field.getName());
          return false;
        }
      } catch (IllegalAccessException e) {
        throw new FatalException("Internal Error for checking if object has any null values failed " + e.getMessage());
      }
    }
    LOG.debug("public fields has only null values");
    return true;
  }

  /**
   * Returns if this record has any properties set that are contradicting each other
   *
   * @return true if bornBefore and bornAfter are not null, otherwise false is returned
   */
  public boolean isContradicting() {
    LOG.trace("isContradicting()");
    return bornBefore != null && bornAfter != null;
  }
}
