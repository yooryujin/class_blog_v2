package com.tenco.blog.board;

import com.tenco.blog.model.Board;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller // IoC 대상 - 싱글톤 패턴으로 관리 됨
public class BoardController {

    @Autowired
    private BoardNativeRepository boardNativeRepository;

    @GetMapping("/board/{id}")
    public String detail(@PathVariable(name = "id") Long id,
                         HttpServletRequest request) {
        Board board = boardNativeRepository.findById(id);
        request.setAttribute("board", board);

        return "board/detail";
    }

    // DI 의존성 주입 : 스프링이 자동으로 객체를 주입
    public BoardController(BoardNativeRepository boardNativeRepository) {
        this.boardNativeRepository = boardNativeRepository;
    }

    @PostMapping("/board/{id}/update-form")
    public String update(@PathVariable(name = "id") Long id,
                         @RequestParam(name = "title") String title,
                         @RequestParam(name = "content") String content,
                         @RequestParam(name = "username") String username,
                         HttpServletRequest request) {

        boardNativeRepository.updateById(id,title,content,username);

        // PRG 패턴 적용
        // 수정 완료 후 해당 게시글 상세보기 페이지로 리다이렉트
        // 게시글 상세보기 URL --> /board/{id}/update-form
        return "redirect:/board/" + id;
    }

    // 게시글 수정 화면 요청 GET 방식
    // /board/3/update-form
    @GetMapping("/board/{id}/update-form")
    public String updateForm(@PathVariable(name = "id") Long id, HttpServletRequest request) {
        Board board = boardNativeRepository.findById(id);
        request.setAttribute("board", board);

        return "board/update-form";
    }

    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable(name = "id") Long id) {
        // 클라이언트 --> 삭제요청 처리 --> 응답 : redirect 처리 --> 클라이언트 / 경로 요청 --> 응답

        boardNativeRepository.deleteById(id);
        // PRG 패턴 (Post-Redirect-Get) 적용
        // 삭제 후 메인 페이지로 리다이렉트 하여 중복 상제 방지
        // 새로 고침을 해도 삭제가 다시 실행 되지 않음
        return "redirect:/";
    }

    // username, title, content <--- DTO 를 설계해서 받는 방법, 기본 데이터 타입 설정해서 받는 방법
    // form 태그에서 넘어오는 데이터 받기
    // form 태그의 name 속성에 key 값 동일 해야 함
    // http://localhost:8080/board/save
    // 스프링 부트 기본 파싱 전략 - key = value (form)
    @PostMapping("/board/save")
    public String save(@RequestParam("username") String username,
                       @RequestParam("title") String title,
                       @RequestParam("content") String content) {
        System.out.println("title : " + title);
        System.out.println("content : " + content);
        System.out.println("username : " + username);

        boardNativeRepository.save(title, content, username);

        return "redirect:/";
    }


    @GetMapping({"/", "/index"})
    public String index(HttpServletRequest request) {
        // DB 에 접근해서 select 결과값을 받아서 mustache 파일에 데이터 바인딩 처리
        List<Board> boardList = boardNativeRepository.findAll();
        // 뷰에 데이터를 전달 -> Model 사용 가능 public String index(Model model)
        request.setAttribute("boardList", boardList);

        return "index";
    }

    @GetMapping("/board/save-form")
    public String saveForm() {
        // templates / board
        // templates / board /

        return "board/save-form";
    }
    // board/1


}
