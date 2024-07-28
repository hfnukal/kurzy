package com.realpad.example.kurzy;

import java.time.LocalDate;

public record Kurz (LocalDate date, String currency, Double rate) {
}
