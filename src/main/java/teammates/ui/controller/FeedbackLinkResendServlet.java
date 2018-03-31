package teammates.ui.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.exception.EmailSendingException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.logic.api.EmailGenerator;
import teammates.logic.api.EmailSender;

public class FeedbackLinkResendServlet extends HttpServlet {

    /**
     * This is a email regex to test whether the input is a valid email.
     */
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final Logger log = Logger.getLogger();

    private EmailSender emailSender;

    @Override
    public final void doGet(HttpServletRequest req, HttpServletResponse resp) {
        this.doPost(req, resp);
    }

    @Override
    public final void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String userEmailToResend = req.getParameter(Const.ParamsNames.STUDENT_EMAIL);
        EmailWrapper email = new EmailGenerator().generateFeedbackSessionResendEmail(userEmailToResend);
        setEmailSender(new EmailSender());

        try {
            if (!isValidEmailAddress(userEmailToResend)) {
                emailSender.sendEmail(email);
            }
        } catch (EmailSendingException e) {
            log.severe("Email of feedback session links failed to send: "
                    + TeammatesException.toStringWithStackTrace(e));
        }
    }

    public void setEmailSender(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    /**
     * Verify whether the input email address is valid.
     * @param emailAddress a string containing the email address of the user
     * @return a boolean indicating whether the input email is valid
     */
    public boolean isValidEmailAddress(String emailAddress) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailAddress);
        return matcher.find();
    }

}
