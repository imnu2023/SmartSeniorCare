package com.example.smartcommunity.service;

import com.example.smartcommunity.entity.Wallet;

import java.math.BigDecimal;

public interface WalletService {
    
    Wallet getWallet(Long userId);
    
    Wallet recharge(Long userId, BigDecimal amount);
    
    boolean pay(Long userId, BigDecimal amount);
    
    BigDecimal getBalance(Long userId);
}
