package io.darpan.user_starter.service;

public interface TokenBlacklistService {

    void add(String token);

    boolean isBlacklisted(String token);
}
