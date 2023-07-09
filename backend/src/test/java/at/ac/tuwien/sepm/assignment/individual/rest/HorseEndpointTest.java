package at.ac.tuwien.sepm.assignment.individual.rest;

import at.ac.tuwien.sepm.assignment.individual.dto.DeletedDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
@EnableWebMvc
@WebAppConfiguration
public class HorseEndpointTest {

  @Autowired
  ObjectMapper objectMapper;
  @Autowired
  private WebApplicationContext webAppContext;
  private MockMvc mockMvc;

  @BeforeEach
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
  }

  @Test
  public void gettingAllHorses() throws Exception {
    byte[] body = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/horses")
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
        .andReturn().getResponse().getContentAsByteArray();

    List<HorseListDto> horseResult = objectMapper.readerFor(HorseListDto.class).<HorseListDto>readValues(body).readAll();

    assertThat(horseResult).isNotNull();
    assertThat(horseResult.size()).isGreaterThanOrEqualTo(8);
    assertThat(horseResult)
        .extracting(HorseListDto::id, HorseListDto::name, HorseListDto::description, HorseListDto::dateOfBirth, HorseListDto::sex, HorseListDto::femaleParentId,
            HorseListDto::maleParentId, HorseListDto::ownerId)
        .contains(tuple(-10L, "Prinzessin Peach", "eating mushrooms", LocalDate.parse("1980-10-10"), Sex.FEMALE, null, null, -30L));
    assertThat(horseResult)
        .extracting(HorseListDto::id, HorseListDto::name, HorseListDto::description, HorseListDto::dateOfBirth, HorseListDto::sex, HorseListDto::femaleParentId,
            HorseListDto::maleParentId, HorseListDto::ownerId)
        .contains(tuple(-1L, "Wendy", null, LocalDate.parse("2000-12-12"), Sex.FEMALE, -10L, null, -30L));
    assertThat(horseResult)
        .extracting(HorseListDto::id, HorseListDto::name, HorseListDto::description, HorseListDto::dateOfBirth, HorseListDto::sex, HorseListDto::femaleParentId,
            HorseListDto::maleParentId, HorseListDto::ownerId)
        .contains(tuple(-2L, "Test Mother", "The famous mother", LocalDate.parse("2020-09-10"), Sex.FEMALE, -1L, null, -22L));
    assertThat(horseResult)
        .extracting(HorseListDto::id, HorseListDto::name, HorseListDto::description, HorseListDto::dateOfBirth, HorseListDto::sex, HorseListDto::femaleParentId,
            HorseListDto::maleParentId, HorseListDto::ownerId)
        .contains(tuple(-3L, "Test Father", "The famous father", LocalDate.parse("2022-09-10"), Sex.MALE, null, null, -22L));
    assertThat(horseResult)
        .extracting(HorseListDto::id, HorseListDto::name, HorseListDto::description, HorseListDto::dateOfBirth, HorseListDto::sex, HorseListDto::femaleParentId,
            HorseListDto::maleParentId, HorseListDto::ownerId)
        .contains(tuple(-5L, "Test Son", "I like horses", LocalDate.parse("2022-10-10"), Sex.MALE, -2L, -3L, -20L));
    assertThat(horseResult)
        .extracting(HorseListDto::id, HorseListDto::name, HorseListDto::description, HorseListDto::dateOfBirth, HorseListDto::sex, HorseListDto::femaleParentId,
            HorseListDto::maleParentId, HorseListDto::ownerId)
        .contains(tuple(-7L, "Yuki Herbert", "My hobby is singing", LocalDate.parse("2022-10-10"), Sex.FEMALE, -2L, null, -29L));
    assertThat(horseResult)
        .extracting(HorseListDto::id, HorseListDto::name, HorseListDto::description, HorseListDto::dateOfBirth, HorseListDto::sex, HorseListDto::femaleParentId,
            HorseListDto::maleParentId, HorseListDto::ownerId)
        .contains(tuple(-9L, "König Arthur I", "My hobby is to reign", LocalDate.parse("1980-05-10"), Sex.MALE, null, null, -24L));
    assertThat(horseResult)
        .extracting(HorseListDto::id, HorseListDto::name, HorseListDto::description, HorseListDto::dateOfBirth, HorseListDto::sex, HorseListDto::femaleParentId,
            HorseListDto::maleParentId, HorseListDto::ownerId)
        .contains(tuple(-8L, "König Arthur II", "Army of horses", LocalDate.parse("2000-10-10"), Sex.MALE, null, -9L, null));
  }

  @Test
  public void createHorse_StatusCodeAndResult() throws Exception {
    HorseCreateDto horseDto = new HorseCreateDto(
        "Katharina",
        null,
        LocalDate.parse("1980-07-06"),
        Sex.FEMALE,
        null,
        null,
        null);
    byte[] body = mockMvc
        .perform(MockMvcRequestBuilders
            .post("/horses")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(horseDto))
        ).andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsByteArray();

    HorseDetailDto persistedHorse = objectMapper.readerFor(HorseDetailDto.class).readValue(body);

    assertThat(persistedHorse).isNotNull();
    assertThat(persistedHorse.id()).isNotNull();
    assertEquals(horseDto.name(), persistedHorse.name());
    assertEquals(horseDto.description(), persistedHorse.description());
    assertEquals(horseDto.sex(), persistedHorse.sex());
    assertEquals(horseDto.dateOfBirth(), persistedHorse.dateOfBirth());
    assertEquals(horseDto.ownerId(), persistedHorse.ownerId());
    assertEquals(horseDto.femaleParentId(), persistedHorse.femaleParentId());
    assertEquals(horseDto.maleParentId(), persistedHorse.maleParentId());
  }

  @Test
  public void deleteById_ReturnDeletedIdAsReturnedValue() throws Exception {
    byte[] body = mockMvc
        .perform(MockMvcRequestBuilders
            .delete("/horses/-6")
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();

    DeletedDto deletedDto = objectMapper.readerFor(DeletedDto.class).readValue(body);
    mockMvc
        .perform(MockMvcRequestBuilders
            .get("/horses/-6")
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    assertThat(deletedDto).isNotNull();
    assertEquals(deletedDto.id(), -6);
  }

  @Test
  public void gettingNonexistentUrlReturns404() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders
            .get("/asdf123")
        ).andExpect(status().isNotFound());
  }
}
