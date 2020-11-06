package com.devandy.web.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.devandy.web.vo.MemberVO;

@Mapper
public interface MemberDao {
	public List<MemberVO> SelectAllMembers();
}
