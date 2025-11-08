package br.com.NatanLima.certificados;

import br.com.NatanLima.certificados.util.LeitorPlanilha;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class GeradorCertificados {

    public void gerarCertificados() {
        List<LeitorPlanilha.Registro> registros = LeitorPlanilha.lerPlanilha("cursos.xlsx");

        for (LeitorPlanilha.Registro r : registros) {
            try (InputStream htmlStream = getClass().getResourceAsStream("/modelo-certificado.html")) {
                assert htmlStream != null;
                String htmlTemplate = new Scanner(htmlStream, StandardCharsets.UTF_8).useDelimiter("\\A").next();

                String htmlFinal = htmlTemplate
                        .replace("{{NOME_COMPLETO}}", r.getNomeCompleto())
                        .replace("{{CURSO}}", r.getNomeCurso())
                        .replace("{{CARGA_HORARIA}}", r.getCargaHoraria())
                        .replace("{{DATA_FINAL}}", r.getDataFinal())
                        .replace("{{LOCAL_AULA}}", r.getLocalDaAula());

                String nomeArquivo = "certificados/" + r.getNomeCompleto().replace(" ", "_") + ".pdf";
                ConversorHtmlParaPdf.converter(htmlFinal, nomeArquivo);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
