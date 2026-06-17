package com.example.smartcommunity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.smartcommunity.entity.Wallet;
import com.example.smartcommunity.mapper.WalletMapper;
import com.example.smartcommunity.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletMapper walletMapper;

    @Override
    public Wallet getWallet(Long userId) {
        QueryWrapper<Wallet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<Wallet> wallets = walletMapper.selectList(queryWrapper);
        
        Wallet wallet = null;
        if (wallets != null && !wallets.isEmpty()) {
            wallet = wallets.get(0);
        }
        
        if (wallet == null) {
            wallet = createWallet(userId);
        }
        if (wallet.getBalance() == null) {
            wallet.setBalance(BigDecimal.ZERO);
        }
        return wallet;
    }

    @Override
    @Transactional
    public Wallet recharge(Long userId, BigDecimal amount) {
        Wallet wallet = getWallet(userId);
        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletMapper.updateById(wallet);
        return wallet;
    }

    @Override
    @Transactional
    public boolean pay(Long userId, BigDecimal amount) {
        Wallet wallet = getWallet(userId);
        if (wallet.getBalance().compareTo(amount) >= 0) {
            wallet.setBalance(wallet.getBalance().subtract(amount));
            wallet.setUpdatedAt(LocalDateTime.now());
            walletMapper.updateById(wallet);
            return true;
        }
        return false;
    }

    @Override
    public BigDecimal getBalance(Long userId) {
        Wallet wallet = getWallet(userId);
        return wallet.getBalance();
    }

    private Wallet createWallet(Long userId) {
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCreatedAt(LocalDateTime.now());
        wallet.setUpdatedAt(LocalDateTime.now());
        walletMapper.insert(wallet);
        return wallet;
    }
}
