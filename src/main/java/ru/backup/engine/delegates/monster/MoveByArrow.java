package ru.backup.engine.delegates.monster;

import org.springframework.stereotype.Component;
import ru.backup.engine.delegates.DelegateExecutionHandler;
import ru.backup.engine.delegates.JavaDelegateHandler;

@Component("moveByArrow")
public class MoveByArrow extends JavaDelegateHandler {

  @Override
  protected void onExecute(DelegateExecutionHandler execution) throws Exception {
    String monsterId = execution.getVariableLocal("monsterId");
    String arrowColor = execution.getVariableLocal("arrowColor");
    int stepsLeft = execution.getVariableLocal("stepsLeft");
    execution.setVariableLocal("stepsLeft", stepsLeft - 1);
    execution.setVariableLocal("monsterCurrentLocationId", "new-monster-current-location-id");

//    Monster monster = monsterRepository.findById(monsterId).get();
//    Location currentLocation = monster.getCurrentLocation();
//
//    // 1. Делаем ровно ОДИН шаг
//    String nextLocationId = "WHITE".equals(arrowColor)
//        ? currentLocation.getWhiteArrowTargetId()
//        : currentLocation.getBlackArrowTargetId();
//
//    if (nextLocationId != null) {
//      Location nextLocation = locationRepository.findById(nextLocationId).get();
//      monster.setCurrentLocation(nextLocation);
//      monsterRepository.save(monster);
//
//      // 2. Уменьшаем счетчик оставшихся шагов
//      int stepsLeft = (int) execution.getVariableLocal("stepsLeft");
//      execution.setVariableLocal("stepsLeft", stepsLeft - 1);
//
//      // 3. ПРАВИЛО ПЕРЕХВАТА: проверяем, есть ли сыщик в новой локации
//      if (checkInvestigatorsInLocation(nextLocationId)) {
//        // Если сыщик на месте, монстр обязан остановиться, движение прерывается
//        execution.setVariableLocal("encounter", true);
//      }
//    } else {
//      // Если тупик, обнуляем шаги
//      execution.setVariableLocal("stepsLeft", 0);
//    }
  }
}
