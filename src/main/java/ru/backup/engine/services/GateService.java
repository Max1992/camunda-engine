package ru.backup.engine.services;

import org.springframework.stereotype.Service;

@Service
public class GateService {

  public String drawGateTokenFromPool() {
    return "gate-99";
  }
}
