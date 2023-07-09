package at.ac.tuwien.sepm.assignment.individual.persistence;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

@ActiveProfiles({"test", "datagen"})
// enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
public class HorseDaoTest {
  @Autowired
  HorseDao horseDao;

  @Test
  public void getAllReturnsAllStoredHorses() {
    List<Horse> horses = horseDao.getAll();
    assertThat(horses.size()).isGreaterThanOrEqualTo(8);
    assertThat(horses).isNotNull();
    assertThat(horses)
        .extracting(Horse::getId, Horse::getName, Horse::getDescription, Horse::getDateOfBirth, Horse::getSex, Horse::getFemaleParentId, Horse::getMaleParentId,
            Horse::getOwnerId)
        .contains(tuple(-10L, "Prinzessin Peach", "eating mushrooms", LocalDate.parse("1980-10-10"), Sex.FEMALE, null, null, -30L));
    assertThat(horses)
        .extracting(Horse::getId, Horse::getName, Horse::getDescription, Horse::getDateOfBirth, Horse::getSex, Horse::getFemaleParentId, Horse::getMaleParentId,
            Horse::getOwnerId)
        .contains(tuple(-1L, "Wendy", null, LocalDate.parse("2000-12-12"), Sex.FEMALE, -10L, null, -30L));
    assertThat(horses)
        .extracting(Horse::getId, Horse::getName, Horse::getDescription, Horse::getDateOfBirth, Horse::getSex, Horse::getFemaleParentId, Horse::getMaleParentId,
            Horse::getOwnerId)
        .contains(tuple(-2L, "Test Mother", "The famous mother", LocalDate.parse("2020-09-10"), Sex.FEMALE, -1L, null, -22L));
    assertThat(horses)
        .extracting(Horse::getId, Horse::getName, Horse::getDescription, Horse::getDateOfBirth, Horse::getSex, Horse::getFemaleParentId, Horse::getMaleParentId,
            Horse::getOwnerId)
        .contains(tuple(-3L, "Test Father", "The famous father", LocalDate.parse("2022-09-10"), Sex.MALE, null, null, -22L));
    assertThat(horses)
        .extracting(Horse::getId, Horse::getName, Horse::getDescription, Horse::getDateOfBirth, Horse::getSex, Horse::getFemaleParentId, Horse::getMaleParentId,
            Horse::getOwnerId)
        .contains(tuple(-5L, "Test Son", "I like horses", LocalDate.parse("2022-10-10"), Sex.MALE, -2L, -3L, -20L));
    assertThat(horses)
        .extracting(Horse::getId, Horse::getName, Horse::getDescription, Horse::getDateOfBirth, Horse::getSex, Horse::getFemaleParentId, Horse::getMaleParentId,
            Horse::getOwnerId)
        .contains(tuple(-7L, "Yuki Herbert", "My hobby is singing", LocalDate.parse("2022-10-10"), Sex.FEMALE, -2L, null, -29L));
    assertThat(horses)
        .extracting(Horse::getId, Horse::getName, Horse::getDescription, Horse::getDateOfBirth, Horse::getSex, Horse::getFemaleParentId, Horse::getMaleParentId,
            Horse::getOwnerId)
        .contains(tuple(-9L, "König Arthur I", "My hobby is to reign", LocalDate.parse("1980-05-10"), Sex.MALE, null, null, -24L));
    assertThat(horses)
        .extracting(Horse::getId, Horse::getName, Horse::getDescription, Horse::getDateOfBirth, Horse::getSex, Horse::getFemaleParentId, Horse::getMaleParentId,
            Horse::getOwnerId)
        .contains(tuple(-8L, "König Arthur II", "Army of horses", LocalDate.parse("2000-10-10"), Sex.MALE, null, -9L, null));
  }

  @Test
  public void persistCreateHorseDaoAndDeleteAfterwards() {
    HorseCreateDto createdHorse = new HorseCreateDto(
        "Katharina",
        null,
        LocalDate.parse("2000-07-06"),
        Sex.FEMALE,
        null,
        null,
        -9L);
    var persistedHorse = horseDao.create(createdHorse);

    assertNotNull(persistedHorse.getId());
    assertEquals(createdHorse.name(), persistedHorse.getName());
    assertEquals(createdHorse.description(), persistedHorse.getDescription());
    assertEquals(createdHorse.dateOfBirth(), persistedHorse.getDateOfBirth());
    assertEquals(createdHorse.sex(), persistedHorse.getSex());
    assertEquals(createdHorse.ownerId(), persistedHorse.getOwnerId());
    assertEquals(createdHorse.femaleParentId(), persistedHorse.getFemaleParentId());
    assertEquals(createdHorse.maleParentId(), persistedHorse.getMaleParentId());
    assertEquals(createdHorse.ownerId(), persistedHorse.getOwnerId());

    try {
      horseDao.deleteById(persistedHorse.getId());
    } catch (NotFoundException e) {
      fail("A NotFoundException was thrown altough it existence should have been asserted", e.getCause());
    }

    assertThrows(NotFoundException.class, () -> {
      horseDao.deleteById(persistedHorse.getId());
    });
  }

  @Test
  public void searchWith_SexAsFiltering() {
    List<Horse> horses = horseDao.search(new HorseSearchDto(
        null,
        null,
        LocalDate.parse("2020-02-02"),
        null,
        Sex.FEMALE,
        null,
        null,
        2
    ));

    assertThat(horses)
        .extracting(Horse::getId, Horse::getName, Horse::getDescription, Horse::getDateOfBirth, Horse::getSex, Horse::getFemaleParentId, Horse::getMaleParentId,
            Horse::getOwnerId)
        .contains(tuple(-10L, "Prinzessin Peach", "eating mushrooms", LocalDate.parse("1980-10-10"), Sex.FEMALE, null, null, -30L));

    assertThat(horses)
        .extracting(Horse::getId, Horse::getName, Horse::getDescription, Horse::getDateOfBirth, Horse::getSex, Horse::getFemaleParentId, Horse::getMaleParentId,
            Horse::getOwnerId)
        .contains(tuple(-1L, "Wendy", null, LocalDate.parse("2000-12-12"), Sex.FEMALE, -10L, null, -30L));
  }

  @Test
  public void getById_OnNonExistingHorse_ThrowNotFoundException() {
    NotFoundException actual = assertThrows(NotFoundException.class, () -> {
      horseDao.getById(-999);
    });

    assertEquals("No horse with ID -999 found", actual.getMessage());
  }
}
