package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.OwnerCreateDto;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.OwnerDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class OwnerValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final OwnerDao dao;

  @Autowired
  public OwnerValidator(OwnerDao dao) {
    this.dao = dao;
  }

  public void validateForInsert(OwnerCreateDto ownerCreateDto) throws ValidationException {
    LOG.trace("validatorForInsert({})", ownerCreateDto);
    List<String> validationErrors = new ArrayList<>();

    if (ownerCreateDto.firstName() != null) {
      if (ownerCreateDto.firstName().isBlank()) {
        validationErrors.add("First name is given but blank");
      } else if (ownerCreateDto.firstName().length() > 4095) {
        validationErrors.add("First name too long: longer than 4095 characters");
      }
      if (!(ownerCreateDto.firstName().trim().length() == ownerCreateDto.firstName().length())) {
        validationErrors.add("Owner first name has whitespaces which is not allowed");
      }
    }

    if (ownerCreateDto.lastName() != null) {
      if (ownerCreateDto.lastName().isBlank()) {
        validationErrors.add("Last name is given but blank");
      } else if (ownerCreateDto.lastName().length() > 255) {
        validationErrors.add("Last name too long: longer than 255 characters");
      }
      if (!(ownerCreateDto.lastName().trim().length() == ownerCreateDto.lastName().length())) {
        validationErrors.add("Owner last name has whitespaces which is not allowed");
      }
    }

    if (ownerCreateDto.email() != null) {
      if (ownerCreateDto.email().isBlank()) {
        validationErrors.add("Mail is given but blank");
      } else {
        if (ownerCreateDto.email().length() > 255) {
          validationErrors.add("E-Mail too long: longer than 255 characters");
        } else {
          Pattern pattern = Pattern.compile("[a-z0-9._%+-]+@[a-z0-9.-]+.[a-z]{2,4}$", Pattern.CASE_INSENSITIVE);
          Matcher matcher = pattern.matcher(ownerCreateDto.email());
          if (!matcher.find()) {
            validationErrors.add("E-Mail string is not formatted as an email, it must be formatted like [text]@[text].[domain]");
          }
          if (dao.isEmailInUse(ownerCreateDto.email())) {
            validationErrors.add("E-Mail is already in use, choose a different one");
          }
        }
      }
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of owner for insert failed", validationErrors);
    }
  }
}
