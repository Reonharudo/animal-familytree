package at.ac.tuwien.sepm.assignment.individual.dto;

/**
 * DTO for specifying how to filter ancestry tree of horse
 *
 * @param id    of horse
 * @param limit is equal to generation or family depth
 */
public record HorseAncestryDto(
    Long id,
    Integer limit
) {
}