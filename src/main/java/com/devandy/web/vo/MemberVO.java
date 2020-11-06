package com.devandy.web.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MemberVO{
	@JsonProperty
	private int id;
	@JsonProperty
	private String name;
	@JsonProperty
	private String job;
	@JsonProperty
	private String home;
}
