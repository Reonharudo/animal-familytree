package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseAncestryDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.OwnerDao;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class HorseValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final HorseDao dao;
  private final OwnerDao ownerDao;

  @Autowired
  public HorseValidator(HorseDao dao, OwnerDao ownerDao) {
    this.dao = dao;
    this.ownerDao = ownerDao;
  }

  public void validateSearch(HorseSearchDto searchParameters) throws ValidationException, NotFoundException {
    LOG.trace("validateSearch({})", searchParameters);
    List<String> validationErrors = new ArrayList<>();

    if (searchParameters.name() != null) {
      if (searchParameters.name().length() > 255) {
        validationErrors.add("Name is given but exceeds maximum of allowed characters which is 255");
      }
    }

    if (searchParameters.description() != null) {
      if (searchParameters.description().length() > 4095) {
        validationErrors.add("Description is given but exceeds maximum of allowed characters which is 4095");
      }
    }

    if (searchParameters.ownerName() != null) {
      if (searchParameters.ownerName().length() > (255 + 255)) {
        validationErrors.add("Owner Name is given but exceeds maximum of allowed characters which is 4095");
      }
    }

    if (searchParameters.limit() != null) {
      if (searchParameters.limit() < 1) {
        validationErrors.add("Limit is given but zero or negative which is not valid as search would be guaranteed empty");
      }
    }

    if (searchParameters.excludeHorseById() != null) {
      LOG.debug("Validating if excluded horse id={} exist", searchParameters.excludeHorseById());
      dao.getById(searchParameters.excludeHorseById());
    }

    if (searchParameters.isContradicting()) {
      validationErrors.add("bornAfter and bornBefore properties are contradicting each other. Please decide which property to use");
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for search failed", validationErrors);
    }
  }

  public void validateForAncestry(HorseAncestryDto horseAncestryDto) throws ValidationException, ConflictException {
    LOG.trace("validateForAncestry({})", horseAncestryDto);
    List<String> validationErrors = new ArrayList<>();
    List<String> conflictErrors = new ArrayList<>();

    if (horseAncestryDto.id() == null) {
      validationErrors.add("ID is mandatory and needs to be set");
    } else {
      try {
        dao.getById(horseAncestryDto.id());
      } catch (NotFoundException e) {
        conflictErrors.add("Given id=" + horseAncestryDto.id() + " does not exist");
      }
    }

    if (horseAncestryDto.limit() != null) {
      if (horseAncestryDto.limit() < 0) {
        validationErrors.add("Limit can not be negative. Limit may only be larger or equal 0");
      }
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for ancestry failed", validationErrors);
    }

    if (!conflictErrors.isEmpty()) {
      throw new ConflictException("Horse for ancestry has conflicts", conflictErrors);
    }
  }

  private String stringNamesOfHorseList(List<Horse> list) {
    LOG.trace("stringNamesOfHorseList({})", list);
    StringBuilder erg = new StringBuilder();
    for (int i = 0; i < list.size(); i++) {
      erg.append(list.get(i).getName());
      if (i != list.size() - 1) {
        erg.append(", ");
      }
    }
    return erg.toString();
  }

  public void validateForUpdate(HorseDetailDto horse) throws ValidationException, ConflictException {
    LOG.trace("validateForUpdate({})", horse);
    List<String> validationErrors = new ArrayList<>();
    List<String> conflictErrors = new ArrayList<>();

    if (horse.id() == null) {
      validationErrors.add("No ID given");
    } else {
      try {
        dao.getById(horse.id());
      } catch (NotFoundException e) {
        conflictErrors.add("Given id=" + horse.id() + " does not exist in the data store");
      }
    }

    if (horse.ownerId() != null) {
      LOG.debug("Validating if ownerId={} exists", horse.ownerId());
      try {
        ownerDao.getById(horse.ownerId());
      } catch (NotFoundException e) {
        conflictErrors.add("Owner with id=" + horse.ownerId() + " does not exist");
      }
    }

    if (horse.dateOfBirth() != null) {
      try {
        LocalDate dateOfBirth = LocalDate.parse("" + horse.dateOfBirth());
        if (dateOfBirth.isAfter(LocalDate.now())) {
          validationErrors.add("Date of Birth is in the future. Maximum allowed date is today which is " + LocalDate.now());
        }
      } catch (DateTimeParseException e) {
        validationErrors.add("Invalid Date of Birth was passed");
      }
    } else {
      validationErrors.add("Date Of Birth can not be null");
    }

    if (horse.description() != null) {
      if (horse.description().isBlank()) {
        validationErrors.add("Horse description is given but blank");
      } else if (horse.description().length() > 4095) {
        validationErrors.add("Horse description too long: longer than 4095 characters");
      }
      if (!(horse.description().trim().length() == horse.description().length())) {
        validationErrors.add("Horse description has whitespaces which is not allowed");
      }
    }

    if (horse.name() != null) {
      if (horse.name().isBlank()) {
        validationErrors.add("Horse name is given but blank");
      } else if (horse.name().length() > 255) {
        validationErrors.add("Name is given but exceeds maximum of allowed characters which is 255");
      }
      if (!(horse.name().trim().length() == horse.name().length())) {
        validationErrors.add("Horse name has whitespaces which is not allowed");
      }
    } else {
      validationErrors.add("Name may not be null");
    }

    if (horse.sex() == null) {
      validationErrors.add("Sex may not be null");
    } else {
      LOG.debug("Check if femaleParenId={} would get an invalid sex as her femaleparent role", horse.id());
      if (horse.id() != null) {
        List<Horse> listWhereFemaleParentIs = dao.listWhereFemaleParentIs(horse.id());
        if (listWhereFemaleParentIs.size() > 0) {
          for (Horse childOfMother : listWhereFemaleParentIs) {
            if (childOfMother.getMaleParentId() != null) {
              if (horse.sex() == Sex.MALE) {
                validationErrors.add("Can not update Sex to " + Sex.MALE + " as it would violate her position as mother of other horses. Found as mother in "
                    + stringNamesOfHorseList(listWhereFemaleParentIs));
              }
            }
          }
        }

        LOG.debug("Check if maleParentId={} would get an invalid sex as her maleparent role", horse.id());
        List<Horse> listWhereMaleParentIs = dao.listWhereMaleParentIs(horse.id());
        if (listWhereMaleParentIs.size() > 0) {
          for (Horse childOfMother : listWhereMaleParentIs) {
            if (childOfMother.getMaleParentId() != null) {
              if (horse.sex() == Sex.FEMALE) {
                validationErrors.add(
                    "Can not update Sex to " + Sex.FEMALE + " as it would violate her position as father of other horses. Found as father in "
                        + stringNamesOfHorseList(listWhereMaleParentIs));
              }
            }
          }
        }
      }
    }

    if (horse.femaleParentId() != null) {
      Horse femaleParent = null;
      try {
        femaleParent = dao.getById(horse.femaleParentId());
      } catch (NotFoundException e) {
        conflictErrors.add("Female Parent with id=" + horse.femaleParentId() + " does not exist");
      }
      LOG.debug("Check if femaleParenId={} would get an invalid sex as her femaleparent role", horse.femaleParentId());
      if (femaleParent != null) {
        if (femaleParent.getSex() != Sex.FEMALE) {
          validationErrors.add("Female Parent can not be male. Please assign the female parent "
              + Sex.FEMALE + " as sex");
        }
        if (horse.dateOfBirth() != null) {
          if (femaleParent.getDateOfBirth().isAfter(horse.dateOfBirth()) || femaleParent.getDateOfBirth().isEqual(horse.dateOfBirth())) {
            validationErrors.add(
                "Female Parent birthdate=" + femaleParent.getDateOfBirth() + " must be born after its child birth which is=" + horse.dateOfBirth()
                    + " . Verify the birthdate");
          }
        }
      }
    }

    if (horse.maleParentId() != null) {
      Horse maleParent = null;
      try {
        maleParent = dao.getById(horse.maleParentId());
      } catch (NotFoundException e) {
        conflictErrors.add("Male Parent with id=" + horse.maleParentId() + " does not exist");
      }
      if (maleParent != null) {
        if (maleParent.getSex() != Sex.MALE) {
          validationErrors.add("Male Parent can not be " + Sex.MALE + " Please assign the male parent "
              + Sex.MALE + " as sex");
        }
        if (horse.dateOfBirth() != null) {
          if (maleParent.getDateOfBirth().isAfter(horse.dateOfBirth()) || maleParent.getDateOfBirth().isEqual(horse.dateOfBirth())) {
            validationErrors.add(
                "Male Parent birthdate=" + maleParent.getDateOfBirth() + " must be born after its child birth which is=" + horse.dateOfBirth()
                    + " . Verify the birthdate");
          }
        }
      }
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for update failed", validationErrors);
    }

    if (!conflictErrors.isEmpty()) {
      throw new ConflictException("Horse for update has conflicts", conflictErrors);
    }
  }

  public void validateForInsert(HorseCreateDto horse) throws ValidationException, ConflictException {
    LOG.trace("validateForUpdate({})", horse);
    List<String> validationErrors = new ArrayList<>();
    List<String> conflictErrors = new ArrayList<>();

    if (horse.ownerId() != null) {
      LOG.debug("Validating if ownerId={} exists", horse.ownerId());
      try {
        ownerDao.getById(horse.ownerId());
      } catch (NotFoundException e) {
        conflictErrors.add("Owner with id=" + horse.ownerId() + " does not exist");
      }
    }

    if (horse.dateOfBirth() != null) {
      try {
        LocalDate dateOfBirth = LocalDate.parse("" + horse.dateOfBirth());
        if (dateOfBirth.isAfter(LocalDate.now())) {
          validationErrors.add("Date of Birth is in the future. Maximum allowed date is today which is " + LocalDate.now());
        }
      } catch (DateTimeParseException e) {
        validationErrors.add("Invalid Date of Birth was passed");
      }
    } else {
      validationErrors.add("Date Of Birth can not be null");
    }

    if (horse.description() != null) {
      if (horse.description().isBlank()) {
        validationErrors.add("Horse description is given but blank");
      }
      if (horse.description().length() > 4095) {
        validationErrors.add("Horse description too long: longer than 4095 characters");
      }
      if (!(horse.description().trim().length() == horse.description().length())) {
        LOG.warn("Horse description '{}' has whitespaces which is not allowed", horse.description());
        validationErrors.add("Horse description has whitespaces which is not allowed");
      }
    }

    if (horse.name() != null) {
      if (horse.name().isBlank()) {
        validationErrors.add("Horse name is given but blank");
      } else if (horse.name().length() > 255) {
        validationErrors.add("Name is given but exceeds maximum of allowed characters which is 255");
      }
      if (!(horse.name().trim().length() == horse.name().length())) {
        validationErrors.add("Horse name has whitespaces which is not allowed");
      }
    } else {
      validationErrors.add("Name may not be null");
    }

    if (horse.sex() == null) {
      validationErrors.add("Sex may not be null");
    }

    if (horse.femaleParentId() != null) {
      Horse femaleParent = null;
      try {
        femaleParent = dao.getById(horse.femaleParentId());
      } catch (NotFoundException e) {
        conflictErrors.add("Female Parent with id=" + horse.femaleParentId() + " does not exist");
      }
      LOG.debug("Check if femaleParenId={} would get an invalid sex as her femaleparent role", horse.femaleParentId());
      if (femaleParent != null) {
        if (femaleParent.getSex() != Sex.FEMALE) {
          validationErrors.add("Female Parent can not be male. Please assign the female parent "
              + Sex.FEMALE + " as sex");
        }
        if (horse.dateOfBirth() != null) {
          if (femaleParent.getDateOfBirth().isAfter(horse.dateOfBirth()) || femaleParent.getDateOfBirth().isEqual(horse.dateOfBirth())) {
            validationErrors.add(
                "Female Parent birthdate=" + femaleParent.getDateOfBirth() + " must be born after its child birth which is=" + horse.dateOfBirth()
                    + " . Verify the birthdate");
          }
        }
      }
    }

    if (horse.maleParentId() != null) {
      Horse maleParent = null;
      try {
        maleParent = dao.getById(horse.maleParentId());
      } catch (NotFoundException e) {
        conflictErrors.add("Male Parent with id=" + horse.maleParentId() + " does not exist");
      }
      if (maleParent != null) {
        if (maleParent.getSex() != Sex.MALE) {
          validationErrors.add("Male Parent can not be " + Sex.MALE + " Please assign the male parent "
              + Sex.MALE + " as sex");
        }
        if (horse.dateOfBirth() != null) {
          if (maleParent.getDateOfBirth().isAfter(horse.dateOfBirth()) || maleParent.getDateOfBirth().isEqual(horse.dateOfBirth())) {
            validationErrors.add(
                "Male Parent birthdate=" + maleParent.getDateOfBirth() + " must be born after its child birth which is=" + horse.dateOfBirth()
                    + " . Verify the birthdate");
          }
        }
      }
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for create failed", validationErrors);
    }

    if (!conflictErrors.isEmpty()) {
      throw new ConflictException("Horse for create has conflicts", conflictErrors);
    }
  }
}

