package com.devandy.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devandy.web.dao.MemberDao;
import com.devandy.web.vo.MemberVO;

@Service
public class MemberServiceImpl implements MemberService{
	
	@Autowired
	private MemberDao memberDao;
	
	@Override
	public List<MemberVO> selectAllMembers() {
		return memberDao.SelectAllMembers();
	}
}
