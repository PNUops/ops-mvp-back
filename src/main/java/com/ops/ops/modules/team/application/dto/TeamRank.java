package com.ops.ops.modules.team.application.dto;

public record TeamRank(
	Long teamId,
	String teamName, 
	String projectName, 
	int likeCount,
	Integer displayOrder,
	String awardTitle,
	String awardBadgeColor,
	String awardBadgeSize
) {}

