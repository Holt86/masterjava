package ru.javaops.masterjava.service.mail.util;

import static com.google.common.io.ByteStreams.toByteArray;

import com.sun.xml.ws.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import lombok.AllArgsConstructor;
import org.apache.commons.io.input.CloseShieldInputStream;
import ru.javaops.masterjava.service.mail.Attachment;

public class Attachments {
    public static Attachment getAttachment(String name, InputStream inputStream) {
        return new Attachment(name, new DataHandler(new InputStreamDataSource(inputStream)));
    }

  public static Attachment getAttachmentWithMultiStream(String fileName, String contentType,
      InputStream is)
      throws IOException {
    DataSource dataSource = new ByteArrayDataSource(toByteArray(is), contentType);
    return new Attachment(fileName, new DataHandler(dataSource));
  }

    //    http://stackoverflow.com/questions/2830561/how-to-convert-an-inputstream-to-a-datahandler
    //    http://stackoverflow.com/a/10783565/548473
    @AllArgsConstructor
    private static class InputStreamDataSource implements DataSource {
        private InputStream inputStream;

        @Override
        public InputStream getInputStream() throws IOException {
            return new CloseShieldInputStream(inputStream);
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public String getContentType() {
            return "application/octet-stream";
        }

        @Override
        public String getName() {
            return "";
        }
    }
}
