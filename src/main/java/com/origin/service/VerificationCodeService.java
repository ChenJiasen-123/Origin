package com.origin.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class VerificationCodeService {
    private static class Entry {
        String code;
        long expireAt;
    }
    private final Map<String, Entry> cache = new ConcurrentHashMap<>();
    private final long ttlMillis = 5 * 60 * 1000;

    public String generate(String email) {
        String code = String.format("%06d", ThreadLocalRandom.current().nextInt(0, 1000000));
        Entry e = new Entry();
        e.code = code;
        e.expireAt = Instant.now().toEpochMilli() + ttlMillis;
        cache.put(email, e);
        System.out.println("DEV Email Code for " + email + ": " + code);
        return code;
    }

    public boolean verify(String email, String code) {
        Entry e = cache.get(email);
        if (e == null) return false;
        if (Instant.now().toEpochMilli() > e.expireAt) {
            cache.remove(email);
            return false;
        }
        boolean ok = e.code.equals(code);
        if (ok) {
            cache.remove(email); // 单次使用
        }
        return ok;
    }
}
