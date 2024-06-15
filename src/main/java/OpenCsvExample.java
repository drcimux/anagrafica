import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.neo.test.anagrafica.model.AnagraficaCsv;
import com.opencsv.bean.CsvToBeanBuilder;

public class OpenCsvExample {

    public static void main(String[] args) throws IOException {

        String fileName = "test.csv";

        List<AnagraficaCsv> listaAnagraficheCsv = new CsvToBeanBuilder(new FileReader(fileName))
                .withType(AnagraficaCsv.class)
                .withSkipLines(1)
                .build()
                .parse();

        listaAnagraficheCsv.forEach(System.out::println);

    }

}
