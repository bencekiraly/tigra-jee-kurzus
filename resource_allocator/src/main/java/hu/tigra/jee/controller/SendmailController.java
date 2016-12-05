/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hu.tigra.jee.controller;


import hu.tigra.jee.model.EmailModel;
import hu.tigra.jee.service.StoreEmails;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


// The @Model stereotype is a convenience mechanism to make this a request-scoped bean that has an
// EL name
// Read more about the @Model stereotype in this FAQ:
// http://www.cdi-spec.org/faq/#accordion6
@Model
public class SendmailController {

    @Inject
    private FacesContext facesContext;

    @Inject
    private StoreEmails storeEmails;

    @Produces
    @Named
    private EmailModel newEmailModel;

    @PostConstruct
    public void initNewEmailModel() {
        newEmailModel = new EmailModel();
    }

    private String host = "smtp.gmail.com";

    final String username = "felhasználónév";
    final String password = "jelszó";

    public void send() throws Exception {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        // Get the Session object.
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            storeEmails.send(newEmailModel);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Elküldve!", "Az email küldés sikeres!!");
            facesContext.addMessage(null, m);
            FacesContext.getCurrentInstance().getExternalContext().redirect("emails.jsf");
            sendMail(session, newEmailModel.getSender(), newEmailModel.getAddress(), newEmailModel.getSubject(), newEmailModel.getText());
            initNewEmailModel();
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
            System.out.println(e.getMessage());
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "A küldés nem sikerült");
            facesContext.addMessage(null, m);
        }
    }

    private void sendMail(Session session, String from, String to, String subject, String text) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(text);
            Transport.send(message);
        } catch (MessagingException e) {
            System.out.println(e.getStackTrace());
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String getRootErrorMessage(Exception e) {
        // Default to general error message that registration failed.
        String errorMessage = "Registration failed. See server log for more information";
        if (e == null) {
            // This shouldn't happen, but return the default messages
            return errorMessage;
        }

        // Start with the exception and recurse to find the root cause
        Throwable t = e;
        while (t != null) {
            // Get the message from the Throwable class instance
            errorMessage = t.getLocalizedMessage();
            t = t.getCause();
        }
        // This is the root cause message
        return errorMessage;
    }

}
