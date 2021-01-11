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
		return memberDao.selectAll();
	}

	@Override
	public void insertMember(MemberVO member) {
		memberDao.insert(member);
	}

	@Override
	public void updateMember(int id, MemberVO updateMember) {
		if(memberDao.selectById(id)!=null){
			MemberVO member = memberDao.selectById(id);
			member.setName(updateMember.getName());
			member.setJob(updateMember.getJob());
			memberDao.update(member);
		} else {
			throw new IllegalStateException("회원이 존재하지 않습니다.");
		}
	}
	@Override
	public void deleteMember(int id) {
		if(memberDao.selectById(id)!=null){
			memberDao.delete(id);
		} else {
			throw new IllegalStateException("회원이 존재하지 않습니다.");
		}
	}

}
