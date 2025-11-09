package br.com.NatanLima.certificados;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Classe principal que coordena a leitura da planilha, geração dos certificados e envio por e-mail.
 */
public class GeradorCertificados {

    private static final String CAMINHO_PLANILHA = "planilhas/cursos.xlsx";
    private static final String PASTA_CERTIFICADOS = "certificados/";

    public static void main(String[] args) {
        try {
            List<Registro> registros = LeitorPlanilhas.lerPlanilha(CAMINHO_PLANILHA);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (Registro registro : registros) {

                // 1. Pula se já foi enviado
                if (registro.getCertificadoEnviado() != null && !registro.getCertificadoEnviado().isBlank()) {
                    System.out.println("⏩ Certificado já enviado para: " + registro.getNomeCompleto());
                    continue;
                }

                // ✅ CORREÇÃO 1: Pular linhas em branco ou incompletas
                // Isso impede o crash de 'DateTimeParseException' em linhas vazias
                if (registro.getNomeCompleto() == null || registro.getNomeCompleto().isBlank() ||
                        registro.getDataFinal() == null || registro.getDataFinal().isBlank()) {
                    // Não é um erro, apenas pula a linha em branco no final da planilha
                    continue;
                }

                LocalDate dataFinalCurso = LocalDate.parse(registro.getDataFinal(), formatter);
                LocalDate hoje = LocalDate.now();

                // 2. Só envia se a data final for hoje ou anterior
                if (dataFinalCurso.isAfter(hoje)) {
                    System.out.println("📅 Curso ainda não finalizado para: " + registro.getNomeCompleto());
                    continue;
                }

                // 3. Gerar HTML
                String html = gerarHtmlCertificado(registro);

                // 4. Gerar o PDF
                String caminhoCertificado = PASTA_CERTIFICADOS + registro.getNomeCompleto().replace(" ", "_") + ".pdf";
                ConversorHtmlParaPdf.converter(html, caminhoCertificado);

                // 5. Enviar por e-mail
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

                // 6. Atualizar planilha
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

        try (java.io.InputStream is = GeradorCertificados.class
                .getClassLoader()
                .getResourceAsStream("modelo-certificado.html")) {

            if (is == null) {
                throw new Exception("ERRO: Arquivo 'modelo-certificado.html' não encontrado em /src/main/resources/");
            }

            String html = new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);

            html = html.replace("${NOME_PALESTRANTE}", r.getNomeCompleto());
            html = html.replace("${NOME_CURSO}", r.getNomeDoCurso());
            html = html.replace("${CARGA_HORARIA}", r.getCargaHoraria());
            html = html.replace("${DATA_CURSO}", r.getDataFinal());
            html = html.replace("${LOCAL_AULA}", r.getLocalDaAula());

            return html;
        }
    }
}