package com.devandy.web.controller;

import com.devandy.web.service.MemberServiceImpl;
import com.devandy.web.vo.MemberVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MemberController {
	
	@Autowired
	MemberServiceImpl memberService;
	
	@GetMapping("/member/all")
	public List<MemberVO> selectAllMembers(){
		return memberService.selectAllMembers();
	}
	
	@PostMapping("/member/new")
	public List<MemberVO> insertMember(@RequestBody MemberVO member){
		memberService.insertMember(member);
		return memberService.selectAllMembers();
	}

	@PutMapping("/member/{id}")
	public List<MemberVO> updateMember(@PathVariable int id, @RequestBody MemberVO member){
		memberService.updateMember(id, member);
		return memberService.selectAllMembers();
	}

	@DeleteMapping("/member/{id}")
	public List<MemberVO> deleteMember(@PathVariable int id){
		memberService.deleteMember(id);
		return memberService.selectAllMembers();
	}
}
