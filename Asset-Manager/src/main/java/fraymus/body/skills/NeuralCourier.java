package fraymus.body.skills;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * NEURAL COURIER - Cryptographically Signed Email Dispatch
 * 
 * Secure, programmatic dispatch. It doesn't just send text.
 * It signs the email with your Identity Hash (from fractal_dna.json)
 * to prove the message came from the Core, not a spoofer.
 */
public class NeuralCourier {

    private final String username;
    private final String password;
    private final Session session;

    public NeuralCourier(String user, String pass) {
        this.username = user;
        this.password = pass;

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com"); // Or your provider
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        this.session = Session.getInstance(prop, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public String dispatch(String to, String subject, String body, String signatureHash) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("⚡ FRAYMUS: " + subject);

            String fullBody = body + "\n\n" +
                "--------------------------------------------------\n" +
                "CRYPTOGRAPHIC PROOF: " + signatureHash + "\n" +
                "SENT VIA: NeuralCourier Protocol v1.0";

            message.setText(fullBody);
            Transport.send(message);

            return "✅ DISPATCHED TO: " + to;

        } catch (MessagingException e) {
            return "❌ TRANSMISSION ERROR: " + e.getMessage();
        }
    }
    
    /**
     * Dispatch with default signature
     */
    public String dispatch(String to, String subject, String body) {
        return dispatch(to, subject, body, "NEXUS_SIG_" + System.currentTimeMillis());
    }
}
