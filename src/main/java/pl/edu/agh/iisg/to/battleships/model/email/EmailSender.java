package pl.edu.agh.iisg.to.battleships.model.email;

import javafx.application.Platform;
import pl.edu.agh.iisg.to.battleships.Main;
import pl.edu.agh.iisg.to.battleships.model.EasyConfigParser;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;


public class EmailSender {

    static private final String emailConfigPathResource = "/emailConfig";

    static int sentEmails = 0;

    static EasyConfigParser parser = new EasyConfigParser( Main.class.getResource(emailConfigPathResource).getPath().replace("%20", " ") );

    /*public EmailSender(){
        // Serwisy SMTP w darmowych pakietach umożliwiają wysyłać maile jedynie do zatwierdzonych adresów email
        // Musicie mi je podać, żebym je dodał, żeby możliwe było wysyłanie na nie maili

        sendEmail("kubakub2@wp.pl", "BattleShips App Notification", createTemplateHtmlEmail("Zostałeś pokonany przez takeshi69", "Wojciech"));
    }*/

    public static void sendEmailLater(String recipient_address, String subject, String data){
        new Thread(() -> {
            Platform.runLater(() -> {
                sendEmail(recipient_address, subject, data);
            });
        }).start();
    }

    public static void sendEmail(String recipient_address, String subject, String data){
        System.out.println("Email nr. " + (++sentEmails) + ": Sending an email to '" + recipient_address + "'.");
        Properties session_properties = System.getProperties();

        String smtp_host = parser.getFromKey("smtp_server");
        String sender_address = parser.getFromKey("email");
        String sender_password = parser.getFromKey("password");
        String smtp_port = parser.getFromKey("smtp_port");

        session_properties.put("mail.smtp.host", smtp_host);
        session_properties.put("mail.smtp.user", sender_address);
        session_properties.put("mail.smtp.password", sender_password);
        session_properties.put("mail.smtp.port", smtp_port);
        session_properties.put("mail.smtp.auth", "true");
        session_properties.put("mail.smtp.starttls.enable", "true");
        session_properties.put("mail.imap.ssl.enable", "true");


        try {
            Session session = Session.getDefaultInstance(session_properties);
            MimeMessage message = new MimeMessage(session);

            Transport transport = session.getTransport("smtp");
            transport.close();
            transport.connect(smtp_host, sender_address, sender_password);

            message.setFrom(new InternetAddress(sender_address));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient_address));
            message.setSubject(subject);
            message.setContent(data, "text/html; charset=UTF-8");

            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            System.out.println("Email nr. " + (sentEmails) + ": Sent successfully.");

        } catch (javax.mail.SendFailedException e){
            System.out.println("Email nr. " + (sentEmails) + ": " + recipient_address + " is not a valid email address.");
        }catch(javax.mail.AuthenticationFailedException e){
            System.out.println("Email service configuration in 'emailConfig' is incorrect and wasn't accepted by the SMTP server (" + smtp_host + ").");
        }catch (MessagingException | IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public static String createTemplateHtmlEmail(String content, String username){
        /*
        * You pass both recipient name (username) and the content of the message.
        * Everything will be packed in nice and tidy HTML template that is compliant with most email services.
        * (tested on Gmail)
        * */
        String filled_HTML_String = "<!DOCTYPE html><html lang='en' id='html'><head> <meta charset='UTF-8'> <title>BattleShips</title> <style>#body{margin: 0; padding: 0; height: 100%; background: #CCC; font-size: max(2vmin, 12px); line-height: max(2vmin, 12px);}#tbody{margin: 0; padding: 0;}#workspace{margin: 0; padding: 0; width: 100%;}#content{color: #333; font-size: max(3vmin, 18px); line-height: max(3vmin, 18px); padding: max(10vmin, 60px) 0; text-align: center;}#emailContentRecipient{font-size: max(6vmin, 36px); line-height: max(6vmin, 36px); font-weight: bold;}#header, #footer{background: #333; color: #CCC; padding: max(4vmin, 24px) !important; text-align: center;}#header{font-size: max(4vmin, 24px) !important; line-height: max(4vmin, 24px) !important;}#footer{font-size: max(3vmin, 18px) !important; line-height: max(3vmin, 18px) !important;}#miniSubtitle{font-size: max(2vmin, 12px) !important; line-height: max(2vmin, 12px) !important;}#footer a{color: inherit;}</style></head><body id='body'><table id='workspace' width='1000'> <tbody id='tbody'> <tr> <td id='header'> Battleships App </td></tr><tr> <td id='content'> <p id='emailContentRecipient'> Hey " + username + "! </p><p id='emailContent'> " + content + " </p></td></tr><tr> <td id='footer'> <p> It's a mail generated by Battleships App, written for Object Based Programming course at Akademia Gorniczo Hutnicza w Krakowie. </p><p> Creators: <a target='_blank' href='https://github.com/def-au1t'>Jacek Nitychoruk</a>, <a target='_blank' href='https://github.com/GabenRulez'>Wojciech Kosztyla</a>, <a target='_blank' href='https://github.com/KartonM'>Marcin Kozubek</a>, <a target='_blank' href='https://github.com/pawek001'>Pawel Kielbasa</a>. </p><p id='miniSubtitle'> Design: Wojciech Kosztyla </p></td></tr></tbody></table></body></html>";
        filled_HTML_String = deletePolishCharacters(filled_HTML_String);
        return filled_HTML_String;
    }

    public static String deletePolishCharacters(String stringToDePolify){
        return stringToDePolify.replace("ę","e").replace("ó","o").replace("ą","a").replace("ś","s").replace("ł","l").replace("ż","z").replace("ź","z").replace("ć","c").replace("ń","n");
    }

}
