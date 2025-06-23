package com.tenco.blog.board;



import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@RequiredArgsConstructor
@Controller // IoC 대상 - 싱글톤 패턴으로 관리 됨
public class BoardController {

    // 생성자 의존 주의 - DI 처리
    private final BoardPersistRepository br;

    // 게시글 상세 보기
    // 주소 설계 GET : http://localhost:8080/board/3
    @GetMapping("/board/{id}")
    public String detail(@PathVariable(name = "id") Long id, HttpServletRequest request) {
        Board board = br.findById(id);
        request.setAttribute("board", board);
        // prefix : classpath:/templates/
        // return : board/detail
        // suffix : .mustache

        // 1차 캐시 효과 - DB 에 접근하지 않고 바로 영속성 컨텍스트에서 꺼냄
        // br.findById(id);
        return "board/detail";
    }


    // 1. index.mustache 파일을 반환 시키는 기능을 들어보자
    // 화면에 리스트를 뿌려주자
    // 주소 설계 : http://localhost:8080/ , http://localhost:8080/index
    @GetMapping({"/", "/index"})
    public String BoardList(HttpServletRequest request) {
        List<Board> boardList = br.findAll();
        request.setAttribute("boardList", boardList);

        return "index";
    }

    // 게시글 작성 화면 요청 처리
    @GetMapping("/board/save-form")
    public String saveForm() {
        return "board/save-form";
    }

    // 게시글 작성 액션(수행) 처리
    @PostMapping("/board/save")
    public String save(BoardRequest.SaveDTO reqDTO) {
        // HTTP 요청 본문 메세지 : title = 값&content=값&username=값
        // form MIME ( application/x-ww-form-urlencoded)

        // reqDTO <-- 사용자가 던진 데이터 상태가 있음
        // DTO -- Board -- DB 에 넣어야함
        // Board board = new Board(reqDTO.getTitle(),reqDTO.getContent(),reqDTO.getUsername());
        Board board = reqDTO.toEntity();
        br.save(board);
        return "redirect:/";
    }

    // 게시글 수정하기 화면
//    @GetMapping("/board/{id}/update-form")
//    public String update(@PathVariable Long id, Model model) {
//        Board board = br.findById(id);
//        model.addAttribute("board",board);
//        return "board/update-form";
//    }

    @PostMapping("/board/{id}/update-form")
    public String correction(@PathVariable Long id, String title, String content, String username) {
        br.correctionById_Entity(id,title,content,username);
        return "redirect:/board/" + id;
    }

}
