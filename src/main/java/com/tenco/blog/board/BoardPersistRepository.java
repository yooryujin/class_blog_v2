package com.tenco.blog.board;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor // 필수 멤버변수를 확인해서 생성자에 등록해줌
@Repository // IoC 대상이 된다 - 싱글톤 패턴으로 관리 됨
public class BoardPersistRepository {

    // JPA 핵심 인터페이스
    // @Autowired final 을 사용시 사용 불가
    private final EntityManager em;

    // JPQL 을 사용한 게시글 목록 조회
    public List<Board> findAll() {
        // JPQL : 엔티티 객체를 대상으로 하는 객체지향 쿼리
        // Board 는 엔티티 클래스명, b는 별칭
        String jpql = "select b from Board b order by b.id DESC";

        // em.createNativeQuery() v1- 버전
        // Query query = em.createQuery(jpql,Board.class);
        // List<Board> boardList = query.getResultList();
        // return boardList;
        return em.createQuery(jpql,Board.class).getResultList();
    }

    // 게시글 저장 기능 - 영속성 컨텍스트 활용
    @Transactional
    public Board save(Board board) {
        // v1 -> 네이티브 쿼리를 활용 했음

        // 1. 매개변수로 받은 board 는 현재 비영속 상태이다
        // 아직 영속성 컨텍스트에 관리 되지 않는 상태
        // 데이터베이스와 아직은 연관 없는 순수 Java 객체 상태

        // 2. em.persist(board); - 이 엔티티를 영속성 컨텍스트에 저장하는 개념이다
        // 영속성 컨텍스트가 board 객체를 관리하게 됨
        em.persist(board);

        // 3. 트랜잭션 커밋 시점 Insert 쿼리가 내부적으로 실행 됨
        // 영속성 컨텍스트의 변경 사항이 자동으로 DB 에 반영 됨
        // board 객체의 id 필드에 자동으로 생성된 값이 설정 됨
        // insert --> DB --> (pk 값을 알 수 있다)
        // select --> DB --> (할당된 pk 값 조회)

        // 4. 영속 상태로 된 board 객체를 반환
        // 이 시점에는 자동으로 board id 멤버 변수에 DB pk 값이 할당된 상태이다

        return board;
    }

}
