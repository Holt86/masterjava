package ru.javaops.masterjava.web.handler;

import com.sun.xml.ws.api.handler.MessageHandlerContext;
import com.sun.xml.ws.transport.Headers;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.xml.ws.handler.MessageContext;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.javaops.masterjava.web.AuthUtil;
import ru.javaops.masterjava.web.WsClient;
import ru.javaops.masterjava.web.WsClient.Credentials;

/**
 * Created by user on 11.11.2018.
 */
@NoArgsConstructor
@AllArgsConstructor
public class SoapServerSecurityHandler extends SoapBaseHandler {

  private String authBasicEncode;

  @Override
  public boolean handleMessage(MessageHandlerContext context) {
    if (!isOutbound(context)){
      Headers headers = (Headers) context.get(MessageContext.HTTP_REQUEST_HEADERS);

      int code = AuthUtil.checkBasicAuth(headers, authBasicEncode);
      if (code != 0) {
        headers.put(MessageContext.HTTP_RESPONSE_CODE, Arrays.asList(String.valueOf(code)));
        throw new SecurityException();
      }
    }
    return true;
  }

  @Override
  public boolean handleFault(MessageHandlerContext context) {
    return false;
  }

}
