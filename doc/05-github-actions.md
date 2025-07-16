# 구성

- [.github/workflows/test.yml](../.github/workflows/test.yml)
- [.github/workflows/prod-build.yml](../.github/workflows/prod-build.yml)
- [.github/workflows/prod-deploy.yml](../.github/workflows/prod-deploy.yml)

# GitHub Secrets 2개 설정

GitHub `coconut` 저장소에서 Settings > Security > Secrets and variables > Actions > Repository secrets

- **PINCOIN_APPLICATION_PROD_YML**: application-prod.yml 파일 내용 전체
- **PREFIX**: `pincoin`

## Self-hosted runner 사용

## 장점

- 이미 서버 관리 중: 추가 관리 부담 적음
- 성능 향상: 3-5배 빠른 배포 가능
- 비용 절약: GitHub Actions 과금 없음
- 보안 강화: 내부 네트워크에서 처리
- SSH 설정 불필요: Runner가 GitHub 서버에 443 포트로 직접 명령 실행

## Self-hosted runner 설치

```bash
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

```bash
# 서비스 설치
sudo ./svc.sh install

# 서비스 시작
sudo ./svc.sh start

# 서비스 상태 확인
sudo ./svc.sh status
```

# 사용 방법

1. **빌드**: GitHub Actions에서 "Build for production" 워크플로우 실행
2. **배포**: GitHub Actions에서 "Deploy for production" 워크플로우 실행

# GitHub Actions 테스트