package io.project.coronavirustracker.services;

import io.project.coronavirustracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@RefreshScope
@Service
public class CoronaVirusDataService {

  @Autowired VirusDataClient virusDataClient;

  private List<LocationStats> allStats = new ArrayList<>();

  public List<LocationStats> getAllStats() {
    return allStats;
  }

  @PostConstruct
  @Scheduled(cron = "* * 23 * * *")
  public void fetchVirusData() throws IOException {

    List<LocationStats> newStats = new ArrayList<>();

    String virusData = virusDataClient.getData();

    StringReader csvBodyReader = new StringReader(virusData);

    Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);

    for (CSVRecord record : records) {

      int latestCases = Integer.parseInt(record.get(record.size() - 1));
      int prevDayCases = Integer.parseInt(record.get(record.size() - 2));

      LocationStats locationStat = new LocationStats();
      locationStat.setState(record.get("Province/State"));
      locationStat.setCountry(record.get("Country/Region"));
      locationStat.setLatestTotalCases(latestCases);
      locationStat.setDiffFromPrevDay(latestCases - prevDayCases);

      newStats.add(locationStat);
    }
    this.allStats = newStats;
  }
}
