
package org.dava.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "hello")
public class HelloEntity {

    @Id
    private Long id;
    private String message;
}
