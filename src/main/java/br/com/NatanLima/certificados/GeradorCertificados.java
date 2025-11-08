package br.com.NatanLima.certificados;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Classe principal que coordena a leitura da planilha, geração dos certificados e envio por e-mail.
 */
public class GeradorCertificados {

    // Caminho da planilha
    private static final String CAMINHO_PLANILHA = "planilhas/dados.xlsx";

    // Caminho da pasta onde os certificados gerados serão salvos
    private static final String PASTA_CERTIFICADOS = "certificados/";

    public static void main(String[] args) {
        try {
            // 1️⃣ Ler todos os registros da planilha
            List<Registro> registros = LeitorPlanilhas.lerPlanilha(CAMINHO_PLANILHA);

            // 2️⃣ Formato da data que vem na planilha
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            // 3️⃣ Iterar pelos alunos
            for (Registro registro : registros) {
                if (registro.getCertificadoEnviado() != null && !registro.getCertificadoEnviado().isBlank()) {
                    System.out.println("⏩ Certificado já enviado para: " + registro.getNomeCompleto());
                    continue;
                }

                LocalDate dataFinalCurso = LocalDate.parse(registro.getDataFinal(), formatter);
                LocalDate hoje = LocalDate.now();

                // Só envia se a data final for hoje ou anterior
                if (dataFinalCurso.isAfter(hoje)) {
                    System.out.println("📅 Curso ainda não finalizado para: " + registro.getNomeCompleto());
                    continue;
                }

                // 4️⃣ Gerar HTML do certificado substituindo variáveis
                String html = gerarHtmlCertificado(registro);

                // 5️⃣ Gerar o PDF
                String caminhoCertificado = PASTA_CERTIFICADOS + registro.getNomeCompleto().replace(" ", "_") + ".pdf";
                ConversorHtmlParaPdf.converter(html, caminhoCertificado);

                // 6️⃣ Enviar por e-mail
                String assunto = "Certificado do Curso - " + registro.getNomeDoCurso();
                String corpoEmail = "Olá " + registro.getNomeCompleto() + ",\n\n"
                        + "Segue em anexo o seu certificado do curso \"" + registro.getNomeDoCurso() + "\".\n\n"
                        + "Atenciosamente,\nEquipe de Certificação";

                ServicoEmail.enviarEmailComAnexo(
                        registro.getEmail(),
                        assunto,
                        corpoEmail,
                        caminhoCertificado
                );

                // 7️⃣ Atualizar planilha
                String dataEnvio = LocalDate.now().format(formatter);
                registro.setCertificadoEnviado("Enviado na data " + dataEnvio);
                LeitorPlanilhas.atualizarStatusEnvio(CAMINHO_PLANILHA, registro);

                System.out.println("✅ Certificado enviado para " + registro.getNomeCompleto());
            }

            System.out.println("\n🎉 Processo concluído com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Erro ao gerar ou enviar certificados: " + e.getMessage());
        }
    }

    /**
     * Gera o HTML do certificado substituindo variáveis.
     */
    private static String gerarHtmlCertificado(Registro r) throws Exception {
        File modeloHtml = new File("src/main/resources/modelo-certificado.html");
        String html = java.nio.file.Files.readString(modeloHtml.toPath());

        html = html.replace("{{NOME_COMPLETO}}", r.getNomeCompleto());
        html = html.replace("{{CURSO}}", r.getNomeDoCurso());
        html = html.replace("{{CARGA_HORARIA}}", r.getCargaHoraria());
        html = html.replace("{{DATA_FINAL}}", r.getDataFinal());
        html = html.replace("{{LOCAL}}", r.getLocalDaAula());

        return html;
    }
}
