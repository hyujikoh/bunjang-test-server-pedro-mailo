package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class User {
    private int Idx;
    private String phoneNumber;
    private String userBirth;
    private String userName;
    private String userPwd;
    private String shopName;

}
