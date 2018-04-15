package ru.javaops.masterjava.persist.model;


import com.bertoncelj.jdbi.entitymapper.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class City extends BaseEntity{

  private @NonNull String name;
  @Column("full_name")
  private String fullName;

  public City(Integer id, String name, String fullName) {
    this(name, fullName);
    this.id = id;
  }
}
