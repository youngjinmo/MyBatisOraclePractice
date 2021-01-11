package com.devandy.web.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberVO{
	@JsonProperty(value = "id")
	private int id;
	@JsonProperty(value = "name")
	private String name;
	@JsonProperty(value = "job")
	private String job;
}
