package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.pdftest.matchers.ContainsExactText;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static org.hamcrest.MatcherAssert.assertThat;

public class TestWithFiles {

    String NameXLSX = "SimpleXLSX.xlsx";
    String NamePDF = "SimplePDF.pdf";
    String NameCSV = "SimpleCSV.csv";

     @Test
    void zipParsingTest() throws Exception {
        ZipFile zf = new ZipFile(new File("src/test/resources/zip/Arxiv.zip"));
        ZipInputStream is = new ZipInputStream(TestWithFiles.class
                .getClassLoader()
                .getResourceAsStream("zip/Arxiv.zip"));
        ZipEntry entry;
        while ((entry = is.getNextEntry()) != null) {
            try (InputStream inputStream = zf.getInputStream(entry)) {
                if (entry.getName().equals(NamePDF)) {
                    PDF pdf = new PDF(inputStream);
                    Assertions.assertEquals(2, pdf.numberOfPages);
                    assertThat(pdf, new ContainsExactText("A Simple PDF File"));
                    System.out.println("PDF файл " + entry.getName() + " успешно проверен");
                }
                if (entry.getName().equals(NameXLSX)) {
                    XLS xls = new XLS(inputStream);
                    String stringCellValue = xls.excel
                            .getSheetAt(0)
                            .getRow(1)
                            .getCell(0)
                            .getStringCellValue();
                    org.assertj.core.api.Assertions.assertThat(stringCellValue).contains("Honda");
                    System.out.println("XLSX файл " + entry.getName() + " успешно проверен");
                }
                if (entry.getName().equals(NameCSV)) {
                    try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                        List<String[]> content = reader.readAll();
                        org.assertj.core.api.Assertions.assertThat(content).contains(
                                new String[]{"Name", "Surname"},
                                new String[]{"Dmitrii", "Tuchs"},
                                new String[]{"Artem", "Eroshenko"}
                        );
                        System.out.println("CSV файл " + entry.getName() + " успешно проверен");
                    }
                }
            }
        }
    }
}

