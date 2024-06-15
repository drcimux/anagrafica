package com.neo.test.anagrafica.dto;

import java.util.List;

public record User(String username, String password, List<String> roles) {
}