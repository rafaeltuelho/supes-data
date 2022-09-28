package org.acme;

public class Power {
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
    return "Power [aliases=" + aliases + ", description=" + description + ", name=" + name + ", score=" + score
        + ", tier=" + tier + "]";
  }

}


