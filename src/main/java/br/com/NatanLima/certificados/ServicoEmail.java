package br.com.NatanLima.certificados;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.File;
import java.util.Properties;

/**
 * Classe responsável por enviar e-mails com anexo (certificados em PDF).
 */
public class ServicoEmail {

    /**
     * Envia um e-mail com anexo (PDF do certificado).
     *
     * @param destinatario E-mail de destino
     * @param assunto      Assunto do e-mail
     * @param corpoEmail   Texto do corpo
     * @param caminhoAnexo Caminho do arquivo PDF a ser anexado
     */
    public static void enviarEmailComAnexo(String destinatario, String assunto, String corpoEmail, String caminhoAnexo) {
        // 📧 Pegando as credenciais do ambiente para segurança
        String EMAIL_REMETENTE = System.getenv("CERTIFICADO_EMAIL");
        String SENHA_REMETENTE = System.getenv("CERTIFICADO_SENHA");

        if (EMAIL_REMETENTE == null || SENHA_REMETENTE == null) {
            System.err.println("❌ ERRO: Variáveis de ambiente CERTIFICADO_EMAIL e CERTIFICADO_SENHA não configuradas!");
            System.err.println("→ Configure-as antes de rodar o sistema.");
            return;
        }

        try {
            // ⚙️ Configurações do servidor SMTP (Gmail)
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            props.put("mail.debug", "true"); // <-- Mostra o que o servidor responde

            // Cria sessão autenticada
            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_REMETENTE, SENHA_REMETENTE);
                }
            });

            // Monta o e-mail
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_REMETENTE, "Sistema de Certificados"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(assunto);

            // Corpo do e-mail (HTML)
            MimeBodyPart corpo = new MimeBodyPart();
            corpo.setContent(corpoEmail, "text/html; charset=utf-8");

            // Anexo (PDF)
            MimeBodyPart anexo = new MimeBodyPart();
            File arquivo = new File(caminhoAnexo);
            if (!arquivo.exists()) {
                System.err.println("⚠️ Arquivo não encontrado: " + caminhoAnexo);
                return;
            }
            anexo.attachFile(arquivo);

            // Montagem final
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(corpo);
            multipart.addBodyPart(anexo);
            message.setContent(multipart);

            // 🚀 Envia o e-mail
            Transport.send(message);

            System.out.println("✅ E-mail enviado com sucesso para: " + destinatario);

        } catch (AuthenticationFailedException e) {
            System.err.println("❌ Falha de autenticação: verifique o e-mail e a senha de app!");
        } catch (SendFailedException e) {
            System.err.println("❌ Falha ao enviar o e-mail. Verifique o endereço do destinatário: " + destinatario);
        } catch (Exception e) {
            System.err.println("❌ Erro inesperado ao enviar o e-mail: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
