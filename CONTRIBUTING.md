# Contributing to Ooder SDK

感谢您有兴趣为 Ooder SDK 做出贡献！

## 行为准则

本项目采用贡献者公约作为行为准则。参与此项目即表示您同意遵守其条款。

## 如何贡献

### 报告 Bug

如果您发现了 bug，请创建一个 [Issue](https://github.com/oodercn/ooder-sdk/issues)，并包含以下信息：

- 问题描述
- 复现步骤
- 预期行为
- 实际行为
- 环境信息（JDK 版本、操作系统等）

### 提交功能请求

欢迎提交功能请求！请创建一个 [Issue](https://github.com/oodercn/ooder-sdk/issues)，并描述：

- 功能描述
- 使用场景
- 预期效果

### 提交代码

1. **Fork 仓库**

   点击右上角的 Fork 按钮

2. **克隆仓库**

   ```bash
   git clone https://github.com/YOUR_USERNAME/ooder-sdk.git
   cd ooder-sdk
   ```

3. **创建分支**

   ```bash
   git checkout -b feature/your-feature-name
   ```

4. **进行更改**

   - 遵循现有的代码风格
   - 添加必要的测试
   - 更新相关文档

5. **提交更改**

   ```bash
   git add .
   git commit -m "feat: 添加新功能描述"
   ```

   提交信息格式：
   - `feat:` 新功能
   - `fix:` Bug 修复
   - `docs:` 文档更新
   - `refactor:` 代码重构
   - `test:` 测试相关
   - `chore:` 构建/工具相关

6. **推送到分支**

   ```bash
   git push origin feature/your-feature-name
   ```

7. **创建 Pull Request**

   在 GitHub 上创建 Pull Request，并描述您的更改

## 开发指南

### 环境要求

- JDK 8+
- Maven 3.6+

### 构建项目

```bash
mvn clean install
```

### 运行测试

```bash
mvn test
```

### 代码风格

- 遵循 Java 命名约定
- 使用 4 个空格缩进
- 每行最大 120 个字符
- 添加必要的注释

### 项目结构

```
ooder-sdk/
├── agent-sdk/          # Agent SDK 核心模块
├── ooder-annotation/   # 注解模块
├── ooder-common/       # 通用组件模块
├── scene-engine/       # 场景引擎
└── pom.xml             # 父 POM
```

## 许可证

通过贡献代码，您同意您的贡献将根据 MIT 许可证进行许可。
