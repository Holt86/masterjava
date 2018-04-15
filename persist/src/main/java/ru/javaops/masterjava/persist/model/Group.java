package ru.javaops.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Created by user on 11.04.2018.
 */

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@RequiredArgsConstructor
public class Group extends BaseEntity {

  private
  @NonNull
  String name;
  @Column("group_type")
  private
  @NonNull
  GroupType groupType;
  @Column("project_id")
  private
  @NonNull
  Integer projectId;
}
