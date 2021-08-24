package com.schoolplus.office.schedulers;

import com.schoolplus.office.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class RefreshTokensPurgeTask {

    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "${school-plus.auth-config.purging-refresh-tokens-cron}", zone="Europe/Istanbul")
    public void purgeExpiredRefreshTokens() {
        refreshTokenRepository.deleteAllExpiredSince();
        log.info("Expired tokens have been purged");
    }

}
