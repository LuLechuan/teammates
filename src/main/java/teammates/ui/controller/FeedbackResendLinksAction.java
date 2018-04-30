package teammates.ui.controller;

import teammates.common.datatransfer.UserType;
import teammates.common.exception.EmailSendingException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.FieldValidator;
import teammates.common.util.Logger;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.logic.api.EmailGenerator;
import teammates.ui.pagedata.FeedbackResendLinksPageData;
import teammates.ui.pagedata.PageData;

public class FeedbackResendLinksAction extends Action {

    private static final Logger log = Logger.getLogger();

    private static UserType userType;

    @Override
    protected void authenticateUser() {
        // This feature is for checking whether the userType is an Admin.
        // If so, the Recaptcha verification in the execution step will be skipped.
        userType = gateKeeper.getCurrentUser();
    }

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        try {
            FieldValidator validator = new FieldValidator();
            String userEmailToResend = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
            String recaptchaResponse = getNonNullRequestParamValue(Const.ParamsNames.RECAPTCHA_RESPONSE);

            statusToAdmin = "Resend links requested"
                    + "<br>Email: " + userEmailToResend;
            String error = validator.getInvalidityInfoForEmail(userEmailToResend);

            if (error.isEmpty() && recaptchaResponse.isEmpty() && (userType == null || !userType.isAdmin)) {
                error = Const.StatusMessages.RECAPTCHA_VALIDATION_FAILED;
            }

            if (error.isEmpty()) {
                EmailWrapper email = new EmailGenerator().generateFeedbackSessionResendLinksEmail(userEmailToResend);
                emailSender.sendEmail(email);
                statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_ACCESS_LINKS_RESENT,
                        StatusMessageColor.SUCCESS));
            } else {
                statusToUser.add(new StatusMessage(error, StatusMessageColor.DANGER));
            }

            PageData data = new FeedbackResendLinksPageData(account, sessionToken, error);

            return createAjaxResult(data);
        } catch (EmailSendingException e) {
            log.severe("Email of feedback session links failed to send: "
                    + TeammatesException.toStringWithStackTrace(e));
        }

        return null;
    }

    @Override
    public ActionResult executeAndPostProcess() {
        if (!isValidUser()) {
            return createRedirectResult(getAuthenticationRedirectUrl());
        }

        // get the result from the child class.
        ActionResult response;
        try {
            response = execute();
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }

        // set error flag of the result
        response.isError = isError;

        response.responseParams.put(Const.ParamsNames.ERROR, Boolean.toString(response.isError));

        // Pass status message using session to prevent XSS attack
        if (!response.getStatusMessage().isEmpty()) {
            putStatusMessageToSession(response);
        }

        return response;
    }
}