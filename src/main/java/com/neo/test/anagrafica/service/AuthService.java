package com.neo.test.anagrafica.service;

import java.util.Map;

import com.neo.test.anagrafica.dto.AuthRequestDto;

public interface AuthService {
     Map<String, String> authRequest(AuthRequestDto authRequestDto);

}
