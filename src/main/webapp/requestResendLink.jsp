<%@ page pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="jsIncludes">
  <script src="https://www.google.com/recaptcha/api.js" async defer></script>
  <script type="text/javascript" src="/js/requestResendLink.js"></script>
</c:set>
<t:statusMessage statusMessagesToUser="${data.statusMessage}"/>
<t:staticPage jsIncludes="${jsIncludes}">
  <link type="text/css" href="/stylesheets/teammatesCommon.css" rel="stylesheet">
  <h1 class="color-orange">
    Request for Resend of Feedback Links
  </h1>
  <p id="message">Please enter your email address, an email containing links to all the feedback sessions that you participated in the recent one month will be resend to you.</p>
  <form id="requestForm" action="/requestResendLinkSuccess.jsp" name="requestForm" method="POST">
    <input id="email" class="width-200-px" name="studentemail" type="text" placeholder="Enter your email address">
    <br/>
    <br/>
    <div class="g-recaptcha" data-sitekey="6LeRFFAUAAAAAJZxGECyrBpqEKr4k5twbsNJnp1R"></div>
    <br/>
    <input id="submitButton" type="submit" value="Submit">
  </form>
</t:staticPage>
