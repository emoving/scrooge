package com.scrooge.scrooge.service.community;

import com.scrooge.scrooge.config.FileUploadProperties;
import com.scrooge.scrooge.domain.community.Article;
import com.scrooge.scrooge.domain.community.ArticleBad;
import com.scrooge.scrooge.domain.community.ArticleGood;
import com.scrooge.scrooge.dto.communityDto.ArticleBadDto;
import com.scrooge.scrooge.dto.communityDto.ArticleDto;
import com.scrooge.scrooge.dto.communityDto.ArticleGoodDto;
import com.scrooge.scrooge.repository.UserRepository;
import com.scrooge.scrooge.repository.community.ArticleBadRepository;
import com.scrooge.scrooge.repository.community.ArticleGoodRepository;
import com.scrooge.scrooge.repository.community.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;

    private final FileUploadProperties fileUploadProperties;

    // 커뮤니티 글을 등록하는 메서드
    @Transactional
    public void createArticle(ArticleDto articleDto, MultipartFile img) {

        Article article = new Article();

        article.setTitle(articleDto.getTitle());
        article.setContent(articleDto.getContent());
        article.setUser(userRepository.findById(articleDto.getUserId()).orElse(null));

        // 이미지 파일 등록 구현

        // 업로드할 위치 설정
        String uploadLocation = fileUploadProperties.getUploadLocation();

        // 업로드된 사진의 파일명을 랜덤 UUID로 생성
        String fileName = UUID.randomUUID().toString() + "_" + img.getOriginalFilename();

        Path filePath = null;

        try {
            // 업로드할 위치에 파일 저장
            byte[] bytes = img.getBytes();
            filePath = Paths.get(uploadLocation + File.separator + fileName);
            Files.write(filePath, bytes);
        } catch (IOException e) { // 파일 저장 중 예외 처리
            e.printStackTrace();
        }

        article.setImgAdress(filePath.toString());

        articleRepository.save(article); // DB에 article 저장

    }
}
