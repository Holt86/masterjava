package ru.javaops.masterjava.web.handler;

import com.sun.xml.ws.api.handler.MessageHandlerContext;
import java.util.Date;
import ru.javaops.masterjava.web.Statistics;
import ru.javaops.masterjava.web.Statistics.RESULT;

/**
 * Created by user on 13.11.2018.
 */
public class SoapStatisticsHandlers extends SoapBaseHandler {

  private static final String START_TIME = "START_TIME";
  private static final String PAYLOAD = "PAYLOAD";

  @Override
  public boolean handleMessage(MessageHandlerContext context) {
    if (!isOutbound(context)){
      context.put(START_TIME, new Date().getTime());
      context.put(PAYLOAD, context.getMessage().getPayloadLocalPart());
    }else {
      Statistics.count(context.get(PAYLOAD).toString(), (Long)context.get(START_TIME), RESULT.SUCCESS);
    }
    return true;
  }

  @Override
  public boolean handleFault(MessageHandlerContext context) {
    Statistics.count(context.get(PAYLOAD).toString(), (Long)context.get(START_TIME), RESULT.SUCCESS);
    return true;
  }
}
