package com.example.peaceful_land.Service;

import com.example.peaceful_land.DTO.RegisterRequest;
import com.example.peaceful_land.Entity.Account;

public interface IAccountService {
    String tryLogin(String userId, String password);
    Account register(RegisterRequest userInfo);
}
