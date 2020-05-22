package io.project.coronavirustracker.controller;

import io.project.coronavirustracker.models.LocationStats;
import io.project.coronavirustracker.services.CoronaVirusDataService;
import io.project.coronavirustracker.services.VirusDataClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Stream;

@RefreshScope
@Controller
public class HomeController {

  @Autowired VirusDataClient virusDataClient;

  @RequestMapping("/getdata")
  public String getData() {
    return virusDataClient.getData();
  }

  @Autowired CoronaVirusDataService dataService;

  @GetMapping("/")
  public String home(Model model) {

    List<LocationStats> allStats = dataService.getAllStats();
    int totalReportedCases = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
    int totalNewCases = allStats.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();
    model.addAttribute("totalReportedCases", totalReportedCases);
    model.addAttribute("locationStats", allStats);
    model.addAttribute("totalNewCases", totalNewCases);

    return "home";
  }

  @GetMapping("/get")
  public Stream<Integer> list() {

    List<LocationStats> allStats = dataService.getAllStats();

    Stream<Integer> integerStream = allStats.stream().map(stat -> stat.getLatestTotalCases());

    return integerStream;
  }
}
