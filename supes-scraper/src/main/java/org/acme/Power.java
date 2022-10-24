package org.acme;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Power implements Serializable {
  @JsonIgnore
  public int id = 0;
  public String name;
  public String tier;
  public int score = 0;
  public String aliases;
  public String description;

  public Power() {
  }

  public Power(String name, String tier, int score, String aliases, String description) {
    this.name = name;
    this.tier = tier;
    this.score = score;
    this.aliases = aliases;
    this.description = description;
  }

  @Override
  public String toString() {
    return "Power [id="+ id +", aliases=" + aliases + ", description=" + description + ", name=" + name + ", score=" + score
        + ", tier=" + tier + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof Power))
      return false;
    Power other = (Power) obj;
    return Objects.equals(name, other.name);
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getTier() {
    return tier;
  }

  public int getScore() {
    return score;
  }

  public String getAliases() {
    return aliases;
  }

  public String getDescription() {
    return description;
  }

}


