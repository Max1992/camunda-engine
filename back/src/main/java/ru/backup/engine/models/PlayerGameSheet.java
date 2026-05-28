package ru.backup.engine.models;

import java.util.ArrayList;
import java.util.List;
import ru.backup.engine.enums.PlayerStatus;

public class PlayerGameSheet {
  public String assignee;
  public PlayerStatus playerStatus;
  public int power;
  public int knowledge;
  public int luck;
  public int stealth;
  public int speed;
  public int will;
  public int clue;
  public List<String> skill = new ArrayList<>();
  public List<String> ally = new ArrayList<>();
  public List<String> simpleItem = new ArrayList<>();
}
