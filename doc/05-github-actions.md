# 구성

```
./github/workflows/test.yml
./github/workflows/prod-build.yml
./github/workflows/prod-deploy.yml
```

# GitHub Secrets 4개 설정

- PINCOIN_SERVER_HOST
- PINCOIN_SERVER_PORT
- PINCOIN_USERNAME
- PINCOIN_SSH_KEY

## SSH 키 생성 및 공개 키 authorized_keys에 추가

```bash
# SSH 키 생성 (ubuntu 사용자로)
ssh-keygen -t ed25519 -C "github-actions@pincoin" -f ~/.ssh/github_actions_key

# 패스프레이즈 입력 시 그냥 엔터 두 번 (비워두기)
Generating public/private ed25519 key pair.
Enter passphrase (empty for no passphrase):
Enter same passphrase again:

# 공개키를 authorized_keys에 추가
cat ~/.ssh/github_actions_key.pub >> ~/.ssh/authorized_keys
```

## SSH 연결 테스트

```bash
# 로컬에서 자기 자신에게 SSH 연결 테스트
ssh -i ~/.ssh/github_actions_key ubuntu@localhost

# 또는 외부 IP로 테스트
ssh -i ~/.ssh/github_actions_key ubuntu@141.164.54.239
```

## Private Key 내용 복사 준비

```bash
# Private Key 전체 내용 출력 (이 내용을 GitHub Secrets에 등록)
cat ~/.ssh/github_actions_key
```

## GitHub Secrets 등록

GitHub `coconut` 저장소에서 Settings > Security > Secrets and variables > Actions > Repository secrets

- **PINCOIN_SSH_KEY**: 위에서 복사한 private key 전체 내용
- **PINCOIN_SERVER_HOST**: 서버 IP 주소
- **PINCOIN_SERVER_PORT**: SSH 포트
- **PINCOIN_USERNAME**: `ubuntu`

# Self-hosted runner(내 서버에서 실행) vs. GitHub-hosted runner (GitHub 서버에서 실행)

Self-hosted runner

- 이미 서버 관리 중: 추가 관리 부담 적음
- 성능 향상: 3-5배 빠른 배포 가능
- 비용 절약: GitHub Actions 과금 없음
- 보안 강화: 내부 네트워크에서 처리

## 설치

```
# 1. /opt/runner 디렉토리 생성
sudo mkdir -p /opt/runner

# 2. ubuntu 사용자 소유권 설정
sudo chown ubuntu:ubuntu /opt/runner

# 3. 디렉토리로 이동
cd /opt/runner

# 4. 최신 runner 패키지 다운로드
curl -o actions-runner-linux-x64-2.326.0.tar.gz -L https://github.com/actions/runner/releases/download/v2.326.0/actions-runner-linux-x64-2.326.0.tar.gz

# 5. 해시 검증
echo "9c74af9b4352bbc99aecc7353b47bcdfcd1b2a0f6d15af54a99f54a0c14a1de8  actions-runner-linux-x64-2.326.0.tar.gz" | shasum -a 256 -c

# 6. 압축 풀기
tar xzf ./actions-runner-linux-x64-2.326.0.tar.gz

# 7. Runner 생성 및 구성 시작
./config.sh --url https://github.com/mairoo/coconut --token ABCFRNAWHSWMJB6L7EVWFELIO5NCA

# 8. 구성 중 질문 답변:
# - Enter the name of the runner group: [엔터]
# - Enter the name of runner: [엔터] 또는 "pincoin-production"
# - Enter any additional labels: Production [엔터]
# - Enter name of work folder: [엔터]

# 9. 테스트 실행 및 ctrl + C 종료
./run.sh
```

## 서비스 등록

```
# 서비스 설치
sudo ./svc.sh install

# 서비스 시작
sudo ./svc.sh start

# 서비스 상태 확인
sudo ./svc.sh status
```

# GitHub Actions 테스트