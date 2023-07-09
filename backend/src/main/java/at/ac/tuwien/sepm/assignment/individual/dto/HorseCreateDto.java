package at.ac.tuwien.sepm.assignment.individual.dto;

import at.ac.tuwien.sepm.assignment.individual.type.Sex;

import java.time.LocalDate;

/**
 * DTO for creation of Horse.
 *
 * @param name           name of horse
 * @param description    of horse
 * @param dateOfBirth    birthdate of horse
 * @param sex            binary biological sex of horse
 * @param ownerId        id of the owner of the horse
 * @param femaleParentId id of the female parent
 * @param maleParentId   if of the male parent
 */
public record HorseCreateDto(
    String name,
    String description,
    LocalDate dateOfBirth,
    Sex sex,
    Long ownerId,
    Long femaleParentId,
    Long maleParentId
) {

}
