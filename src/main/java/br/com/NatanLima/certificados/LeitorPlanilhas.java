package br.com.NatanLima.certificados.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LeitorPlanilha {

    public static class Registro {
        private final String nomeCompleto;
        private final String email;
        private final String nomeCurso;
        private final String cargaHoraria;
        private final String dataFinal;
        private final String certificadoEnviado;
        private final String localDaAula;

        public Registro(String nomeCompleto, String email, String nomeCurso,
                        String cargaHoraria, String dataFinal, String certificadoEnviado, String localDaAula) {
            this.nomeCompleto = nomeCompleto;
            this.email = email;
            this.nomeCurso = nomeCurso;
            this.cargaHoraria = cargaHoraria;
            this.dataFinal = dataFinal;
            this.certificadoEnviado = certificadoEnviado;
            this.localDaAula = localDaAula;
        }

        public String getNomeCompleto() { return nomeCompleto; }
        public String getEmail() { return email; }
        public String getNomeCurso() { return nomeCurso; }
        public String getCargaHoraria() { return cargaHoraria; }
        public String getDataFinal() { return dataFinal; }
        public String getCertificadoEnviado() { return certificadoEnviado; }
        public String getLocalDaAula() { return localDaAula; }
    }

    public static List<Registro> lerPlanilha(String nomeArquivo) {
        List<Registro> registros = new ArrayList<>();

        try (InputStream arquivo = LeitorPlanilha.class.getResourceAsStream("/" + nomeArquivo);
             Workbook workbook = new XSSFWorkbook(arquivo)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = sheet.iterator();

            if (iterator.hasNext()) iterator.next(); // Ignora cabeçalho

            while (iterator.hasNext()) {
                Row row = iterator.next();

                String nomeCompleto = getCellValue(row.getCell(0));
                String email = getCellValue(row.getCell(1));
                String nomeCurso = getCellValue(row.getCell(2));
                String cargaHoraria = getCellValue(row.getCell(3));
                String dataFinal = getCellValue(row.getCell(4));
                String certificadoEnviado = getCellValue(row.getCell(5));
                String localDaAula = getCellValue(row.getCell(6));

                registros.add(new Registro(
                        nomeCompleto, email, nomeCurso, cargaHoraria, dataFinal, certificadoEnviado, localDaAula
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return registros;
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().toLocalDate().toString();
            } else {
                return String.valueOf((int) cell.getNumericCellValue());
            }
        }
        return cell.getStringCellValue();
    }
}
