package at.ac.tuwien.sepm.assignment.individual.entity;

import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;

/**
 * Represents a horse in the persistent data store
 */
public class Horse {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private Long id;
  private String name;
  private String description;
  private LocalDate dateOfBirth;
  private Sex sex;
  private Long ownerId;
  private Long femaleParentId;
  private Long maleParentId;

  public Long getId() {
    LOG.trace("getId()");
    return id;
  }

  public Horse setId(Long id) {
    LOG.trace("setId({})", id);
    this.id = id;
    return this;
  }

  public String getName() {
    LOG.trace("getName()");
    return name;
  }

  public Horse setName(String name) {
    LOG.trace("setName({})", name);
    this.name = name;
    return this;
  }

  public String getDescription() {
    LOG.trace("getDescription()");
    return description;
  }

  public Horse setDescription(String description) {
    LOG.trace("setDescription({})", description);
    this.description = description;
    return this;
  }

  public LocalDate getDateOfBirth() {
    LOG.trace("getDateOfBirth()");
    return dateOfBirth;
  }

  public Horse setDateOfBirth(LocalDate dateOfBirth) {
    LOG.trace("setDateOfBirth({})", dateOfBirth);
    this.dateOfBirth = dateOfBirth;
    return this;
  }

  public Sex getSex() {
    LOG.trace("getSex()");
    return sex;
  }

  public Horse setSex(Sex sex) {
    LOG.trace("setSex({})", sex);
    this.sex = sex;
    return this;
  }


  public Long getOwnerId() {
    LOG.trace("getOwnerId()");
    return ownerId;
  }

  public Horse setOwnerId(Long ownerId) {
    LOG.trace("setOwnerId({ownerId})");
    this.ownerId = ownerId;
    return this;
  }

  public Long getFemaleParentId() {
    LOG.trace("getFemaleParentId()");
    return femaleParentId;
  }

  public Horse setFemaleParentId(Long femaleParentId) {
    LOG.trace("setFemaleParentId({femaleParentId})");
    this.femaleParentId = femaleParentId;
    return this;
  }

  public Long getMaleParentId() {
    LOG.trace("getMaleParentId()");
    return maleParentId;
  }

  public Horse setMaleParentId(Long maleParentId) {
    LOG.trace("setMaleParentId(){}", maleParentId);
    this.maleParentId = maleParentId;
    return this;
  }

  @Override
  public String toString() {
    LOG.trace("toString()");
    return "Horse{" + "id=" + id + ", name='" + name + '\'' + ", description='" + description + '\'' + ", dateOfBirth=" + dateOfBirth + ", sex=" + sex
        + ", ownerId=" + ownerId + ", femaleParentId=" + femaleParentId + ", maleParentId=" + maleParentId + '}';
  }
}
