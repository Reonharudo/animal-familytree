package at.ac.tuwien.sepm.assignment.individual.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class Owner {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private long id;
  private String firstName;
  private String lastName;
  private String email;

  public long getId() {
    LOG.trace("getId()");
    return id;
  }

  public Owner setId(long id) {
    LOG.trace("setId({})", id);
    this.id = id;
    return this;
  }

  public String getFirstName() {
    LOG.trace("getFirstName()");
    return firstName;
  }

  public Owner setFirstName(String firstName) {
    LOG.trace("setFirstName({})", firstName);
    this.firstName = firstName;
    return this;
  }

  public String getLastName() {
    LOG.trace("getLastName()");
    return lastName;
  }

  public Owner setLastName(String lastName) {
    LOG.trace("setLastName({})", lastName);
    this.lastName = lastName;
    return this;
  }

  public String getEmail() {
    LOG.trace("getEmail()");
    return email;
  }

  public Owner setEmail(String email) {
    LOG.trace("setEmail({})", email);
    this.email = email;
    return this;
  }

  @Override
  public String toString() {
    LOG.trace("toString()");
    return "Owner{"
        + "id=" + id
        + ", firstName='" + firstName + '\''
        + ", lastName='" + lastName + '\''
        + ", email='" + email + '\''
        + '}';
  }
}
