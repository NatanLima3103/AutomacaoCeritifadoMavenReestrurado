package br.com.NatanLima.certificados;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.File;
import java.util.Properties;

/**
 * Classe responsável por enviar e-mails com anexo (certificados em PDF).
 */
public class ServicoEmail {

    private static final String ENV_EMAIL = "CERTIFICADO_EMAIL";
    private static final String ENV_SENHA = "CERTIFICADO_SENHA";

    /**
     * Envia um e-mail com anexo (PDF do certificado).
     */
    public static void enviarEmailComAnexo(String destinatario, String assunto, String corpoEmail, String caminhoAnexo)
            throws Exception
    {
        // 📧 Pegando as credenciais do ambiente para segurança
        // ✅ CORREÇÃO APLICADA: Usa o NOME da variável para obter o VALOR
        String EMAIL_REMETENTE = System.getenv(ENV_EMAIL);
        String SENHA_REMETENTE = System.getenv(ENV_SENHA);

        if (EMAIL_REMETENTE == null || SENHA_REMETENTE == null) {
            System.err.println("❌ ERRO: Variáveis de ambiente (" + ENV_EMAIL + " e " + ENV_SENHA + ") não configuradas!");
            System.err.println("→ Certifique-se de que os NOMES estão corretos nas configurações de ambiente.");
            throw new Exception("Variáveis de ambiente não configuradas. Verifique as credenciais.");
        }

        try {
            // ⚙️ Configurações do servidor SMTP (Gmail)
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.debug", "true");

            // Cria sessão autenticada, usando as credenciais lidas corretamente
            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    // Usa a senha de app configurada
                    return new PasswordAuthentication(EMAIL_REMETENTE, SENHA_REMETENTE);
                }
            });
            session.setDebug(true);

            // Cria a mensagem
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_REMETENTE, "Emissor de Certificados"));
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
                throw new Exception("Arquivo PDF não encontrado: " + caminhoAnexo);
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
            throw e;
        } catch (SendFailedException e) {
            System.err.println("❌ Falha ao enviar o e-mail. Verifique o endereço do destinatário: " + destinatario);
            throw e;
        } catch (Exception e) {
            System.err.println("❌ Erro inesperado ao enviar o e-mail: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}