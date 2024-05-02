package com.dreamgames.backendengineeringcasestudy.controller;


import com.dreamgames.backendengineeringcasestudy.model.leaderboard.CountryLeaderBoard;
import com.dreamgames.backendengineeringcasestudy.model.leaderboard.GroupLeaderBoard;
import com.dreamgames.backendengineeringcasestudy.service.LeaderBoardService;
import com.dreamgames.backendengineeringcasestudy.service.RedisService;
import com.dreamgames.backendengineeringcasestudy.service.TournamentService;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = TournamentController.class)
@AutoConfigureMockMvc
public class TournamentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TournamentService tournamentService;

    @MockBean
    private RedisService redisService;

    @MockBean
    private LeaderBoardService leaderBoardService;

    @Test
    public void testEnterTournament() throws Exception {
        Long userId = 1L;
        List<GroupLeaderBoard> mockLeaderBoard = new ArrayList<>();
        mockLeaderBoard.add(new GroupLeaderBoard(userId, "testUser", "USA", 100));

        when(tournamentService.enterTournament(userId)).thenReturn(mockLeaderBoard);

        mockMvc.perform(post("/enter/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(userId))
                .andExpect(jsonPath("$[0].username").value("testUser"))
                .andExpect(jsonPath("$[0].country").value("USA"))
                .andExpect(jsonPath("$[0].score").value(100));

        verify(tournamentService).enterTournament(userId);
    }

    @Test
    public void testGetGroupLeaderBoard() throws Exception {
        Long userId = 1L;
        List<GroupLeaderBoard> mockLeaderBoard = new ArrayList<>();
        mockLeaderBoard.add(new GroupLeaderBoard(userId, "testUser", "USA", 100));

        when(leaderBoardService.getGroupLeaderBoardWithUserId(userId)).thenReturn(mockLeaderBoard);

        mockMvc.perform(get("/group-leaderboard/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(userId))
                .andExpect(jsonPath("$[0].username").value("testUser"))
                .andExpect(jsonPath("$[0].country").value("USA"))
                .andExpect(jsonPath("$[0].score").value(100));

        verify(leaderBoardService).getGroupLeaderBoardWithUserId(userId);
    }

    @Test
    public void testGetCountryLeaderBoard() throws Exception {
        List<CountryLeaderBoard> mockLeaderBoard = new ArrayList<>();
        mockLeaderBoard.add(new CountryLeaderBoard("USA", 150));

        when(leaderBoardService.getCountryLeaderBoardCurrentTournament()).thenReturn(mockLeaderBoard);

        mockMvc.perform(get("/country-leaderboard/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].country").value("USA"))
                .andExpect(jsonPath("$[0].score").value(150));

        verify(leaderBoardService).getCountryLeaderBoardCurrentTournament();
    }

    @Test
    public void testGetUserTournamentGroupRank() throws Exception {
        Long tournamentId = 1L;
        Long userId = 1L;
        Integer expectedRank = 2;

        when(leaderBoardService.getUserTournamentGroupRank(tournamentId, userId)).thenReturn(expectedRank);

        mockMvc.perform(get("/rank/" + tournamentId + "/" + userId + "/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedRank.toString()));

        verify(leaderBoardService).getUserTournamentGroupRank(tournamentId, userId);
    }

    @Test
    public void testClaimTournamentsReward() throws Exception {
        Long userId = 1L;

        doNothing().when(tournamentService).claimTournamentsReward(userId);

        mockMvc.perform(post("/claim-rewards/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(tournamentService).claimTournamentsReward(userId);
    }
}