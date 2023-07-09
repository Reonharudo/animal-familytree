package at.ac.tuwien.sepm.assignment.individual.dto;

import at.ac.tuwien.sepm.assignment.individual.type.Sex;

import java.time.LocalDate;

/**
 * DTO used to return a detailed representation of a Horse object with in depth data
 * for associated Owner.
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
public record HorseDetailDto(
    Long id,
    String name,
    String description,
    LocalDate dateOfBirth,
    Sex sex,
    OwnerDto owner,

    Long femaleParentId,
    Long maleParentId
) {
  public HorseDetailDto withId(long newId) {
    return new HorseDetailDto(
        newId,
        name,
        description,
        dateOfBirth,
        sex,
        owner,
        femaleParentId,
        maleParentId);
  }

  public Long ownerId() {
    return owner == null
        ? null
        : owner.id();
  }
}
