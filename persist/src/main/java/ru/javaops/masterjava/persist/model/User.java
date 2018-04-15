package ru.javaops.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;
import java.util.List;
import lombok.*;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class User extends BaseEntity {

  @Column("full_name")
  private
  @NonNull
  String fullName;
  private
  @NonNull
  String email;
  private
  @NonNull
  UserFlag flag;
  @Column("city_name")
  private
  @NonNull
  String cityName;

  public User(Integer id, String fullName, String email, UserFlag flag, String cityName) {
    this(fullName, email, flag, cityName);
    this.id = id;
  }
}