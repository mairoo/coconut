## `KeycloakJwtAuthenticationConverter`

- 단수 역할 부여

```kotlin
val user = userRepository.findUser(UserSearchCriteria(email = email, isActive = true))

if (user != null) {
    val role = if (user.isSuperuser) "ROLE_ADMIN" else "ROLE_USER"
    val authority = SimpleGrantedAuthority(role)
    logger.debug { "사용자 권한 조회 완료: email=$email, role=$role" }
    listOf(authority)
} else {
    logger.warn { "사용자를 찾을 수 없음: email=$email" }
    listOf(SimpleGrantedAuthority("ROLE_USER"))
}
```

- 복수 역할 부여

```kotlin
val user = userRepository.findUserWithRoles(UserSearchCriteria(email = email, isActive = true))

if (user != null) {
    val authorities = user.roles.map { role ->
        SimpleGrantedAuthority(role.name)
    }
    logger.debug { "사용자 권한 조회 완료: email=$email, roles=${user.roles.map { it.name }}" }
    authorities
} else {
    logger.warn { "사용자를 찾을 수 없음: email=$email" }
    listOf(SimpleGrantedAuthority("ROLE_MEMBER"))
}
```