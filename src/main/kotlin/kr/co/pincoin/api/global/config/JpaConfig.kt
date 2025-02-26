package kr.co.pincoin.api.global.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@Configuration
@EnableJpaAuditing  // created, modified 필드 자동 관리
class JpaConfig {
}