package org.dava.davaquiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "org.dava")
@EnableJpaRepositories(basePackages = "org.dava.dao")
@EntityScan(basePackages = "org.dava.domain")
public class DavaQuizApplication {

  public static void main(String[] args) {
    SpringApplication.run(DavaQuizApplication.class, args);
  }
}
