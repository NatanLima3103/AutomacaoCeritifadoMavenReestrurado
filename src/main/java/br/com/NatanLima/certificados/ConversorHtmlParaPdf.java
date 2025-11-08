package br.com.NatanLima.certificados;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ConversorHtmlParaPdf {

    public static void converter(String html, String caminhoSaidaPdf) throws IOException {
        try (FileOutputStream os = new FileOutputStream(new File(caminhoSaidaPdf))) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();
        } catch (Exception e) {
            throw new IOException("Erro ao gerar PDF: " + e.getMessage(), e);
        }
    }
}
