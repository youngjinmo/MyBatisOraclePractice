package com.devandy.web.service;

import java.util.List;

import com.devandy.web.vo.MemberVO;

public interface MemberService {

	public List<MemberVO> selectAllMembers();
	
}
