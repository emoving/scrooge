package com.scrooge.scrooge.controller;

import com.scrooge.scrooge.dto.member.*;
import com.scrooge.scrooge.config.jwt.JwtTokenProvider;
import com.scrooge.scrooge.repository.member.MemberRepository;
import com.scrooge.scrooge.service.member.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "Member", description = "Member API")
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Operation(summary = "회원가입 API", description = "회원가입 POST")
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        memberService.signUp(signUpRequestDto);
        return ResponseEntity.ok("회원가입 완료");
    }

    @Operation(summary = "로그인 API", description = "로그인 POST")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequestDto) {
        String token = memberService.login(loginRequestDto);
        return ResponseEntity.ok(token);
    }
    
    // 유저 토큰을 받아서 해당 유저 정보를 반환
    @Operation(summary = "멤버정보 API", description = "멤버정보 GET")
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String tokenHeader) {
       String token = extractToken(tokenHeader);

       if (!jwtTokenProvider.validateToken(token)) {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new RuntimeException("유효하지 않은 토큰입니다."));
       }

       String email = jwtTokenProvider.extractEmail(token);
       Optional<MemberDto> memberDto = memberService.getInfo(email);
       return memberDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    private String extractToken(String header) {
        if (header != null && header.startsWith("Bearer")) {
            return header.substring(7);
        }
        return null;
    }
    
    // 주간 목표 설정
    @Operation(summary = "주간 목표를 설정하는 API")
    @PutMapping("/weekly-goal")
    public ResponseEntity<?> updateWeeklyGoal(@RequestHeader("Authorization")String header, @RequestBody UpdateWeeklyGoalDto updateWeeklyGoalDto) {
        String token = extractToken(header);

        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new RuntimeException("유효하지 않은 토큰입니다."));
        }

        Long memberId = jwtTokenProvider.extractMemberId(token);
        MemberDto memberDto = memberService.updateWeeklyGoal(updateWeeklyGoalDto, memberId);
        return ResponseEntity.ok(memberDto);
    }

    @Operation(summary = "비밀번호 변경 API", description = "비밀번호 변경 API")
    @PutMapping("/change-password")
    public ResponseEntity<?> updatePassword(@RequestHeader("Authorization")String header, @RequestBody UpdatePasswordDto updatePasswordDto) {
        String token = extractToken(header);

        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }

        Long memberId = jwtTokenProvider.extractMemberId(token);
        MemberDto memberDto = memberService.updatePassword(updatePasswordDto, memberId);
        return ResponseEntity.ok(memberDto);
    }

    @Operation(summary = "회원 탈퇴", description = "회원탈퇴 API")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteMember(@RequestHeader("Authorization")String header) {
        String token = jwtTokenProvider.extractToken(header);
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }

        memberService.deleteMember(jwtTokenProvider.extractMemberId(token));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("회원탈퇴 완료");
    }
}
