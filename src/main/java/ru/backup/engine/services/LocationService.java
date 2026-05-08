package ru.backup.engine.services;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LocationService {

  public List<String> getPlayersByLocation(String location) {
    return Arrays.asList("player1", "player2");
  }
}
