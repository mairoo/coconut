package kr.co.pincoin.api.global.security.adapter

import kr.co.pincoin.api.domain.user.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * `User` 도메인 객체를 스프링시큐리티의 `UserDetails`로 변환하는 어댑터
 *
 * - 도메인 모델(User)이 외부 프레임워크(UserDetails)에 의존하지 않도록 분리
 * - 도메인 계층 User -> 어댑터(단방형 mapper) -> UserDetails
 * - 단순 인터페이스 변환만 수행하며 비즈니스 로직은 포함하지 않음
 */
data class UserDetailsAdapter(
    val user: User,
) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> {
        val authorities = mutableListOf<GrantedAuthority>()

        // 기본 사용자 권한
        authorities.add(SimpleGrantedAuthority("ROLE_USER"))

        // 관리자인 경우 관리자 권한 추가
        if (user.isSuperuser) {
            authorities.add(SimpleGrantedAuthority("ROLE_ADMIN"))
        }

        // 스태프인 경우 스태프 권한 추가
        if (user.isStaff) {
            authorities.add(SimpleGrantedAuthority("ROLE_STAFF"))
        }

        return authorities
    }

    override fun getPassword(): String = user.password

    override fun getUsername(): String = user.email

    // UserDetails 인터페이스의 나머지 메서드들은 기본값 true 반환
    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}