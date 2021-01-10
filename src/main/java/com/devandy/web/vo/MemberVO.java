package com.devandy.web.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MemberVO{
	@JsonProperty(value = "id")
	private int id;
	@JsonProperty(value = "name")
	private String name;
	@JsonProperty(value = "job")
	private String job;
}
