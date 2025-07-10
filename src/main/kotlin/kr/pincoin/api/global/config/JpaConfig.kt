package kr.pincoin.api.global.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@Configuration
// created, modified 필드 자동 관리
@EnableJpaAuditing // (auditorAwareRef = "userAuditorAware")
class JpaConfig