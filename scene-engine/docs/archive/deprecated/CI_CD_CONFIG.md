# CI/CD 自动配置

> **文档版本**: 1.0.0  
> **创建日期**: 2026-03-11  
> **用途**: 配置自动生成 skill-index.yaml 的 CI/CD 流程

---

## 一、GitHub Actions 配置

### 1.1 工作流文件

**文件路径**: `.github/workflows/generate-skill-index.yml`

```yaml
name: Generate Skill Index

on:
  push:
    branches: [ main, develop ]
    paths:
      - 'config/**'
      - 'skills/**/skill-index-entry.yaml'
  pull_request:
    branches: [ main ]
    paths:
      - 'config/**'
      - 'skills/**/skill-index-entry.yaml'
  workflow_dispatch:  # 手动触发

jobs:
  generate-index:
    runs-on: ubuntu-latest
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v3
      
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        
    - name: Cache Maven dependencies
      uses: actions/cache@v3
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
        restore-keys: ${{ runner.os }}-m2
        
    - name: Build aggregator tool
      run: |
        cd scene-engine
        mvn compile -q
        
    - name: Generate skill-index.yaml
      run: |
        java -cp scene-engine/target/classes:scene-engine/target/dependency/* \
          net.ooder.scene.skill.index.SkillIndexAggregator \
          --schema config/schema.yaml \
          --addresses config/addresses.yaml \
          --categories config/categories.yaml \
          --skills-dir skills/ \
          --output skill-index.yaml
          
    - name: Validate generated index
      run: |
        java -cp scene-engine/target/classes:scene-engine/target/dependency/* \
          net.ooder.scene.skill.validation.SkillIndexValidator \
          skills/
          
    - name: Commit and push changes
      if: github.event_name == 'push' && github.ref == 'refs/heads/main'
      run: |
        git config --local user.email "action@github.com"
        git config --local user.name "GitHub Action"
        git add skill-index.yaml
        git diff --staged --quiet || (git commit -m "ci: auto-generate skill-index.yaml" && git push)
```

---

## 二、GitLab CI 配置

### 2.1 CI 配置文件

**文件路径**: `.gitlab-ci.yml`

```yaml
stages:
  - build
  - generate
  - validate
  - deploy

variables:
  MAVEN_OPTS: "-Dmaven.repo.local=$CI_PROJECT_DIR/.m2/repository"
  MAVEN_CLI_OPTS: "--batch-mode --errors --fail-at-end --show-version"

cache:
  paths:
    - .m2/repository
    - scene-engine/target/

build-aggregator:
  stage: build
  image: maven:3.9-eclipse-temurin-17
  script:
    - cd scene-engine
    - mvn $MAVEN_CLI_OPTS compile
  only:
    changes:
      - config/**/*
      - skills/**/skill-index-entry.yaml

generate-index:
  stage: generate
  image: maven:3.9-eclipse-temurin-17
  script:
    - java -cp scene-engine/target/classes:scene-engine/target/dependency/*
        net.ooder.scene.skill.index.SkillIndexAggregator
        --schema config/schema.yaml
        --addresses config/addresses.yaml
        --categories config/categories.yaml
        --skills-dir skills/
        --output skill-index.yaml
  artifacts:
    paths:
      - skill-index.yaml
    expire_in: 1 hour
  only:
    changes:
      - config/**/*
      - skills/**/skill-index-entry.yaml

validate-index:
  stage: validate
  image: maven:3.9-eclipse-temurin-17
  script:
    - java -cp scene-engine/target/classes:scene-engine/target/dependency/*
        net.ooder.scene.skill.validation.SkillIndexValidator
        skills/
  only:
    changes:
      - config/**/*
      - skills/**/skill-index-entry.yaml

commit-index:
  stage: deploy
  image: alpine/git
  script:
    - git config --global user.email "gitlab-ci@ooder.net"
    - git config --global user.name "GitLab CI"
    - git add skill-index.yaml
    - 'git diff --staged --quiet || (git commit -m "ci: auto-generate skill-index.yaml [skip ci]" && git push origin HEAD:$CI_COMMIT_REF_NAME)'
  only:
    - main
    - develop
  when: on_success
```

---

## 三、Jenkins 配置

### 3.1 Jenkinsfile

**文件路径**: `Jenkinsfile`

```groovy
pipeline {
    agent any
    
    tools {
        maven 'Maven-3.9'
        jdk 'JDK-17'
    }
    
    triggers {
        pollSCM('*/5 * * * *')  // 每5分钟轮询
    }
    
    stages {
        stage('Build Aggregator') {
            steps {
                dir('scene-engine') {
                    sh 'mvn compile -q'
                }
            }
        }
        
        stage('Generate Index') {
            steps {
                sh '''
                    java -cp scene-engine/target/classes:scene-engine/target/dependency/* \
                        net.ooder.scene.skill.index.SkillIndexAggregator \
                        --schema config/schema.yaml \
                        --addresses config/addresses.yaml \
                        --categories config/categories.yaml \
                        --skills-dir skills/ \
                        --output skill-index.yaml
                '''
            }
        }
        
        stage('Validate') {
            steps {
                sh '''
                    java -cp scene-engine/target/classes:scene-engine/target/dependency/* \
                        net.ooder.scene.skill.validation.SkillIndexValidator \
                        skills/
                '''
            }
        }
        
        stage('Commit Changes') {
            when {
                branch 'main'
            }
            steps {
                sh '''
                    git config user.email "jenkins@ooder.net"
                    git config user.name "Jenkins"
                    git add skill-index.yaml
                    git diff --staged --quiet || (git commit -m "ci: auto-generate skill-index.yaml" && git push)
                '''
            }
        }
    }
    
    post {
        always {
            archiveArtifacts artifacts: 'skill-index.yaml', fingerprint: true
        }
        failure {
            mail to: 'team@ooder.net',
                 subject: "Skill Index Generation Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "Check console output at ${env.BUILD_URL}"
        }
    }
}
```

---

## 四、本地开发脚本

### 4.1 生成脚本 (Windows)

**文件路径**: `scripts/generate-index.bat`

```batch
@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo ========================================
echo Skill Index Generator
echo ========================================

set SCENE_ENGINE_DIR=..\scene-engine
set SKILLS_DIR=..\skills
set OUTPUT_FILE=%SKILLS_DIR%\skill-index.yaml

echo [1/4] Building aggregator tool...
cd %SCENE_ENGINE_DIR%
call mvn compile -q
if errorlevel 1 (
    echo ERROR: Build failed
    exit /b 1
)

echo [2/4] Generating skill-index.yaml...
cd %SKILLS_DIR%
java -cp %SCENE_ENGINE_DIR%\target\classes;%SCENE_ENGINE_DIR%\target\dependency\* ^
    net.ooder.scene.skill.index.SkillIndexAggregator ^
    --schema config\schema.yaml ^
    --addresses config\addresses.yaml ^
    --categories config\categories.yaml ^
    --skills-dir . ^
    --output skill-index.yaml

if errorlevel 1 (
    echo ERROR: Generation failed
    exit /b 1
)

echo [3/4] Validating generated index...
java -cp %SCENE_ENGINE_DIR%\target\classes;%SCENE_ENGINE_DIR%\target\dependency\* ^
    net.ooder.scene.skill.validation.SkillIndexValidator ^
    .

if errorlevel 1 (
    echo ERROR: Validation failed
    exit /b 1
)

echo [4/4] Done!
echo ========================================
echo Generated: %OUTPUT_FILE%
echo ========================================

endlocal
```

### 4.2 生成脚本 (Linux/Mac)

**文件路径**: `scripts/generate-index.sh`

```bash
#!/bin/bash

set -e

echo "========================================"
echo "Skill Index Generator"
echo "========================================"

SCENE_ENGINE_DIR="../scene-engine"
SKILLS_DIR="../skills"
OUTPUT_FILE="$SKILLS_DIR/skill-index.yaml"

echo "[1/4] Building aggregator tool..."
cd "$SCENE_ENGINE_DIR"
mvn compile -q

echo "[2/4] Generating skill-index.yaml..."
cd "$SKILLS_DIR"
java -cp "$SCENE_ENGINE_DIR/target/classes:$SCENE_ENGINE_DIR/target/dependency/*" \
    net.ooder.scene.skill.index.SkillIndexAggregator \
    --schema config/schema.yaml \
    --addresses config/addresses.yaml \
    --categories config/categories.yaml \
    --skills-dir . \
    --output skill-index.yaml

echo "[3/4] Validating generated index..."
java -cp "$SCENE_ENGINE_DIR/target/classes:$SCENE_ENGINE_DIR/target/dependency/*" \
    net.ooder.scene.skill.validation.SkillIndexValidator \
    .

echo "[4/4] Done!"
echo "========================================"
echo "Generated: $OUTPUT_FILE"
echo "========================================"
```

---

## 五、使用说明

### 5.1 手动触发生成

```bash
# Windows
.\scripts\generate-index.bat

# Linux/Mac
./scripts/generate-index.sh
```

### 5.2 验证单个技能

```bash
# 验证所有技能
mvn exec:java -Dexec.mainClass="net.ooder.scene.skill.validation.SkillIndexValidator"

# 验证单个技能
mvn exec:java -Dexec.mainClass="net.ooder.scene.skill.validation.SkillIndexValidator" \
  -Dexec.args="skills/scenes/skill-knowledge-qa/skill-index-entry.yaml"
```

---

## 六、配置说明

### 6.1 触发条件

CI/CD 在以下情况自动触发：
- `config/schema.yaml` 变更
- `config/addresses.yaml` 变更
- `config/categories.yaml` 变更
- 任意 `skills/**/skill-index-entry.yaml` 变更

### 6.2 生成流程

```
1. 检出代码
2. 编译聚合工具
3. 生成 skill-index.yaml
4. 验证配置
5. 提交变更 (仅 main 分支)
```

---

## 七、故障排查

### 7.1 常见问题

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| 生成失败 | 技能条目格式错误 | 运行验证程序检查 |
| 验证失败 | 字段值无效 | 检查枚举值范围 |
| 提交失败 | 权限问题 | 检查 CI token |

### 7.2 调试模式

```bash
# 详细日志
java -Ddebug=true SkillIndexAggregator ...

# 仅生成不提交
java SkillIndexAggregator --dry-run ...
```

---

**状态**: 配置完成，可立即使用
