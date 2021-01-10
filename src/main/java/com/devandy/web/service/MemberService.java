package com.devandy.web.service;

import java.util.List;

import com.devandy.web.vo.MemberVO;

public interface MemberService {

	List<MemberVO> selectAllMembers();

	void insertMember(MemberVO member);

	void updateMember(int id, MemberVO member);

	void deleteMember(int id);
	
}
