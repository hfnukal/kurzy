package com.realpad.example.kurzy;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.http.HttpMethod;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KurzyService {

  private String URL = "https://www.cnb.cz/cs/financni-trhy/devizovy-trh/kurzy-devizoveho-trhu/kurzy-devizoveho-trhu/rok.txt?rok={}";

  private List<Kurz> getData(String year) {
    RestTemplate restTemplate = new RestTemplate();
    String url = URL.replace("{}", year == null ? LocalDate.now().getYear() + "" : year);

    return restTemplate.execute(url, HttpMethod.GET, null, response -> {
      InputStream body = response.getBody();
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(body, StandardCharsets.UTF_8));

      // Header se muze vyskytnout uprostred dat, je potreba parsovat pri prochazeni radku
      // BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(body, StandardCharsets.UTF_8));
      // bufferedReader2.lines().forEach(l -> System.out.println(l));

      // parse header Datum|currency1|currency1|...
      // String header = bufferedReader.readLine();
      // String[] columns = header.split("\\|");
      // find header position containing USD
      // int usdIndex = findIndex(columns, "USD");

      return parseData(bufferedReader);
    });
  }

  private List<Kurz> parseData(BufferedReader bufferedReader) {
    String[][] columnsArr = new String[][] { null };
    int[] usdIndexArr = new int[] { 0 };
    return bufferedReader.lines()
      .map((line) -> {
        String[] values = line.split("\\|");
        if (values[0].equals("Datum") || values[0].equals("Date")) {
          usdIndexArr[0] = findIndex(values, "USD");
          columnsArr[0] = values;
          return null;
        } else {
          String[] columns = columnsArr[0];
          int index = usdIndexArr[0];
          try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.ENGLISH);
            LocalDate date = LocalDate.parse(values[0], formatter);
  
            NumberFormat format = NumberFormat.getInstance(Locale.of("cs"));
            Number number;
            try {
              number = format.parse(values[index]);
            } catch (ParseException e) {
              number = 0;
            }
            double rate = number.doubleValue();
  
            Kurz k = new Kurz(date, columns[index], rate);
            return k;
          } catch (Throwable e) {
            return (Kurz) null;
          }
        }
      })
      .filter(v -> v != null)
      .collect(Collectors.toList())
    ;
  }

  private int findIndex(String[] columns, String currency) {
    for (int i = 0; i < columns.length; i++) {
      if (columns[i].contains(currency)) {
        return i;
      }
    }
    return -1;
  }

  public List<Kurz> getKurzy(String year) {
    return getData(year);
  }

}
