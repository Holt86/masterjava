package ru.javaops.masterjava.service.mail.rest;

import com.google.common.collect.ImmutableList;
import java.io.InputStream;
import java.util.Collections;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.hibernate.validator.constraints.NotBlank;
import ru.javaops.masterjava.service.mail.Attachment;
import ru.javaops.masterjava.service.mail.GroupResult;
import ru.javaops.masterjava.service.mail.MailServiceExecutor;
import ru.javaops.masterjava.service.mail.MailWSClient;
import ru.javaops.masterjava.service.mail.util.Attachments;

@Path("/")
public class MailRS {

  @GET
  @Path("test")
  @Produces(MediaType.TEXT_PLAIN)
  public String test() {
    return "Test";
  }

  @POST
  @Path("send")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public GroupResult send(@NotBlank @FormDataParam("users") String users,
      @FormDataParam("subject") String subject,
      @NotBlank @FormDataParam("body") String body, @FormDataParam("attach") FormDataBodyPart attach)
      throws Exception {
    Attachment attachment = null;
    if (attach != null){
      String fileName = new String(attach.getContentDisposition().getFileName().getBytes("ISO-8859-1"), "UTF-8");
      InputStream is = ((BodyPartEntity)attach.getEntity()).getInputStream();
      attachment = Attachments.getAttachmentWithMultiStream(fileName, attach.getMediaType().toString(), is);
    }
    return MailServiceExecutor
        .sendBulk(MailWSClient.split(users), subject, body, attachment == null ? Collections.emptyList() :
            ImmutableList.of(attachment));
  }
}