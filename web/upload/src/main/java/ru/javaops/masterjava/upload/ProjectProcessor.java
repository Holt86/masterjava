package ru.javaops.masterjava.upload;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import lombok.val;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.GroupDao;
import ru.javaops.masterjava.persist.dao.ProjectDao;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.Project;
import ru.javaops.masterjava.persist.model.type.GroupType;
import ru.javaops.masterjava.xml.schema.ObjectFactory;

import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

/**
 * Created by user on 22.09.2018.
 */
public class ProjectProcessor {

  private static final JaxbParser parser = new JaxbParser(ObjectFactory.class);
  private final ProjectDao projectDao = DBIProvider.getDao(ProjectDao.class);
  private final GroupDao groupDao = DBIProvider.getDao(GroupDao.class);

  public Map<String, Group> process(StaxStreamProcessor processor) throws XMLStreamException, JAXBException {
    val unmarshaller = parser.createUnmarshaller();
    val projects = projectDao.getAsMap();

    while (processor.startElement("Project", "Projects")) {
      ru.javaops.masterjava.xml.schema.Project xmlProject = unmarshaller.unmarshal(
          processor.getReader(), ru.javaops.masterjava.xml.schema.Project.class);
      Project project = new Project(xmlProject.getName(), xmlProject.getDescription());
      if (projects.get(project.getName()) == null) {
        int id = projectDao.insertGeneratedId(project);
        val groups = xmlProject.getGroup().stream()
            .map(group -> new Group(group.getName(), GroupType.valueOf(group.getType().name()), id))
            .collect(Collectors.toList());
        if (!groups.isEmpty()) {
          groupDao.insertButch(groups, groups.size());
        }
      }
    }
    return groupDao.getAsMap();
  }

}
