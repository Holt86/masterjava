<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Upload XML</title>
</head>
<body>
<form method="POST" action="upload" enctype="multipart/form-data" >
    File:
    <input type="file" name="file" id="file" /> <br/>
    </br>
    <input type="submit" value="Upload" name="upload" id="upload" />
</form>
<br/>
<br/>
<table border="1" cellpadding="8" cellspacing="0">
    <tr>
        <th>Name</th>
        <th>Email</th>
        <th>Flag</th>
    </tr>
    <c:forEach items="${users}" var="user">
        <jsp:useBean id="user" scope="page" type="ru.javaops.masterjava.xml.schema.User"/>
        <tr>
            <td>${user.value}</td>
            <td>${user.email}</td>
            <td>${user.flag.value()}</td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
