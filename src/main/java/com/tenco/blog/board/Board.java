package com.tenco.blog.board;


import com.tenco.blog.utils.MyDateUtil;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@NoArgsConstructor // 기본 생성자 생성 어노테이션 - JPA 에서 엔티티는 기본 생성자가 필요
@Data
// @Table : 실제 데이터베이스 테이블 명을 지정할 때 사용
@Table(name = "board_tb")
// @Entity : JPA  가 이 클래스를 데이터베이스 테이블과 매핑하는 객체(엔티티)로 인식 한다
// 즉, @Entity 어노테이션이 있어야 JPA 가 이 객체를 관리 한다
@Entity
public class Board {

    // @Id 이 필드가 기본키(Primary key) 임을 나타냄
    @Id
    // IDENTITY 전략 : 데이터베이스의 기본 전략을 사용한다. -> Auto_Increment
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 별도 annotation 이 없으면 필드명이 컬럼명이 됨
    private String title;
    private String content;
    private String username;
    // CreationTimestamp : 하이버네이트가 제공하는 어노테이션
    // 엔티티가 처음 저장할 때 현재 시간을 자동으로 설정한다
    // pc --> DB (날짜 주입)
    // v1 에서는 SQL now()를 직접 사용했지만 JPA 가 자동 처리
    @CreationTimestamp
    private Timestamp createdAt;  // created_at ( 스네이크 케이스로 자동 변환 )

    // 생성자 만들어 주기
    public Board(String title, String content, String username) {
        this.title = title;
        this.content = content;
        this.username = username;
        // id 와 createAt은 JPA/Hibernate 가 자동으로 설정
    }

    // mustache 에서 표현 할 시간을 포맷 기능을(행위) 스스로 만들자
    public String getTime() {
        return MyDateUtil.timestampFormat(createdAt);
    }

}
