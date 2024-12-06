package com.example.peaceful_land.Service;

import com.example.peaceful_land.DTO.*;
import com.example.peaceful_land.Entity.Account;
import com.example.peaceful_land.Entity.Purchase;
import com.example.peaceful_land.Repository.AccountRepository;
import com.example.peaceful_land.Repository.PurchaseRepository;
import com.example.peaceful_land.Utils.ImageUtils;
import com.example.peaceful_land.Utils.PriceUtils;
import com.example.peaceful_land.Utils.VariableUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

import static com.example.peaceful_land.Utils.VariableUtils.TYPE_UPLOAD_AVATAR;

@Service @RequiredArgsConstructor
public class AccountService implements IAccountService {

    private final AccountRepository accountRepository;
    private final PurchaseRepository purchaseRepository;
    private final EmailService emailService;
    private final RedisService redisService;

    @Override
    public AccountInfoResponse getAccountInfo(Long userId) {
        return accountRepository.findById(userId)
                .map(AccountInfoResponse::from)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));
    }

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

        // TODO: Mã hóa mật khẩu
        return accountRepository.save(
                Account.builder()
                        .role((byte) 0)
                        .email(userInfo.getEmail())
                        .password(userInfo.getPassword())   // TODO: Mã hóa mật khẩu
                        .accountBalance(0)
                        .name(userInfo.getName())
                        .birthDate(userInfo.getBirthDate())
                        .phone(userInfo.getPhone())
                        .status(true)
                        .avatarUrl(VariableUtils.DEFAULT_AVATAR)
                        .roleExpiration(LocalDate.of(9999, 12, 31))
                        .build()
        );
    }

    @Override
    public String changePassword(ChangePasswordRequest request) {
        return accountRepository.findById(request.getUserId())
                .map(account -> {
                    if (!account.getPassword().equals(request.getOldPassword())) {
                        throw new RuntimeException("Mật khẩu cũ không chính xác");
                    }
                    account.setPassword(request.getNewPassword()); // TODO: Mã hóa mật khẩu
                    accountRepository.save(account);
                    return "Đổi mật khẩu thành công";
                })
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));
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

    @Override
    public boolean resetRoleIfExpired(Long id) {
        return accountRepository.findById(id).map(account -> {
            if (account.getRoleExpiration().isBefore(LocalDate.now())) {
                account.setRole((byte) 0);
                account.setRoleExpiration(LocalDate.of(9999, 12, 31));
                accountRepository.save(account);
                return true;
            }
            return false;
        }).orElse(false);
    }

    @Override
    public Account purchaseRole(PurchaseRoleRequest request) {
        Account account = accountRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));
        Long requiredMoney = PriceUtils.getRolePriceFromDayRange(request.getRole(), request.getDay());
        if (account.getAccountBalance() < requiredMoney) {
            throw new RuntimeException("Số dư không đủ. Yêu cầu tối thiểu " + requiredMoney);
        }
        // Nếu đã có role thì cộng thêm thời gian
        if (account.getRole() == request.getRole()){
            account.setRoleExpiration(account.getRoleExpiration().plusDays(request.getDay()));
            // Lưu thông tin giao dịch
            purchaseRepository.save(Purchase.builder()
                    .user(account)
                    .action(VariableUtils.PURCHASE_ACTION_EXTEND_ROLE)
                    .amount(requiredMoney)
                    .build()
            );
        }
        // Nếu role mới cao hơn role cũ thì cập nhật role mới và thời gian
        else if (account.getRole() < request.getRole()){
            account.setRole(request.getRole());
            account.setRoleExpiration(LocalDate.now().plusDays(request.getDay()));
            // Lưu thông tin giao dịch
            purchaseRepository.save(Purchase.builder()
                    .user(account)
                    .action(Objects.equals(request.getRole(), VariableUtils.ROLE_BROKER) ?
                            VariableUtils.PURCHASE_ACTION_BROKER :
                            VariableUtils.PURCHASE_ACTION_BROKER_VIP)
                    .amount(requiredMoney)
                    .build()
            );
        }
        else {
            throw new RuntimeException("Không thể mua role thấp hơn role hiện tại");
        }
        account.setAccountBalance(account.getAccountBalance() - requiredMoney);
        return accountRepository.save(account);
    }

    @Override
    public int getExpirationRange(String userId) {
        Account account = accountRepository.findByEmail(userId)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));
        if (account.getRole() == VariableUtils.ROLE_NORMAL) return 7;
        else if (account.getRole() == VariableUtils.ROLE_BROKER) return 10;
        else return 14;
    }

    @Override
    public int getApprovalRange(Byte role) {
        // Nếu role không phải từ 0 đến 2 thì trả về -1
        if (role < 0 || role > 2) return -1;
        else if (Objects.equals(role, VariableUtils.ROLE_NORMAL)) return 2;
        else return 1;
    }

    @Override
    public String changeAvatar(ChangeAvatarRequest request) {
        // Kiểm tra tài khoản tồn tại
        Account account = accountRepository.findById(request.getUserId()).orElse(null);
        if (account == null) throw new RuntimeException("Tài khoản không tồn tại");
        // Kiểm tra file hợp lệ
        MultipartFile file = request.getImage();
        ImageUtils.checkImageFile(file);
        // Thực hiện thay đổi avatar
        try {
            // Lưu file vào server
            String fileName = ImageUtils.saveFileServer(file, TYPE_UPLOAD_AVATAR);
            // Xóa file cũ
            if (!account.getAvatarUrl().equals(VariableUtils.DEFAULT_AVATAR)) {
                ImageUtils.deleteFileServer(account.getAvatarUrl());
            }
            // Cập nhật đường dẫn file mới vào database
            account.setAvatarUrl(fileName);
            accountRepository.save(account);
            // Trả về thông báo thành công
            return "Đổi avatar thành công. Đường dẫn mới: " + account.getAvatarUrl();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu tập tin: " + e.getMessage());
        }
    }
}