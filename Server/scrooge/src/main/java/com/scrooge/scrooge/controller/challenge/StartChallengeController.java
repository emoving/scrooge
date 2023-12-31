package com.scrooge.scrooge.controller.challenge;

import com.scrooge.scrooge.config.jwt.JwtTokenProvider;
import com.scrooge.scrooge.dto.challengeDto.*;
import com.scrooge.scrooge.repository.challenge.ChallengeRepository;
import com.scrooge.scrooge.service.challenge.StartChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;

@Tag(name = "ChallengeStart", description = "시작된 챌린지에 관련된 API")
@RestController
@RequestMapping("/api/challenge")
@RequiredArgsConstructor
public class StartChallengeController {

    private final StartChallengeService startChallengeService;

    private final JwtTokenProvider jwtTokenProvider;
    private final ChallengeRepository challengeRepository;

    // 사용자가 참여한 시작된 챌린지에 대한 정보 조회
    @Operation(summary = "사용자가 참여한 시작된 챌린지에 대한 정보 조회 API")
    @GetMapping("/{challengeId}/started")
    public ResponseEntity<?> getMyStartedChallenge(@PathVariable("challengeId") Long challengeId) {
        if (!challengeRepository.existsByIdAndStatus(challengeId, 2)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("시작하지 않은 챌린지입니다.");
        }

        return ResponseEntity.ok(startChallengeService.getStartedChallenge(challengeId));
    }

    // 사용자 인증 등록 API
    @Operation(summary = "사용자가 인증을 등록하는 API")
    @PostMapping(value = "/{challengeId}/auth", consumes = "multipart/form-data")
    public ResponseEntity<ChallengeStartRespDto> createMyChallengeAuth(@RequestHeader("Authorization") String tokenHeader, @PathVariable("challengeId") Long challengeId,
                                                                       @RequestParam MultipartFile img) throws IOException {
        ChallengeStartRespDto challengeStartRespDto = new ChallengeStartRespDto();

        String token = extractToken(tokenHeader);

        if (!jwtTokenProvider.validateToken(token)) {
            challengeStartRespDto.setStatus("Failed");
            challengeStartRespDto.setMessage("유효하지 않은 토큰입니다.");
            return ResponseEntity.ok(challengeStartRespDto);
        }

        challengeStartRespDto = startChallengeService.createMyChallengeAuth(challengeId, jwtTokenProvider.extractMemberId(token), img);
        return ResponseEntity.ok(challengeStartRespDto);
    }

    // 사용자 인증 현황 조회 API
    @Operation(summary = "사용자 인증 현황 조회 API")
    @GetMapping("/{challengeId}/my-challenge/my-auth")
    public ResponseEntity<MyChallengeMyAuthDto> getMyChallengeMyAuth(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable("challengeId") Long challengeId) {
        String token = extractToken(tokenHeader);
        Long memberId = jwtTokenProvider.extractMemberId(token);

        MyChallengeMyAuthDto myChallengeMyAuthDto = startChallengeService.getMyChallengeMyAuth(challengeId, memberId);
        return ResponseEntity.ok(myChallengeMyAuthDto);
    }

    @Operation(summary = "우리팀 인증 현황 조회")
    @GetMapping("/{challengeId}/team-auth")
    public ResponseEntity<?> getTeamAuths(@RequestHeader("Authorization")String header, @PathVariable("challengeId")Long challengeId) {
        String token = jwtTokenProvider.extractToken(header);
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }

        return ResponseEntity.ok(startChallengeService.getTeamAuths(challengeId, jwtTokenProvider.extractMemberId(token)));
    }

    @Operation(summary = "종료된 챌린지 정보 조회")
    @GetMapping("/end-challenge")
    public ResponseEntity<?> getMyEndChallenges(@RequestHeader("Authorization") String header) {
        String token = jwtTokenProvider.extractToken(header);
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }

        Long memberId = jwtTokenProvider.extractMemberId(token);

        List<ChallengeEndResDto> challengeEndResDtoList = startChallengeService.getMyEndChallenges(memberId);
        return ResponseEntity.ok(challengeEndResDtoList);
    }

    private String extractToken (String tokenHeader){
        if (tokenHeader != null && tokenHeader.startsWith("Bearer")) {
            return tokenHeader.substring(7);
        }
        return null;
    }
}