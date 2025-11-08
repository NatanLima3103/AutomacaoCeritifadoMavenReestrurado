package br.com.NatanLima.certificados;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.io.File;
import java.util.Properties;

/**
 * Responsável pelo envio de e-mails com anexo.
 */
public class ServicoEmail {

    // --- CONFIGURE AQUI ---
    private static final String SEU_EMAIL = "natan.lima@oabpr.org.br"; // E-mail que vai enviar
    private static final String SUA_SENHA_APP = "zbtc nbpk bxhw krnm"; // A senha de 16 letras gerada
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    // ----------------------

    public static void enviarEmailComAnexo(String paraEmail, String assunto, String corpoMsg, String caminhoAnexo) throws MessagingException {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("natan.lima@oabpr.org.br", "zbtc nbpk bxhw krnm");
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("natan.lima@oabpr.org.br"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(paraEmail));
        message.setSubject(assunto);

        MimeBodyPart mimeBodyPartTexto = new MimeBodyPart();
        mimeBodyPartTexto.setText(corpoMsg);

        MimeBodyPart mimeBodyPartAnexo = new MimeBodyPart();
        try {
            mimeBodyPartAnexo.attachFile(new File(caminhoAnexo));
        } catch (Exception e) {
            e.printStackTrace();
            throw new MessagingException("Erro ao anexar arquivo: " + e.getMessage(), e);
        }

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPartTexto);
        multipart.addBodyPart(mimeBodyPartAnexo);

        message.setContent(multipart);
        Transport.send(message);
    }
}
