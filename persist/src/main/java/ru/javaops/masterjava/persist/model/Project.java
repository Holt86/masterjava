package ru.javaops.masterjava.persist.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Project extends BaseEntity {

  private @NonNull String name;
  private String description;

  public Project(Integer id, String name, String description) {
    this(name, description);
    this.id = id;
  }
}
