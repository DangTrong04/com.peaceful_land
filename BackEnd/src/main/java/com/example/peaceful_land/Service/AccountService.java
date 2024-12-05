package com.example.peaceful_land.Service;

import com.example.peaceful_land.DTO.RegisterRequest;
import com.example.peaceful_land.Entity.Account;
import com.example.peaceful_land.Entity.UserInterest;
import com.example.peaceful_land.Entity.UserUninterest;
import com.example.peaceful_land.Repository.AccountRepository;
import com.example.peaceful_land.Repository.UserInterestRepository;
import com.example.peaceful_land.Repository.UserUninterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class AccountService implements IAccountService {

    private final AccountRepository accountRepository;
    private final UserInterestRepository userInterestRepository;
    private final UserUninterestRepository userUninterestRepository;
    private final EmailService emailService;
    private final RedisService redisService;

    @Override
    public String tryLogin(String userId, String password) {
        // userId có thể là email hoặc số điện thoại
        // Xử lý ngoại lệ

        // Xử lý và trả về Token nếu hợp lệ
        return "token";
    }

    @Override
    public Account register(RegisterRequest userInfo) {
        if (accountRepository.existsByEmail(userInfo.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }
        else if (accountRepository.existsByPhone(userInfo.getPhone())) {
            throw new RuntimeException("Số điện thoại đã tồn tại");
        }

        Account account = accountRepository.save(
                Account.builder()
                        .role((byte) 0)
                        .email(userInfo.getEmail())
                        .password(userInfo.getPassword())   // TODO: Mã hóa mật khẩu
                        .accountBalance(0)
                        .name(userInfo.getName())
                        .birthDate(userInfo.getBirthDate())
                        .phone(userInfo.getPhone())
                        .status(true)
                        .avatar(1L)
                        .build()
        );

        // Tạo thông tin quan tâm và không quan tâm mặc định
        userInterestRepository.save(
                UserInterest.builder()
                        .user(account)
                        .propertyListId("")
                        .count(0)
                        .build()
        );
        userUninterestRepository.save(
                UserUninterest.builder()
                        .user(account)
                        .propertyListId("")
                        .count(0)
                        .build()
        );
        return account;
    }

    @Override
    public void forgotPassword(String email) {
        if (!accountRepository.existsByEmail(email)) {
            throw new RuntimeException("Email không tồn tại");
        }
        // Tạo mã OTP có 6 ksy tự
        String otp = String.valueOf((int) (Math.random() * 900000 + 100000));
        // Lưu mã OTP vào Redis với email là key, thời gian sống là 5 phút
        redisService.storeOtp(email, otp, 300);
        // Gửi email chứa mã OTP
        emailService.sendForgotPassVerifyEmail(email, otp);
    }

    @Override
    public void verifyOtp(String email, String otp) {
        if (!accountRepository.existsByEmail(email)) {
            throw new RuntimeException("Email không hợp lệ");
        }
        if (!redisService.verifyOtp(email, otp)) {
            throw new RuntimeException("Mã OTP không hợp lệ hoặc đã hết hạn");
        }
    }

    @Override
    public void resetPassword(String email, String newPassword) {
        if (!accountRepository.existsByEmail(email)) {
            throw new RuntimeException("Email không hợp lệ");
        }
        accountRepository.findByEmail(email).ifPresent(account -> {
            account.setPassword(newPassword); // TODO: Mã hóa mật khẩu
            accountRepository.save(account);
        });
    }
}