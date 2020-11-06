---
title: SpringBoot + MyBatis + Oracle 초간단실습
tags:
  - springboot
  - mybatis
  - docker
  - oracle
categories:
  - Dev
  - Springboot
date: 2020-10-25 12:29:51
---
![](https://user-images.githubusercontent.com/33862991/97108403-a8922e00-1710-11eb-9e42-b093ed1857d8.png)

JPA를 공부하기 전에 먼저 MyBatis로 서버를 셋팅하는걸 해보고 싶었다. 지난해 학원에서 교육받을때는 실력있는 팀원이 해주셔서 내가 직접 해본 경험이 없었다. 

이 실습의 목적은 스프링부트로 만든 서버에서 MyBatis로 오라클에 쿼리를 던져서 조회하는 실습을 해볼 것이다.

DB는 **도커(Docker)** 로 **오라클** 컨테이너를 생성해서 이용할 것이며, 커맨드라인에서 **SQL PLUS** 을 통해 테이블을 생성하고, 데이터를 삽입한다.

서버는 **스프링부트(Spring Boot)** 로 생성하며, MVC 패턴으로 생성한다.

- [DB 생성하기](#docker-oracle)
- [Spring Boot 서버 생성하기](#springboot)
- [Postman으로 API 호출하기](#postman)

<br>

## <a name="docker-oracle"></a>DB 생성하기

도커 컨테이너 접속해서 SQL PLUS 실행하기

~~~bash
$ docker start oralce11g
$ docker exec -it oracle11g bash
~~~

~~~bash
root@a0ddd3dd3495: /# sqlplus
~~~

![](https://user-images.githubusercontent.com/33862991/97108778-03c52000-1713-11eb-8b4f-b5be6409780f.png)



### 테이블 생성하기

~~~sql
CREATE TABLE members(
   id 		number 				PRIMARY KEY,
   name 	varchar2(20),
   job		varchar2(20),
   loc		varchar2(30)
);
~~~

id값에 `INSERT`할 때 사용하기 위한 `SEQUENCE` 생성하기

~~~sql
CREATE SEQUENCE id_seq	-- 시퀀스 이름
INCREMENT BY 1			-- 시퀀스 증감 숫자
START WITH 1			-- 시퀀스 시작 숫자
MINVALUE 1				-- 최솟값
MAXVALUE 100			-- 최댓값
NOCYCLE;				-- 순환하지 않음
~~~



테이블에 값 추가하기

~~~sql
INSERT INTO members(id, name, jon, loc)
VALUES (id_seq.nextval, 'Sam', 'Athelete', 'Seoul');

INSERT INTO members(id, name, jon, loc)
VALUES(id_seq.nextval, 'Andy', 'Programmer', 'Kyonggi');

INSERT INTO members(id, name, jon, loc)
VALUES(id_seq.nextval, 'Nani', 'Mechanical Engineer', 'Seoul');

INSERT INTO members(id, name, jon, loc)
VALUES(id_seq.nextval, 'Gil', 'Fashion MD', 'Seoul');

INSERT INTO members(id, name, jon, loc)
VALUES(id_seq.nextval, 'Tom', 'Reporter', 'Seoul');

COMMIT;
~~~

<br>

## <a name="springboot"></a>Spring Boot 서버 생성하기

- [프로젝트 생성하기](#create-project)
- [application.properties에 Datasource 설정 추가하기](#application-properties)
- [VO 생성하기](#vo)
- [Mapper 생성하기 (Oracle 쿼리)](#xml)
- [DAO 생성하기](#dao)
- [Service 생성하기](#service)
- [Controller 생성하기](#controller)

### <a name="create-project"></a> 프로젝트 생성하기

**[Spring Start Project]** 로 생성하고, Dependency로 **Spring Web**, **Lombok**, **MyBatis Framework**, **Oracle Driver** 을 주입한다.

![](https://user-images.githubusercontent.com/33862991/97098624-38f55200-16c2-11eb-9ba4-66c6e97a7d83.png)



프로젝트의 패키지 구조는 다음과 같다.

![](https://user-images.githubusercontent.com/33862991/97098575-a2c12c00-16c1-11eb-974a-16efd0e66d60.png)

<br>

### <a name="application-properties"></a>application.properties에 Datasource 설정 추가하기 

먼저 MyBatis Mapper와 DBMS에 대한 Datasource 설정을 application.properties에서 한다. 본 파일은 `/src/main/resources`에 위치해있다.

~~~properties
# Setting for Oracle
spring.datasource.driver-class-name=oracle.jdbc.driver.OracleDriver
spring.datasource.url=jdbc:oracle:thin:@localhost:1521/xe
spring.datasource.username=[db-user-name]
spring.datasource.password=[db-user-password]

# VO location
mybatis.type-aliases-package=com.devandy.web.vo

# XML location
mybatis.mapper-locations=classpath:mappers/**/*.xml
~~~

<br>

### <a name="vo"></a>VO 생성하기

데이터를 오브젝트 형태로 담아놓을 VO를 생성한다.

경로 : `/src/main/java/com/devandy/web/vo`
파일명 : MemberVO.class

~~~java
package com.devandy.web.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MemberVO {
   @JsonProperty
   private int id;
   
   @JsonProperty
   private String name;
   
   @JsonProperty
   private String job;
   
   @JsonProperty
   private String home;
}
~~~

`Getter/Setter` 메서드를 자동으로 생성해주는 Lombok 라이브러리의 `@Data` 어노테이션을 클래스 위에 작성한다.

그리고 Postman으로 Json 형식으로 데이터를 호출할것이므로 VO 각 필드위에 `@JsonProperty` 라는 어노테이션도 추가해준다. 이 어노테이션을 붙이지 않으면, API를 호출하는 과정에서 <b><span style="color: red;">Serializable(직렬화)</span></b> 에러가 발생한다.

<br>

### <a name="xml"></a> Mapper 작성하기 (Oracle 쿼리)

DB를 조회하는 API 호출을 구현하기 위해 SQL 쿼리를 MyBatis로 작성해준다.

경로 : `/src/main/resources/mappers/member` 
파일명 : **SelectSQL.xml**

~~~xml
<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.devandy.web.dao.MemberDAO">
   <select id="selectAllMembers"
         resultType="MemberVO">
      SELECT 	ID, NAME, JOB, LOC
      FROM		MEMBERS
   </select>
</mapper>
~~~

Mapper 태그안의 `namespace` 를 통해 MyBatis가 맵핑할 이 쿼리가 어떤 DAO에서 호출될 것인지 명시한다.

Mapper 태그 내부에는 쿼리의 타입으로 `SELECT` 인지, `INSERT` 인지를 명시하고, `id`는 DAO에서 해당 쿼리를 implement하는 메서드명을 작성해준다. `resultType` 은 DAO 메서드를 통해 반환받을 타입을 작성한다. 

위의 쿼리를 단순히 MEMBERS라는 테이블을 조회하는 쿼리이므로, 반환받을 데이터는 VO 객체에 해당한다. 따라서 `resultType` 으로 VO 명을 작성한다.

정리하면, 위의 쿼리는 `MemberDAO.class` 의 `selectAllMembers()` 를 구현하는 쿼리이며, 쿼리의 결과로 VO를 반환받는 객체이다.

<br>

### <a name="dao"></a>DAO 생성하기

위에서 작성한 Mapper를 호출하는 DAO를 작성하자.

경로 : `src/main/java/com/devandy/web/dao`
파일명 : **MemberDAO.class**

~~~java
package com.devandy.web.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.devandy.web.vo.MemberVO;

@Mapper
public interface MemberDAO {
   public List<MemberVO> selectAllMembers();
}
~~~

<br>

### <a name=""></a>Service 생성하기

Service 클래스는 MVC 패턴에서 Controller에의해 호출되며, 앞서 생성한 DAO를 호출하는 클래스이다.

경로 : `src/main/java/com/devandy/web/service`
파일명 : **MemberService.class**

~~~java
package com.devandy.web.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devandy.web.dao.MemberDAO;
import com.devandy.web.vo.MemberVO;

@Service
public class MemberService {

      @Autowired
      MemberDAO memberDao;

      public List<MemberVO> selectAllMembers() {
        return memberDao.selectAllMembers();
      }
}
~~~

<br>

### <a name="controller"></a>Controller 생성하기

드디어 API를 호출하는 컨트롤러를 생성한다. 

~~~java
package com.devandy.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.devandy.web.service.MemberService;
import com.devandy.web.vo.MemberVO;

@Controller
public class APIcontroller {
	
   @Autowired
   MemberService memberService;
	
   @GetMapping("/members")
   public @ResponseBody List<MemberVO> selectListMembers() {
      List<MemberVO> allMembers = memberService.selectAllMembers();
      return allMembers;
   }
}
~~~

`/members`라는 API를 호출하는 `selectListMembers()`라는 메서드를 생성했다. 웹페이지를 반환하는게 아니라 Json 형식으로 데이터를 반환받을 것이므로 메서드의 리턴타입은 `@ResponseBody` 로 한다.

그리고 제네릭타입으로 `MemberVO` 를 갖는 List를 생성해서 List에 `memberService` 의 `selectAllMembers()` 호출 결과를 담는다.

메서드의 리턴타입으로 이 List를 받으면, Postman을 통해 Json으로 데이터를 받을 수 있을 것이다.

<br>

## <a name="postman"></a>Postman으로 API 호출하기

Postman은 API를 테스트할 수 있는 애플리케이션이다. Postman을 사용하면, 파라미터(param)나 인증(Authorization), Header 등을 변경해서 간편하게 API를 호출하는 테스트를 해볼수 있다.

![](https://user-images.githubusercontent.com/33862991/97108789-09226a80-1713-11eb-8ed3-056f0b787abf.png)

GET 메서드로 `http://localhost:8080/members` 로 API를 호출해보니 오라클에서 작성한 테이블의 데이터가 정상적으로 JSON형식으로 반환받는 것을 확인할 수 있다.