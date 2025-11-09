package br.com.NatanLima.certificados;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Classe responsável por ler e atualizar os dados da planilha Excel.
 */
public class LeitorPlanilhas {

    /**
     * Lê os registros da planilha Excel.
     *
     * @param caminhoArquivo Caminho do arquivo XLSX (ex: "planilhas/cursos.xlsx")
     * @return Lista de objetos Registro com os dados da planilha.
     */
    public static List<Registro> lerPlanilha(String caminhoArquivo) {
        List<Registro> registros = new ArrayList<>();
        try (FileInputStream file = new FileInputStream(new File(caminhoArquivo));
             Workbook workbook = new XSSFWorkbook(file)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = sheet.iterator();

            // Pular cabeçalho
            if (iterator.hasNext()) iterator.next();

            while (iterator.hasNext()) {
                Row row = iterator.next();
                Registro r = new Registro();
                r.setNomeCompleto(getValorCelula(row.getCell(0)));         // Coluna A
                r.setEmail(getValorCelula(row.getCell(1)));                // Coluna B
                r.setNomeDoCurso(getValorCelula(row.getCell(2)));          // Coluna C
                r.setCargaHoraria(getValorCelula(row.getCell(3)));         // Coluna D
                r.setDataFinal(getValorCelula(row.getCell(4)));            // Coluna E

                // ✅ CORREÇÃO AQUI (Baseado na sua imagem da planilha)
                r.setCertificadoEnviado(getValorCelula(row.getCell(5)));   // Coluna F
                r.setLocalDaAula(getValorCelula(row.getCell(6)));          // Coluna G

                registros.add(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Erro ao ler a planilha: " + e.getMessage());
        }

        return registros;
    }

    /**
     * Atualiza o status de envio na planilha Excel.
     *
     * @param caminhoArquivo Caminho da planilha (ex: "planilhas/cursos.xlsx")
     * @param registro       Objeto Registro com o nome e status atualizado
     */
    public static void atualizarStatusEnvio(String caminhoArquivo, Registro registro) {
        try (FileInputStream file = new FileInputStream(new File(caminhoArquivo));
             Workbook workbook = new XSSFWorkbook(file)) {

            Sheet sheet = workbook.getSheetAt(0);

            // Percorrer linhas até encontrar o aluno
            for (Row row : sheet) {
                Cell cellNome = row.getCell(0);
                if (cellNome != null && getValorCelula(cellNome).equalsIgnoreCase(registro.getNomeCompleto())) {

                    // ✅ CORREÇÃO AQUI (Baseado na sua imagem da planilha)
                    // Deve escrever na Coluna F (índice 5)
                    Cell cellStatus = row.getCell(5); // Coluna "Certificado Enviado"
                    if (cellStatus == null) cellStatus = row.createCell(5);

                    cellStatus.setCellValue(registro.getCertificadoEnviado());
                    break;
                }
            }

            // Salvar alterações
            try (FileOutputStream out = new FileOutputStream(caminhoArquivo)) {
                workbook.write(out);
            }

            System.out.println("📝 Planilha atualizada para: " + registro.getNomeCompleto());

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Erro ao atualizar a planilha: " + e.getMessage());
        }
    }

    /**
     * Método auxiliar para pegar o valor da célula como String.
     */
    private static String getValorCelula(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // ✅ MELHORIA: Formatar data como dd/MM/yyyy
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                    return sdf.format(cell.getDateCellValue());
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}