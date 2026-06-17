package com.example.smartcommunity.controller;

import com.example.smartcommunity.dto.response.ApiResponse;
import com.example.smartcommunity.entity.Wallet;
import com.example.smartcommunity.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @GetMapping("/{userId}")
    public ApiResponse<Wallet> getWallet(@PathVariable Long userId) {
        Wallet wallet = walletService.getWallet(userId);
        return ApiResponse.success(wallet);
    }

    @GetMapping("/balance/{userId}")
    public ApiResponse<Map<String, Object>> getBalance(@PathVariable Long userId) {
        BigDecimal balance = walletService.getBalance(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("balance", balance);
        return ApiResponse.success(result);
    }

    @PostMapping("/recharge")
    public ApiResponse<Map<String, Object>> recharge(@RequestBody Map<String, Object> params) {
        Long userId = Long.parseLong(params.get("userId").toString());
        BigDecimal amount = new BigDecimal(params.get("amount").toString());
        
        Wallet wallet = walletService.recharge(userId, amount);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("balance", wallet.getBalance());
        return ApiResponse.success(result);
    }

    @PostMapping("/pay")
    public ApiResponse<Map<String, Object>> pay(@RequestBody Map<String, Object> params) {
        Long userId = Long.parseLong(params.get("userId").toString());
        BigDecimal amount = new BigDecimal(params.get("amount").toString());
        
        boolean success = walletService.pay(userId, amount);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        if (!success) {
            result.put("message", "余额不足");
        } else {
            result.put("balance", walletService.getBalance(userId));
        }
        return ApiResponse.success(result);
    }
}
