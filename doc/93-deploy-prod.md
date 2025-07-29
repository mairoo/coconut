# 구성

```
/opt/docker/scripts/
/opt/docker/logs/
/opt/docker/logs/projects/
/opt/docker/logs/projects/pincoin/
/opt/docker/logs/projects/pincoin/infra/
/opt/docker/logs/projects/pincoin/backend/
/opt/docker/logs/projects/pincoin/frontend/
/opt/docker/logs/projects/cleanup.sh
```

# 도커 관리

# 인프라 구성

```dotenv
PREFIX=pincoin

# keycloak postgres 설정
KEYCLOAK_POSTGRES_DATABASE=keycloak
KEYCLOAK_POSTGRES_USER=keycloak
KEYCLOAK_POSTGRES_PASSWORD=secure_password_123

# keycloak db 연동
KEYCLOAK_DB=postgres
```

```yaml
services:
  redis:
    container_name: ${PREFIX}-redis
    image: redis:alpine
    restart: unless-stopped
    volumes:
      - redis-data:/data
    networks:
      - app-network
    environment:
      - TZ=Asia/Seoul
    logging:
      driver: "json-file"
      options:
        max-size: "20m"
        max-file: "10"

  keycloak-postgres:
    container_name: ${PREFIX}-keycloak-postgres
    image: postgres:15-alpine
    restart: unless-stopped
    ports:
      - "15432:5432"
    volumes:
      - keycloak-postgres-data:/var/lib/postgresql/data
    networks:
      - app-network
    environment:
      - TZ=Asia/Seoul
      - POSTGRES_DB=${KEYCLOAK_POSTGRES_DATABASE}
      - POSTGRES_USER=${KEYCLOAK_POSTGRES_USER}
      - POSTGRES_PASSWORD=${KEYCLOAK_POSTGRES_PASSWORD}
    logging:
      driver: "json-file"
      options:
        max-size: "20m"
        max-file: "10"

  keycloak:
    container_name: ${PREFIX}-keycloak
    image: quay.io/keycloak/keycloak:26.3.1
    restart: unless-stopped
    ports:
      - "8081:8080"
    depends_on:
      - keycloak-postgres
    networks:
      - app-network
    environment:
      - TZ=Asia/Seoul
      - KC_DB=${KEYCLOAK_DB}
      - KC_DB_URL=jdbc:postgresql://keycloak-postgres:5432/${KEYCLOAK_POSTGRES_DATABASE}
      - KC_DB_USERNAME=${KEYCLOAK_POSTGRES_USER}
      - KC_DB_PASSWORD=${KEYCLOAK_POSTGRES_PASSWORD}
      # - KC_HOSTNAME=keycloak.pincoin.kr
      # - KC_HOSTNAME_PORT=443
      - KC_HOSTNAME_STRICT=false
      - KC_HOSTNAME_STRICT_HTTPS=false
      - KC_PROXY=edge # 프록시 모드
      - KC_PROXY_HEADERS=xforwarded
      - KC_HTTP_ENABLED=true
      - KC_HEALTH_ENABLED=true
      - KC_METRICS_ENABLED=true
    command: start
    volumes:
      - keycloak-data:/opt/keycloak/data
    logging:
      driver: "json-file"
      options:
        max-size: "20m"
        max-file: "10"

networks:
  app-network:
    name: ${PREFIX}-network
    driver: bridge

volumes:
  redis-data:
    name: ${PREFIX}-redis-data
  keycloak-postgres-data:
    name: ${PREFIX}-keycloak-postgres-data
  keycloak-data:
    name: ${PREFIX}-keycloak-data
```

# 포트 허용

```
sudo ufw allow 15432/tcp
sudo ufw allow 8081/tcp
```

## nginx 설정

```
server {
	listen 443 ssl http2;
	server_name keycloak.pincoin.kr;

	ssl_certificate /opt/docker/projects/pincoin/ssl/pincoin.kr.pem;
	ssl_certificate_key /opt/docker/projects/pincoin/ssl/pincoin.kr.key;

	access_log /opt/docker/projects/pincoin/infra/logs/access.log;
	error_log /opt/docker/projects/pincoin/infra/logs/error.log;

	location / {
		proxy_pass http://localhost:8081;
		proxy_set_header Host $host;
		proxy_set_header X-Real-IP $remote_addr;
		proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
		proxy_set_header X-Forwarded-Proto $scheme;
		proxy_set_header X-Forwarded-Host $host;

		# WebSocket 지원
		proxy_http_version 1.1;
		proxy_set_header Upgrade $http_upgrade;
		proxy_set_header Connection "upgrade";
	}
}
```

# keycloak 설정

- 임시 계정 `temp-admin` 생성
- Keycloak 영구 `admin` 계정 생성
- `admin` 계정 로그인 후 `temp-admin` 계정 삭제
- pincoin realm 생성
- pincoin realm 이메일 설정
- master realm, pincoin realm 이벤트 로깅 저장 설정
- pincoin realm에서 pincoin-backend client 생성