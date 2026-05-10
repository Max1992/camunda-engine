package ru.backup.engine.activity.clue;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.withVariables;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static ru.backup.engine.delegates.Consts.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import ru.backup.engine.TestUtils;
import ru.backup.engine.activity.ProcessConsts;
import ru.backup.engine.enums.LocationStatus;

/**
 * Распределение улик
 * */
public class DistributionClueTest {
  @RegisterExtension
  public static ProcessEngineExtension extension = ProcessEngineExtension.builder()
      .useProcessEngine(new StandaloneInMemProcessEngineConfiguration()
          .setJdbcUrl("jdbc:h2:mem:camunda-test;DB_CLOSE_DELAY=-1")
          .setDatabaseSchemaUpdate("true")
          .setJobExecutorActivate(false)
          .buildProcessEngine())
      .build();

  @BeforeAll
  public static void beforeAll() throws FileNotFoundException {
    TestUtils.deploy("Фаза мифа.bpmn");
  }

  @AfterEach
  void tearDown() {
    Mocks.reset();
  }

  @BeforeEach
  public void setUp() throws Exception {

    JavaDelegate putClueToLocation = Mockito.mock(JavaDelegate.class);
    JavaDelegate putClueToPlayer = Mockito.mock(JavaDelegate.class);
    JavaDelegate determineInvestigatorForClue = Mockito.mock(JavaDelegate.class);

    Mocks.register(PUT_CLUE_TO_LOCATION_TASK, putClueToLocation);
    Mocks.register(PUT_CLUE_TO_PLAYER_TASK, putClueToPlayer);
    Mocks.register(DETERMINE_INVESTIGATOR_FOR_CLUE_TASK, determineInvestigatorForClue);

    Mockito.doAnswer(invocation -> null).when(putClueToLocation).execute(Mockito.any());
    Mockito.doAnswer(invocation -> null).when(putClueToPlayer).execute(Mockito.any());
    Mockito.doAnswer(invocation -> null).when(determineInvestigatorForClue).execute(Mockito.any());
  }

  /**
   * локация со знаком древних пропускается.
   * */
  @ParameterizedTest(name = "{index} ==> Локация: {0}")
  @EnumSource(value = LocationStatus.class, names = {"ELDER_SIGN", "OPEN_GATE"})
  public void testClue_NotEmpty(
      LocationStatus status
  ) throws Exception {

    JavaDelegate getLocationStatusMock = Mockito.mock(JavaDelegate.class);

    Mocks.register(GET_LOCATION_STATUS_TASK, getLocationStatusMock);

    Mockito.doAnswer(invocation -> {
      var execution = (org.camunda.bpm.engine.delegate.DelegateExecution) invocation.getArgument(0);
      execution.setVariableLocal("locationStatus", status);
      return null;
    }).when(getLocationStatusMock).execute(Mockito.any());

    ProcessInstance pi = runtimeService().startProcessInstanceByKey(ProcessConsts.CLUE_DISTRIBUTION_PROCESS_KEY,
        withVariables(
            "locationId", "loc-001"
        )
    );

    assertThat(pi).hasNotPassed("Activity_1do48yi");
    assertThat(pi).isEnded();
  }

  @ParameterizedTest(name = "{index} ==> Локация: {0}, Количество сыщиков: {1}, Ожидаемая таска: {2}")
  @MethodSource("provideTestData")
  public void testClue_Empty(
      LocationStatus status,
      int playersCount,
      String expectedTaskId
  ) throws Exception {

    JavaDelegate getLocationStatusMock = Mockito.mock(JavaDelegate.class);
    JavaDelegate getPlayersByLocationMock = Mockito.mock(JavaDelegate.class);
    JavaDelegate determineInvestigatorForClueMock = Mockito.mock(JavaDelegate.class);

    Mocks.register(GET_LOCATION_STATUS_TASK, getLocationStatusMock);
    Mocks.register(GET_PLAYERS_BY_LOCATION_TASK, getPlayersByLocationMock);
    Mocks.register(DETERMINE_INVESTIGATOR_FOR_CLUE_TASK, determineInvestigatorForClueMock);

    Mockito.doAnswer(invocation -> {
      var execution = (org.camunda.bpm.engine.delegate.DelegateExecution) invocation.getArgument(0);
      execution.setVariableLocal("locationStatus", status);
      return null;
    }).when(getLocationStatusMock).execute(Mockito.any());

    Mockito.doAnswer(invocation -> {
      var execution = (org.camunda.bpm.engine.delegate.DelegateExecution) invocation.getArgument(0);
      List<String> list = Collections.nCopies(playersCount, "player");
      execution.setVariableLocal("playerCollection", new ArrayList<>(list));
      return null;
    }).when(getPlayersByLocationMock).execute(Mockito.any());

    Mockito.doAnswer(invocation -> {
      var execution = (org.camunda.bpm.engine.delegate.DelegateExecution) invocation.getArgument(0);
      execution.setVariableLocal("playerId", "player-001");
      return null;
    }).when(determineInvestigatorForClueMock).execute(Mockito.any());

    ProcessInstance pi = runtimeService().startProcessInstanceByKey(ProcessConsts.CLUE_DISTRIBUTION_PROCESS_KEY,
        withVariables(
            "locationId", "loc-001"
        )
    );

    Set<String> tasks = new HashSet<>(
        Set.of("Activity_152ml40", "Activity_1uhu64o", "Activity_0y8zru7")
    );
    tasks.remove(expectedTaskId);

    assertThat(pi).hasPassed(
        "Activity_1do48yi",
        expectedTaskId
    );
    assertThat(pi).hasNotPassed(tasks.toArray(new String[0]));

    assertThat(pi).isEnded();
  }

  private static Stream<Arguments> provideTestData() {
    return Stream.of(
        arguments(LocationStatus.EMPTY, 0, "Activity_152ml40"),
        arguments(LocationStatus.EMPTY, 1, "Activity_1uhu64o"),
        arguments(LocationStatus.EMPTY, 2, "Activity_0y8zru7")
    );
  }
}
