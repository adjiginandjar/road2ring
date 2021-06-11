package com.r2r.road2ring;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import java.io.FileInputStream;
import java.io.IOException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.builder.SpringApplicationBuilder;

import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.datatables.repository.DataTablesRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class Road2RingApplication extends SpringBootServletInitializer {

  @Value("${firebase.database.url}")
  private String databaseUrl;

  @Value("${firebase.config.path}")
  private String configPath;

  public static void main(String[] args) {
    SpringApplication.run(Road2RingApplication.class, args);
  }

  @Bean
  public EmbeddedServletContainerCustomizer containerCustomizer() {

    return new EmbeddedServletContainerCustomizer() {
      @Override
      public void customize(ConfigurableEmbeddedServletContainer container) {

        ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/error/pagenotfound");
        ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/error/pagenotfound");
        ErrorPage error500Page =
            new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/internalservererror");
        container.addErrorPages(error401Page, error404Page, error500Page);
      }
    };
  }

  @Bean
  @ConfigurationProperties(prefix = "datasource.secondary")
  public DataSource mysqlDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean(name = "mysqlJdbc")
  public JdbcTemplate mysqlJdbcTemplate(DataSource mysqlDs) {
    return new JdbcTemplate(mysqlDataSource());
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(Road2RingApplication.class);
  }

  @Bean
  public FirebaseAuth firebaseAuth() throws IOException {

    ClassPathResource serviceAccount = new ClassPathResource(
        configPath);

    FirebaseOptions options = new FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount.getInputStream()))
        .setDatabaseUrl(databaseUrl).build();

    FirebaseApp.initializeApp(options);

    return FirebaseAuth.getInstance();
  }
}
