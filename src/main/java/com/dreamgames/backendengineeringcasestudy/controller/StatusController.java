package com.dreamgames.backendengineeringcasestudy.controller;

import com.dreamgames.backendengineeringcasestudy.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// It is implemented to check server status. Don't modify this class.

@RestController
@RequiredArgsConstructor
public class StatusController {
    private final TournamentService tournamentService;

    @GetMapping("/status")
    public String status() {
        return "Server is up!";
    }
}
