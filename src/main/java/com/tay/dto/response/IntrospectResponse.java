package com.tay.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IntrospectResponse {
	
	private boolean valid;

}
