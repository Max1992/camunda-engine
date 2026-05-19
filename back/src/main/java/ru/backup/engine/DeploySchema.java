package ru.backup.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DeploySchema {

  private static final Logger log = LoggerFactory.getLogger(DeploySchema.class);

  @Value("${camunda.bpm.deploy.path}")
  private String deployFolderPath;

  @Value("${camunda.bpm.deploy.auto-deploy}")
  private Boolean autoDeploy;

  private final RepositoryService repositoryService;

  public DeploySchema(RepositoryService repositoryService) {
    this.repositoryService = repositoryService;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void deployFromFolder() throws IOException {

    if(!autoDeploy){
      log.info("Автоматический деплой выключен");
      return;
    }

    File folder = new File(deployFolderPath);

    if (!folder.exists() || !folder.isDirectory()) {
      log.warn("Папка для деплоя не найдена: {}", deployFolderPath);
      return;
    }

    File[] files = getFiles(folder);

    if (files.length == 0) {
      log.warn("Нет файлов для деплоя в папке: {}", deployFolderPath);
      return;
    }

    DeploymentBuilder deploymentBuilder = repositoryService.createDeployment()
        .name("ExternalFolderDeployment")
        .enableDuplicateFiltering(true); // Игнорировать деплой, если схемы не менялись

    for (File file : files) {
      try (FileInputStream fis = new FileInputStream(file)) {
        deploymentBuilder.addInputStream(file.getName(), fis);
        log.info("Файл добавлен к деплою: {}", file.getName());
      } catch (IOException e) {
        log.error("Ошибка чтения файла {}: {}", file.getName(), e.getMessage(), e);
      }
    }

    deploymentBuilder.deploy();
    log.info("Деплой из внешней папки успешно завершен.");
  }

  private File[] getFiles(File folder) throws IOException {
    try (Stream<Path> stream = Files.walk(folder.toPath())) {
      return stream
          .map(Path::toFile)
          .filter(file -> file.isFile() && (
              file.getName().endsWith(".bpmn") ||
                  file.getName().endsWith(".dmn") ||
                  file.getName().endsWith(".form")
          ))
          .toArray(File[]::new);
    }
  }
}
