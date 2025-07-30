# 우분투 서버

## 기본 설정

```
# 패키지 업데이트
apt-get update && apt-get dist-upgrade
apt-get autormeove
apt-get autoclean

# 타임존 설정
timedatectl set-timezone Asia/Seoul

# 로케일 설정
apt-get install -y language-pack-ko
update-locale LANG=en_US.UTF-8

# 호스트 이름 (필요 시)
hostnamectl
hostnamectl set-hostname my-server-name

# vim 디폴트
update-alternatives --config editor
```

## `ubuntu` 관리 계정 (`ubuntu` 디폴트 계정은 이미 `sudo` 그룹에 속해 있음)

```
visudo
```

```
%sudo   ALL=(ALL:ALL) NOPASSWD: ALL
```

로컬 컴퓨터에서

```
# SSH 키 생성 (이미 있다면 생략)
ssh-keygen -t rsa -b 4096 -C "your_email@example.com"
```

원격 서버에서

```
# ubuntu 계정으로 전환
sudo su - ubuntu

# .ssh 디렉토리 생성
mkdir -p ~/.ssh
chmod 700 ~/.ssh

# authorized_keys 파일 생성
vim ~/.ssh/authorized_keys
# 여기에 로컬의 ~/.ssh/id_rsa.pub 내용 복사 붙여넣기

# 파일 권한 설정
chmod 600 ~/.ssh/authorized_keys
```

루트 원격 접속 금지 및 키 접속만 허용

```
sudo vi /etc/ssh/sshd_config
```

```
PermitRootLogin no
PasswordAuthentication no
PubkeyAuthentication yes
```

```
sudo service ssh restart
```

## 도커 시스템

https://docs.docker.com/engine/install/ubuntu/#install-using-the-repository

```
# 도커 공식 GPG 키 추가
sudo apt-get update
sudo apt-get install ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

# APT 소스에 저장소 추가
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "${UBUNTU_CODENAME:-$VERSION_CODENAME}") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update

# 도커 패키지 설치
sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# ubuntu 계정 docker 그룹에 추가하여 sudo 권한 필요 없이 접근
sudo usermod -aG docker ubuntu

# 현재 세션에 그룹 변경사항 적용
newgrp docker

# 도커 설치 확인
docker run hello-world

sudo mkdir -p /opt/docker
sudo chown ubuntu:ubuntu /opt/docker
```

`.dockerignore`

```dockerignore
**/build/
**/.gradle/
.gradle/
*.log
logs/
```

## 호스트 nginx

### 설치

```
sudo apt-get install nginx
sudo ufw allow "Nginx Full"
sudo ufw status
```

### 설정

`/etc/nginx/sites-enabled/default`

```
server {
listen 80;
listen [::]:80;

    server_name _;
    root /var/www/html;
    index index.html index.htm;
    
    # 서버 정보 숨기기
    server_tokens off;
    
    # 숨김 파일 차단
    location ~ /\. {
        deny all;
    }
    
    # 기본 위치
    location / {
        try_files $uri $uri/ =404;
    }
    
    # PHP 차단 (불필요시)
    location ~ \.php$ {
        return 404;
    }
}
```

`/etc/nginx/nginx.conf`

```
http {
    # 서버 토큰 숨기기
    server_tokens off;
    
    # 파일 업로드 크기 제한
    client_max_body_size 10M;
    
    # 기존 설정들...
}
```

```
# 설정 확인
sudo nginx -t

# 적용
sudo systemctl reload nginx
```

# 최종 운영환경 아키텍처 검토

## 호스트 nginx 리버스 프록시 아키텍처

```
인터넷
    ↓
호스트 nginx (443)
    ├── api.example.com → localhost:8800 (Docker nginx-api)
    ├── www.example.com → localhost:8300 (Docker nginx-web)  
    ├── keycloak.example.com → localhost:8801 (Docker keycloak)
    └── grafana.example.com → localhost:9300 (Docker grafana)
```

#### 호스트 레벨

| 서비스   | 도메인                  | 포트  | 프록시 대상           |
|-------|----------------------|-----|------------------|
| nginx | api.example.com      | 443 | → localhost:8800 |
| nginx | www.example.com      | 443 | → localhost:8300 |
| nginx | keycloak.example.com | 443 | → localhost:8801 |
| nginx | grafana.example.com  | 443 | → localhost:9300 |

#### Docker 레벨

| 서비스               | 외부포트  | 내부포트 | 역할        |
|-------------------|-------|------|-----------|
| redis             | -     | 6379 | 내부전용      |
| keycloak-postgres | 15432 | 5432 | 관리용       |
| keycloak          | 8801  | 8080 | 인증서버      |
| nginx-api         | 8800  | 8080 | API 로드밸런서 |
| backend-1         | -     | 8080 | 내부전용      |
| backend-2         | -     | 8080 | 내부전용      |
| prometheus        | -     | 9090 | 내부전용      |
| grafana           | 9300  | 3000 | 모니터링      |
| nginx-web         | 8300  | 3000 | 웹 로드밸런서   |
| frontend-1        | -     | 3000 | 내부전용      |
| frontend-2        | -     | 3000 | 내부전용      |