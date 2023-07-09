package at.ac.tuwien.sepm.assignment.individual.service;

import at.ac.tuwien.sepm.assignment.individual.dto.OwnerCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerDto;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
public class OwnerServiceTest {
  @Autowired
  OwnerService ownerService;

  @Test
  public void createOwnerSuccessfully() {
    OwnerCreateDto createOwner = new OwnerCreateDto("Henry", "Ford", "test1234999@tuwien.ac.com");

    assertDoesNotThrow(() -> {
      OwnerDto persistedOwner = ownerService.create(createOwner);
      assertThat(persistedOwner).isNotNull();
      assertEquals(createOwner.firstName(), persistedOwner.firstName());
      assertEquals(createOwner.lastName(), persistedOwner.lastName());
      assertEquals(createOwner.email(), persistedOwner.email());
    });
  }

  @Test
  public void createOwnerWithInvalidEmail_shouldThrowError() {
    OwnerCreateDto createOwner = new OwnerCreateDto("Henry", "Ford", "wrongEmail");

    ValidationException actual = assertThrows(ValidationException.class, () -> {
      ownerService.create(createOwner);
    });

    assertEquals(actual.getMessage(), "Validation of owner for insert failed. Failed validations: "
        + "E-Mail string is not formatted as an email, it must be formatted like [text]@[text].[domain].");
  }
}
