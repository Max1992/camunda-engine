package ru.backup.engine.rest.configuration;

import java.util.Collections;
import org.apache.tomcat.websocket.server.WsSci;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatWebSocketCustomizer {

  // исправляет появление ошибки
  // No 'jakarta.websocket.server.ServerContainer' ServletContext attribute. Are you running in a Servlet container that supports JSR-356?
  @Bean
  public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
    return factory -> factory.addContextCustomizers(context -> {
      // Принудительно связываем встроенный Tomcat и WebSocket Инициализатор Apache (JSR-356)
      context.addServletContainerInitializer(new WsSci(), Collections.emptySet());
    });
  }
}