package com.example.music.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class GoogleUserInfo {

    private String email;
    private String name;
    private String picture;

    public GoogleUserInfo(Map<String, Object> attributes) {
        this.email = (String) attributes.get("email");
        this.name = (String) attributes.get("name");
        this.picture = (String) attributes.get("picture");
    }
}
