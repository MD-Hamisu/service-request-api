package com.md.service_request_api.service.impl;

import com.md.service_request_api.constant.Role;
import com.md.service_request_api.model.ServiceRequest;
import com.md.service_request_api.model.User;
import com.md.service_request_api.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    @Override
    public void sendRequestSubmissionEmail(User requester, ServiceRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(requester.getEmail());
        message.setSubject("Service Request Submitted - #" + request.getId());
        message.setText(
                "Dear " + requester.getUsername() + ",\n\n" +
                        "Your service request for \"" + request.getService().getName() + "\" has been submitted successfully.\n" +
                        "Request ID: #" + request.getId() + "\n" +
                        "Submission Date: " + request.getSubmissionDate() + "\n" +
                        "Status: " + request.getStatus() + "\n\n" +
                        "You can track the status of your request in the Service Request System.\n\n" +
                        "Best regards,\nSRS Team"
        );
        try {
            mailSender.send(message);
        } catch (MailException e) {
            // Log the error but don't throw an exception (email failure shouldn't block the main flow)
            System.err.println("Failed to send request submission email to " + requester.getEmail() + ": " + e.getMessage());
        }
    }

    @Async
    @Override
    public void sendRequestStatusChangeEmail(User requester, ServiceRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(requester.getEmail());
        message.setSubject("Service Request Status Updated - #" + request.getId());
        message.setText(
                "Dear " + requester.getUsername() + ",\n\n" +
                        "The status of your service request for \"" + request.getService().getName() + "\" has been updated.\n" +
                        "Request ID: #" + request.getId() + "\n" +
                        "New Status: " + request.getStatus() + "\n\n" +
                        "You can view the details in the Service Request System.\n\n" +
                        "Best regards,\nSRS Team"
        );
        try {
            mailSender.send(message);
        } catch (MailException e) {
            System.err.println("Failed to send request status change email to " + requester.getEmail() + ": " + e.getMessage());
        }
    }

    @Async
    @Override
    public void sendPasswordResetEmail(User user, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(user.getEmail());
        message.setSubject("Your Password Has Been Reset");
        message.setText(
                "Dear " + user.getUsername() + ",\n\n" +
                        "Your password has been reset.\n" +
                        "New New Password: " + newPassword + "\n\n" +
                        "Please log in with your new password and change it if needed.\n\n" +
                        "Best regards,\nSRS Team"
        );
        try {
            mailSender.send(message);
        } catch (MailException e) {
            System.err.println("Failed to send password reset email to " + user.getEmail() + ": " + e.getMessage());
        }
    }

    @Async
    @Override
    public void sendManagerAccountCreatedEmail(User manager, String initialPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(manager.getEmail());
        message.setSubject("Welcome to SRS - Manager Account Created");
        message.setText(
                "Dear " + manager.getUsername() + ",\n\n" +
                        "Your Manager account has been created in the Service Request System.\n" +
                        "You can now log in with the following credentials:\n" +
                        "Username: " + manager.getUsername() + "\n" +
                        "Temporary Password: "+ initialPassword + "\n\n" +
                        "Please log in and change your password immediately.\n\n" +
                        "Best regards,\nSRS Team"
        );
        try {
            mailSender.send(message);
        } catch (MailException e) {
            System.err.println("Failed to send manager account created email to " + manager.getEmail() + ": " + e.getMessage());
        }
    }

    @Override
    public void sendRoleAssignedEmail(User updatedUser, Role role) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(updatedUser.getEmail());
        message.setSubject("Service Request System: New Role Assignment");
        message.setText(
                "Dear " + updatedUser.getUsername() + ",\n\n" +
                        "You have been assigned the role of "+role.name()+"\n" +
                        "You can now be able to perform all operations of "+role.name()+"\n\n" +
                        "Best regards,\nSRS Team"
        );
        try {
            mailSender.send(message);
        } catch (MailException e) {
            System.err.println("Failed to send role update email to " + updatedUser.getEmail() + ": " + e.getMessage());
        }
    }
}
