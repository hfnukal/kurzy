package com.realpad.example.kurzy;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class KurzyController {
  private KurzyService kurzyService;

  KurzyController (KurzyService kurzyService) {
    this.kurzyService = kurzyService;
  }

  @GetMapping("/kurzy/{year}")
  public List<Kurz> kurzy(@PathVariable(value="year") String year) {
    return kurzyService.getKurzy(year);
  }
}
