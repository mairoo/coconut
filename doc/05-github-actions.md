# 구성

```
./github/workflows/test.yml
./github/workflows/prod-build.yml
./github/workflows/prod-deploy.yml
```

# GitHub Secrets 3개 설정

- PINCOIN_SERVER_HOST
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
- **PINCOIN_USERNAME**: `ubuntu`

## GitHub Actions 테스트

# Self-hosted runner(내 서버에서 실행) vs. GitHub-hosted runner (GitHub 서버에서 실행)

Self-hosted runner

- 이미 서버 관리 중: 추가 관리 부담 적음
- 성능 향상: 3-5배 빠른 배포 가능
- 비용 절약: GitHub Actions 과금 없음
- 보안 강화: 내부 네트워크에서 처리