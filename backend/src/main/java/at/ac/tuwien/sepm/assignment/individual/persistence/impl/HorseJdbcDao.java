package at.ac.tuwien.sepm.assignment.individual.persistence.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.DeletedDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseAncestryDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerSearchDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.entity.Owner;
import at.ac.tuwien.sepm.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class HorseJdbcDao implements HorseDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final String TABLE_NAME = "horse";
  private static final String SQL_SELECT_ALL = "SELECT * FROM " + TABLE_NAME;
  private static final String SQL_SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
  private static final String SQL_UPDATE =
      "UPDATE " + TABLE_NAME + " SET name = ?" + "  , description = ?" + "  , date_of_birth = ?" + "  , sex = ?" + "  , owner_id = ?"
          + "  , female_parent_id = ?" + "  , male_parent_id = ?" + " WHERE id = ?";
  private static final String SQL_CREATE =
      "INSERT INTO " + TABLE_NAME + " (name, description, date_of_birth, sex, owner_id, female_parent_id, male_parent_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
  private static final String SQL_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
  private static final String SQL_FEMALE_PARENT_OF_HORSES =
      "SELECT * FROM " + TABLE_NAME + " WHERE female_parent_id = ?";
  private static final String SQL_MALE_PARENT_OF_HORSES =
      "SELECT * FROM " + TABLE_NAME + " WHERE male_parent_id = ?";
  private static final String SQL_ANCESTRY =
      "    " + "WITH RECURSIVE PARENT(id,name,date_of_birth,female_parent_id,male_parent_id,sex,description, owner_id,N) AS ("
          + "          SELECT id,name,date_of_birth,female_parent_id,male_parent_id,sex,description, owner_id,1" + "          FROM horse WHERE id= ?"
          + "          UNION ALL"
          +
          "          SELECT horse.id,horse.name,horse.date_of_birth,horse.female_parent_id,horse.male_parent_id,horse.sex,horse.description, horse.owner_id,N+1"
          + "          FROM PARENT,horse" + "          WHERE (horse.id=PARENT.female_parent_id OR horse.id=PARENT.male_parent_id) AND (N < ?)" + "        )"
          + "SELECT DISTINCT * FROM PARENT";
  private final JdbcTemplate jdbcTemplate;
  private final OwnerJdbcDao ownerJdbcDao;

  @Autowired
  public HorseJdbcDao(OwnerJdbcDao ownerJdbcDao, JdbcTemplate jdbcTemplate) {
    this.ownerJdbcDao = ownerJdbcDao;
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public List<Horse> listWhereFemaleParentIs(long femaleParentId) {
    LOG.trace("listWhereFemaleParentIs({})", femaleParentId);
    return jdbcTemplate.query(SQL_FEMALE_PARENT_OF_HORSES, this::mapRow, femaleParentId);
  }

  @Override
  public List<Horse> listWhereMaleParentIs(long maleParentId) {
    LOG.trace("listWhereMaleParentIs({})", maleParentId);
    return jdbcTemplate.query(SQL_MALE_PARENT_OF_HORSES, this::mapRow, maleParentId);
  }

  private List<Horse> executeSearchHorses(HorseSearchDto searchParameters) {
    LOG.trace("executeSearchHorses({})", searchParameters);
    StringBuilder sqlQuery = new StringBuilder("SELECT * FROM " + TABLE_NAME + " WHERE ");

    boolean needANDLogicalOperator = false;
    Map<Integer, String> parametersMap = new HashMap<>();
    int paramNumber = 1;

    if (searchParameters.name() != null) {
      sqlQuery.append("UPPER(name) LIKE ? ");
      needANDLogicalOperator = true;
      parametersMap.put(paramNumber, "%" + searchParameters.name().toUpperCase() + "%");
      paramNumber++;
    }

    if (searchParameters.description() != null) {
      if (needANDLogicalOperator) {
        sqlQuery.append("AND ");
      }
      sqlQuery.append("UPPER(description) LIKE ? ");
      needANDLogicalOperator = true;
      parametersMap.put(paramNumber, "%" + searchParameters.description().toUpperCase() + "%");
      paramNumber++;
    }

    if (searchParameters.excludeHorseById() != null) {
      if (needANDLogicalOperator) {
        sqlQuery.append("AND ");
      }
      sqlQuery.append("id != ? ");
      needANDLogicalOperator = true;
      parametersMap.put(paramNumber, String.valueOf(searchParameters.excludeHorseById()));
      paramNumber++;
    }

    if (searchParameters.bornBefore() != null) {
      if (needANDLogicalOperator) {
        sqlQuery.append("AND ");
      }
      sqlQuery.append("date_of_birth < ? ");
      needANDLogicalOperator = true;
      parametersMap.put(paramNumber, searchParameters.bornBefore().toString());
      paramNumber++;
    }

    if (searchParameters.bornAfter() != null) {
      if (needANDLogicalOperator) {
        sqlQuery.append("AND ");
      }
      sqlQuery.append("date_of_birth > ? ");
      needANDLogicalOperator = true;
      parametersMap.put(paramNumber, searchParameters.bornAfter().toString());
      paramNumber++;
    }

    if (searchParameters.sex() != null) {
      if (needANDLogicalOperator) {
        sqlQuery.append("AND ");
      }
      sqlQuery.append("UPPER(sex) = ? ");
      needANDLogicalOperator = true;
      parametersMap.put(paramNumber, searchParameters.sex().toString().toUpperCase());
      paramNumber++;
    }

    if (searchParameters.ownerName() != null) {
      ArrayList<Owner> owners = new ArrayList<>(ownerJdbcDao.search(new OwnerSearchDto(searchParameters.ownerName(), 50)));
      if (owners.size() > 0) {
        LOG.debug("search horses found {} possible owners", owners.size());
        if (needANDLogicalOperator) {
          sqlQuery.append("AND (");
        }
        boolean appendAND = false;
        for (Owner owner : owners) {
          if (appendAND) {
            sqlQuery.append(" OR ");
          } else {
            appendAND = true;
          }

          sqlQuery.append("owner_id = ?");

          parametersMap.put(paramNumber, String.valueOf(owner.getId()));
          paramNumber++;
        }

        if (needANDLogicalOperator) {
          sqlQuery.append(" ) ");
        }
      } else {
        return new ArrayList<>();
      }
    }

    if (searchParameters.limit() != null) {
      sqlQuery.append("LIMIT ").append(searchParameters.limit());
    }

    LOG.debug("created sql query is {}", sqlQuery);

    return jdbcTemplate.query(sqlQuery.toString(), this::mapRow, parametersMap.values().toArray());
  }

  @Override
  public List<Horse> getAll() {
    LOG.trace("getAll()");
    return jdbcTemplate.query(SQL_SELECT_ALL, this::mapRow);
  }

  @Override
  public Horse getById(long id) throws NotFoundException {
    LOG.trace("getById({})", id);
    List<Horse> horses;
    horses = jdbcTemplate.query(SQL_SELECT_BY_ID, this::mapRow, id);

    if (horses.isEmpty()) {
      throw new NotFoundException("No horse with ID %d found".formatted(id));
    }
    if (horses.size() > 1) {
      LOG.error("Too many horses with ID %d found".formatted(id));
      throw new FatalException("Too many horses with ID %d found".formatted(id));
    }

    return horses.get(0);
  }

  @Override
  public DeletedDto deleteById(long id) throws NotFoundException {
    LOG.trace("deleteById({})", id);

    int updated = jdbcTemplate.update(SQL_DELETE, id);

    if (updated == 0) {
      throw new NotFoundException("Could not delete horse with ID " + id + ", because it does not exist");
    } else if (updated > 1) {
      LOG.error("Too many horses with ID %d found".formatted(id));
      throw new FatalException("Too many horses with ID %d found to be deleted".formatted(id));
    }

    return new DeletedDto(id);
  }

  @Override
  public List<Horse> search(HorseSearchDto searchParameters) {
    LOG.trace("search({})", searchParameters);
    return this.executeSearchHorses(searchParameters);
  }

  @Override
  public List<Horse> ancestryOf(HorseAncestryDto searchParameters) throws NotFoundException {
    LOG.trace("ancestryOf({})", searchParameters);
    var limit = searchParameters.limit() != null ? searchParameters.limit() : 10;
    List<Horse> horses;
    horses = jdbcTemplate.query(SQL_ANCESTRY.trim(), this::mapRow, searchParameters.id(), limit);
    if (horses.isEmpty()) {
      throw new NotFoundException("No horse with ID %d found".formatted(searchParameters.id()));
    } else {
      return horses;
    }
  }

  @Override
  public Horse update(HorseDetailDto horse) throws NotFoundException {
    LOG.trace("update({})", horse);
    int updated =
        jdbcTemplate.update(SQL_UPDATE, horse.name(), horse.description(), horse.dateOfBirth(), horse.sex().toString(), horse.ownerId(), horse.femaleParentId(),
            horse.maleParentId(), horse.id());
    if (updated == 0) {
      throw new NotFoundException("Could not update horse with ID " + horse.id() + ", because it does not exist");
    }

    return new Horse().setId(horse.id()).setName(horse.name()).setDescription(horse.description()).setDateOfBirth(horse.dateOfBirth()).setSex(horse.sex())
        .setOwnerId(horse.ownerId()).setMaleParentId(horse.maleParentId()).setFemaleParentId(horse.femaleParentId());
  }

  @Override
  public Horse create(HorseCreateDto newHorse) {
    LOG.trace("create({})", newHorse);

    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(con -> {
      PreparedStatement stmt = con.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS);
      stmt.setString(1, newHorse.name());
      stmt.setString(2, newHorse.description());
      stmt.setString(3, newHorse.dateOfBirth().toString());
      stmt.setString(4, newHorse.sex().toString());
      stmt.setString(5, newHorse.ownerId() != null ? String.valueOf(newHorse.ownerId()) : null);
      stmt.setString(6, newHorse.femaleParentId() != null ? String.valueOf(newHorse.femaleParentId()) : null);
      stmt.setString(7, newHorse.maleParentId() != null ? String.valueOf(newHorse.maleParentId()) : null);
      return stmt;
    }, keyHolder);

    Number key = keyHolder.getKey();
    if (key == null) {
      LOG.error("Could not extract key for newly created owner. There is probably a programming error…");
      throw new FatalException("Could not extract key for newly created owner. There is probably a programming error…");
    }

    return new Horse().setId(key.longValue()).setName(newHorse.name()).setDescription(newHorse.description()).setDateOfBirth(newHorse.dateOfBirth())
        .setSex(newHorse.sex()).setOwnerId(newHorse.ownerId()).setFemaleParentId(newHorse.femaleParentId()).setMaleParentId(newHorse.maleParentId());
  }

  private Horse mapRow(ResultSet result, int rownum) throws SQLException {
    LOG.trace("mapRow({}, {})", result, rownum);
    return new Horse().setId(result.getLong("id")).setName(result.getString("name"))
        .setDescription(result.getString("description"))
        .setDateOfBirth(result.getDate("date_of_birth").toLocalDate()).setSex(Sex.valueOf(result.getString("sex")))
        .setFemaleParentId(result.getObject("female_parent_id", Long.class)).setMaleParentId(result.getObject("male_parent_id", Long.class))
        .setOwnerId(result.getObject("owner_id", Long.class));
  }
}
