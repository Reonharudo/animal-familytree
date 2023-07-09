package at.ac.tuwien.sepm.assignment.individual.dto;

/**
 * DTO for creation of owner
 *
 * @param firstName of owner
 * @param lastName  of owner
 * @param email     of owner
 */
public record OwnerCreateDto(
    String firstName,
    String lastName,
    String email
) {
}
