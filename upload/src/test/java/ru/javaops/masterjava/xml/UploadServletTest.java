package ru.javaops.masterjava.xml;

import static org.mockito.Mockito.*;

import com.google.common.io.Resources;
import java.io.InputStream;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author - A.Ovechnikov
 * @date - 24.03.2018
 */
public class UploadServletTest {

  private InputStream is;
  @Mock
  private HttpServletRequest request;
  @Mock
  private HttpServletResponse response;
  @Mock
  private RequestDispatcher dispatcher;
  @Mock
  private Part part;

  @Before
  public void init() throws Exception {
    MockitoAnnotations.initMocks(this);
    is = Resources.getResource("payload.xml").openStream();
    when(part.getInputStream()).thenReturn(is);
    when(request.getPart("file")).thenReturn(part);
    when(request.getRequestDispatcher(any())).thenReturn(dispatcher);
  }

  @Test
  public void testDoPost() throws Exception {
    new UploadServlet().doPost(request, response);
    verify(request, atLeast(1)).getPart("file");
    verify(request, atLeast(1)).setAttribute(anyString(), anyList());
  }
}