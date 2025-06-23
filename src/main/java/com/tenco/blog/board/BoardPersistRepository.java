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

    // 게시글 삭제하기 ( 영속성 컨텍스트를 활용 )
    @Transactional
    public void deleteById(Long id) {
        // 1. 먼저 삭제 할 엔티티를 영속 상태로 조회
        Board board = em.find(Board.class, id);

        // 영속 상태의 엔티티를 삭제 상태로 변경
        em.remove(board);
        // 트랜잭션이 커밋 되는 순간 삭제 처리
        // 삭제 과정
        // 1. Board 엔티티가 영속 상태에서 remove() 호출 시 삭제 상태로 변경
        // 2. 1차 캐시에서 해당 엔티티를 제거
        // 3. 트랜잭션 커밋 시점에 DELETE SQL 자동 실행
        // 4. 연관관계 처리 자동 수행 (cascade 설정 시)


    }

    // 게시글 수정하기 (DB 접근 계층)
    public void update(Long boardId ,BoardRequest.UpdateDTO updateDTO) {
        Board board = findById(boardId);
        // board -> 영속성 컨텍스트 1차 캐시에 key = value 값이 저장 되어 있다
        board.setTitle(updateDTO.getTitle());
        board.setContent(updateDTO.getContent());
        board.setUsername(updateDTO.getUsername());

        // 트랜잭션 끝나면 영속성 컨텍스트에서 변경 감지를 한다
        // 변경 감지(Dirty Checking)
        // 1. 영속성 컨텍스트가 엔티티 최초 상태를 스냅샷으로 보관
        // 2. 필드 값 변경 시 현재 상태와 스냅샷 비교
        // 3. 트랜잭션 커밋 시점에 변경된 필드만 UPDATE 쿼리를 자동 생성
        // 4. Update board_tb set title = ? , content = ?, username = ? where id = ?
    }

    // 게시글 한건 조회 쿼리 만들기
    // em.find(), JPQL, 네이티브 쿼리
    public Board findById(Long id) {
        // 1차 캐시 활용
        return em.find(Board.class,id);
    }

    // JPQL 을 사용한 조회 방법 (비교형 - 실제로는 find() 권장)
    public Board findByIdWithJPQL(Long id) {
        // 네임드 파라미터 권장 사용
        String jpql = "SELECT b FROM Board b WHERE b.id = :id ";
        Query query = em.createNativeQuery(jpql,Board.class);
        query.setParameter("id",id);
        Board board = (Board) query.getSingleResult();
        try {
            return em.createQuery(jpql,Board.class).setParameter("id",id).getSingleResult();
            // 주의점 : 결과 없으면 NoResultException 발생
        } catch (Exception e) {
            return null;
        }
        // JPQL 단점 :
        // 1. 1차 캐시 우회하여 항상 DB 접근
        // 2. 코드가 복잡하게 나올 수 있음
        // 3. getSingleResult() 호출 <-- 예외 처리 해주어야 함

    }


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

     // 게시글 수정 correction 쿼리
    @Transactional
    public void correctionById(Long id, String title, String content, String username) {
        String sqlStr = "update board_tb SET title = ?, content = ?, username = ? where id = ? ";
        Query query = em.createNativeQuery(sqlStr);
        query.setParameter(1,title);
        query.setParameter(2,content);
        query.setParameter(3,username);
        query.setParameter(4,id);

        int correctionRows = query.executeUpdate();
        System.out.println("수정하기 : " + correctionRows);
    }
    @Transactional
    public void correctionById_Entity(Long id, String title, String content, String username) {
        Board board = em.find(Board.class,id);

    }

}
