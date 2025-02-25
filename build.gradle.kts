plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	kotlin("plugin.jpa") version "1.9.25"
	kotlin("kapt") version "1.9.25"
	id("org.springframework.boot") version "3.4.3"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "kr.co.pincoin"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}


// 버전 상수
object Versions {
	const val KOTLIN_LOGGING_VERSION = "7.0.3"
	const val QUERYDSL_VERSION = "5.1.0"
	const val JJWT_VERSION = "0.12.6"
	const val SPRINGDOC_OPENAPI_VERSION = "2.8.3"
	const val NETTY_VERSION = "4.1.116.Final"
	const val DANAL_VERSION = "1.6.2"
	const val COMMONS_CODEC_VERSION = "1.17.2"
}

// OS 및 아키텍처 관련 상수
object Platform {
	val nettyClassifier: String? = when {
		System.getProperty("os.name").lowercase().contains("mac") ->
			if (System.getProperty("os.arch").lowercase().contains("aarch64")) "osx-aarch_64"
			else "osx-x86_64"

		else -> null
	}
}

dependencies {
	// 스프링부트
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-webflux")

	// 코틀린
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

	// 개발도구
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// 데이터베이스
	runtimeOnly("org.postgresql:postgresql")

	// 애노테이션 프로세서
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

	// 테스트
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// 로깅
	implementation("io.github.oshai:kotlin-logging-jvm:${Versions.KOTLIN_LOGGING_VERSION}")

	// QueryDSL
	implementation("com.querydsl:querydsl-jpa:${Versions.QUERYDSL_VERSION}:jakarta")
	kapt("com.querydsl:querydsl-apt:${Versions.QUERYDSL_VERSION}:jakarta")
	kapt("jakarta.annotation:jakarta.annotation-api")
	kapt("jakarta.persistence:jakarta.persistence-api")

	// JWT
	implementation("io.jsonwebtoken:jjwt-api:${Versions.JJWT_VERSION}")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:${Versions.JJWT_VERSION}")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:${Versions.JJWT_VERSION}")

	// Commons-Codec for TOTP
	implementation("commons-codec:commons-codec:${Versions.COMMONS_CODEC_VERSION}")

	// Netty DNS resolver for Mac
	Platform.nettyClassifier?.let {
		runtimeOnly("io.netty:netty-resolver-dns-native-macos:${Versions.NETTY_VERSION}:${it}")
	}
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
