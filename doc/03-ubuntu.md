# 우분투 서버

## 기본 설정

- AMD EPYC
- 4 vCPUs 12GB 7.00TB 260GB

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
nano ~/.ssh/authorized_keys
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

# 도커 설치 확인
sudo docker run hello-world

sudo mkdir -p /opt/docker
sudo chown ubuntu:ubuntu /opt/docker
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