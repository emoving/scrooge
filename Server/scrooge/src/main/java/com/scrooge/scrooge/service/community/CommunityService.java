package com.scrooge.scrooge.service.community;

import com.scrooge.scrooge.config.FileUploadProperties;
import com.scrooge.scrooge.domain.community.Article;
import com.scrooge.scrooge.dto.communityDto.ArticleDto;
import com.scrooge.scrooge.repository.community.ArticleRepository;
import com.scrooge.scrooge.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {

    
    private final MemberRepository memberRepository;
    private final ArticleRepository articleRepository;

    private final FileUploadProperties fileUploadProperties;

    // 커뮤니티 글을 등록하는 메서드
    @Transactional
    public void createArticle(ArticleDto articleDto, MultipartFile img) {

        Article article = new Article();

        article.setContent(articleDto.getContent());
        article.setMember(memberRepository.findById(articleDto.getMemberId()).orElse(null));

        // 이미지 파일 등록 구현

        // 업로드할 위치 설정
        String uploadLocation = fileUploadProperties.getUploadLocation() + "/community";

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

    // 커뮤니티 전체 글을 조회하는 API
    public List<ArticleDto> getAllCommunityArticles() {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Article> articles = articleRepository.findAll(sort);
        return articles.stream()
                .map(article -> {
                    ArticleDto articleDto = new ArticleDto();
                    articleDto.setId(article.getId());
                    articleDto.setContent(article.getContent());
                    articleDto.setImgAdress(article.getImgAdress());
                    articleDto.setCreatedAt(article.getCreatedAt()); //필요X?

                    // user 관련 정보
                    articleDto.setMemberId(article.getMember().getId()); //필요X?
                    articleDto.setNickname(article.getMember().getNickname());
                    articleDto.setAvatarImgAddress(article.getMember().getMainAvatar().getImgAddress());

                    return articleDto;
                })
                .collect(Collectors.toList());
    }

    // 커뮤니티 글을 상세 조회하는 API
    public ArticleDto getCommunityArticle(Long articleId) throws IllegalAccessException {
        Optional<Article> article = articleRepository.findById(articleId);

        if(article.isPresent()) {
            ArticleDto articleDto = new ArticleDto();
            articleDto.setId(article.get().getId());
            articleDto.setContent(article.get().getContent());
            articleDto.setImgAdress(article.get().getImgAdress());
            articleDto.setCreatedAt(article.get().getCreatedAt());

            // user 관련 정보
            articleDto.setMemberId(article.get().getMember().getId()); // 필요X?
            articleDto.setNickname(article.get().getMember().getNickname());
            articleDto.setAvatarImgAddress(article.get().getMember().getMainAvatar().getImgAddress());

            return articleDto;
        }
        else {
            throw new IllegalAccessException("Article not found with ID: " + articleId);
        }

    }

    // 커뮤니티 글을 수정하는 API
    public void updateCommunityArticle(ArticleDto articleDto) {
        Optional<Article> article = articleRepository.findById(articleDto.getId());

        // 변경사항 반영 => 내용만 수정 가능
        if(article.isPresent()){
            article.get().setContent(articleDto.getContent());
            articleRepository.save(article.get());
        } else {
            throw new NotFoundException("해당 글이 존재하지 않습니다.");
        }

    }

    // 커뮤니티 글을 삭제하는 API
    public void deleteCommunityArticle(Long articleId) {
        Optional<Article> article = articleRepository.findById(articleId);

        if(article.isPresent()) {
            Article article1 = article.get();
            articleRepository.delete(article1);
        } else {
            throw new NotFoundException("해당 글이 존재하지 않습니다.");
        }
    }


}
