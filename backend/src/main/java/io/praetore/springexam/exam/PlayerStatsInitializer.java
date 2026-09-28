package io.praetore.springexam.exam;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PlayerStatsInitializer implements ApplicationRunner {

    private final PlayerStatsRepository playerStatsRepository;

    public PlayerStatsInitializer(PlayerStatsRepository playerStatsRepository) {
        this.playerStatsRepository = playerStatsRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!playerStatsRepository.existsById(PlayerStats.DEFAULT_PLAYER_ID)) {
            playerStatsRepository.save(PlayerStats.fresh());
        }
    }
}
