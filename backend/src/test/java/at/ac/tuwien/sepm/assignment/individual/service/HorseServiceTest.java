package at.ac.tuwien.sepm.assignment.individual.service;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
public class HorseServiceTest {

  @Autowired
  HorseService horseService;

  @Test
  public void getByIdOfNotExistingHorse() {
    NotFoundException actual = assertThrows(NotFoundException.class, () -> {
      horseService.getById(-999);
    });
    assertEquals("No horse with ID -999 found", actual.getMessage());
  }

  @Test
  public void getAllReturnsAllStoredHorsesAndVerifyIdAndSex() {
    List<HorseListDto> horses = horseService.allHorses()
        .toList();
    assertThat(horses.size()).isGreaterThanOrEqualTo(9);
    assertThat(horses)
        .extracting(HorseListDto::id, HorseListDto::name, HorseListDto::description, HorseListDto::dateOfBirth, HorseListDto::sex, HorseListDto::femaleParentId,
            HorseListDto::maleParentId, HorseListDto::ownerId)
        .contains(tuple(-10L, "Prinzessin Peach", "eating mushrooms", LocalDate.parse("1980-10-10"), Sex.FEMALE, null, null, -30L));
    assertThat(horses)
        .extracting(HorseListDto::id, HorseListDto::name, HorseListDto::description, HorseListDto::dateOfBirth, HorseListDto::sex, HorseListDto::femaleParentId,
            HorseListDto::maleParentId, HorseListDto::ownerId)
        .contains(tuple(-1L, "Wendy", null, LocalDate.parse("2000-12-12"), Sex.FEMALE, -10L, null, -30L));
    assertThat(horses)
        .extracting(HorseListDto::id, HorseListDto::name, HorseListDto::description, HorseListDto::dateOfBirth, HorseListDto::sex, HorseListDto::femaleParentId,
            HorseListDto::maleParentId, HorseListDto::ownerId)
        .contains(tuple(-2L, "Test Mother", "The famous mother", LocalDate.parse("2020-09-10"), Sex.FEMALE, -1L, null, -22L));
    assertThat(horses)
        .extracting(HorseListDto::id, HorseListDto::name, HorseListDto::description, HorseListDto::dateOfBirth, HorseListDto::sex, HorseListDto::femaleParentId,
            HorseListDto::maleParentId, HorseListDto::ownerId)
        .contains(tuple(-3L, "Test Father", "The famous father", LocalDate.parse("2022-09-10"), Sex.MALE, null, null, -22L));
    assertThat(horses)
        .extracting(HorseListDto::id, HorseListDto::name, HorseListDto::description, HorseListDto::dateOfBirth, HorseListDto::sex, HorseListDto::femaleParentId,
            HorseListDto::maleParentId, HorseListDto::ownerId)
        .contains(tuple(-5L, "Test Son", "I like horses", LocalDate.parse("2022-10-10"), Sex.MALE, -2L, -3L, -20L));
    assertThat(horses)
        .extracting(HorseListDto::id, HorseListDto::name, HorseListDto::description, HorseListDto::dateOfBirth, HorseListDto::sex, HorseListDto::femaleParentId,
            HorseListDto::maleParentId, HorseListDto::ownerId)
        .contains(tuple(-7L, "Yuki Herbert", "My hobby is singing", LocalDate.parse("2022-10-10"), Sex.FEMALE, -2L, null, -29L));
    assertThat(horses)
        .extracting(HorseListDto::id, HorseListDto::name, HorseListDto::description, HorseListDto::dateOfBirth, HorseListDto::sex, HorseListDto::femaleParentId,
            HorseListDto::maleParentId, HorseListDto::ownerId)
        .contains(tuple(-9L, "König Arthur I", "My hobby is to reign", LocalDate.parse("1980-05-10"), Sex.MALE, null, null, -24L));
    assertThat(horses)
        .extracting(HorseListDto::id, HorseListDto::name, HorseListDto::description, HorseListDto::dateOfBirth, HorseListDto::sex, HorseListDto::femaleParentId,
            HorseListDto::maleParentId, HorseListDto::ownerId)
        .contains(tuple(-8L, "König Arthur II", "Army of horses", LocalDate.parse("2000-10-10"), Sex.MALE, null, -9L, null));
  }
}
