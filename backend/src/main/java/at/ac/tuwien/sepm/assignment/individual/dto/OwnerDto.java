package at.ac.tuwien.sepm.assignment.individual.dto;

/**
 * DTO representing an owner
 *
 * @param id        of owner
 * @param firstName of owner
 * @param lastName  of owner
 * @param email     of owner
 */
public record OwnerDto(
    long id,
    String firstName,
    String lastName,
    String email
) {
}
