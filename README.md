# Origin

Origin 是一款课程表与日程管理应用，支持网页版和 Android 安装版。通过周视图安排课程，也可以让 AI 根据自然语言或上传的课表生成待确认的日程变更。

## 功能

- **课程表与日程**：按周查看，新增、编辑、删除日程，设置时间、地点、备注、颜色和完成状态。
- **AI 助手**：查询安排、导入课表、通过对话编辑日程；新增、修改和删除操作先展示方案，经用户确认后执行。AI 新建日程必须提供有效颜色。
- **课表上传**：支持 PNG、JPG、WEBP 图片及 TXT、CSV、JSON 文件。图片识别需要所选模型支持视觉输入。
- **本地聊天记录**：按账号保存对话和草稿，切换页面后保留；记录存于当前设备／浏览器，不跨设备同步，图片附件原始数据不长期保存。
- **AI 服务设置**：用户自行填写 API Key，可配置兼容 Chat Completions 的接口地址和模型。当前默认地址为 `https://api.deepseek.com/v1`，模型为 `deepseek-flash`。
- **Android 闹钟**：提醒默认关闭；设置页提供总开关和授权，每条日程单独选择提醒时间，没有全局默认提前时间。
- **桌面课表**：Android 原生小组件展示今日安排，可从设置页请求添加。是否显示添加弹窗和组件入口取决于桌面支持情况。
- **账号与语言**：邮箱验证码、注册、登录、会话保持，以及简体中文／English 切换。

## 技术栈

| 层 | 技术 |
| --- | --- |
| 前端 | Vue 3、Vite、Vue Router、Element Plus、Axios |
| 后端 | Java 17、Spring Boot 3.4、Spring Security、Spring Data JPA |
| 数据库与邮件 | MySQL、SMTP |
| Android | Capacitor 8、原生小组件、闹钟与 Android Keystore |

## 项目结构

```text
Origin/
├── src/main/                 # Spring Boot 后端
├── src/test/                 # 后端测试
├── pom.xml
└── origin-web/
    ├── src/                  # Vue 页面及前端逻辑
    ├── tests/                # 前端功能测试
    ├── android/              # Android 原生工程
    └── capacitor.config.json
```

## 本地运行

准备 Java 17、Maven、MySQL 和 Node.js 22.12+。构建 Android 还需要 JDK 21 和 Android SDK；SDK 版本见 `origin-web/android/variables.gradle`。

### 后端

创建名为 `origin` 的 MySQL 数据库，并配置自己的数据库和 SMTP 凭据。建议通过环境变量覆盖 `src/main/resources/application.properties` 中的配置：

```text
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/origin?characterEncoding=utf-8
SPRING_DATASOURCE_USERNAME=<数据库用户名>
SPRING_DATASOURCE_PASSWORD=<数据库密码>
SPRING_MAIL_USERNAME=<SMTP账号>
SPRING_MAIL_PASSWORD=<SMTP授权码>
```

在仓库根目录启动：

```sh
mvn spring-boot:run
```

未指定端口时使用 Spring Boot 默认端口 `8080`。开发环境的表结构由 JPA 根据实体更新。

### 前端

```sh
cd origin-web
npm ci
npm run dev
```

本地连接自己的后端时，将 `vite.config.js` 中 `/origin` 代理的 `target` 改为后端地址，例如 `http://localhost:8080`。当前仓库中的代理目标是已有服务器，并非本地后端。

前端构建：

```sh
npm run build
npm run preview
```

部署时为 `/origin` 配置反向代理，或在构建前设置 `VITE_API_BASE_URL`。生产环境需要单独配置 API 路由；跨域部署还需正确配置后端 CORS 和会话 Cookie。

Windows 与 WSL 应分别安装依赖，不要共用 `node_modules`，以免缺少 Rolldown 等平台原生依赖。在实际运行的环境中执行 `npm ci`。

### Android

在 `origin-web` 目录执行：

```sh
npm run build
npx cap sync android
npx cap open android
```

在 Android Studio 中配置 JDK 21、SDK 和签名后构建。也可进入 `android` 目录运行 `./gradlew assembleDebug`（Windows 使用 `gradlew.bat assembleDebug`）。调试 APK 输出到 `origin-web/android/app/build/outputs/apk/debug/`。

Android API 地址也可通过构建时的 `VITE_API_BASE_URL` 指定；每次修改网页代码后，需要重新构建并同步到原生工程。

## 验证

```sh
cd origin-web
npm test
npm run build
```

Android 单元测试可在 `origin-web/android` 中运行：

```sh
./gradlew testDebugUnitTest
```

## 使用说明与限制

- API Key 由用户提供。Android 使用 Keystore 加密保存在设备上；网页端保存在当前浏览器。对话、相关日程和附件会发送至用户配置的 AI 服务。
- 保存聊天记录不等于无限上下文：当前 AI 请求携带最近 8 条消息和筛选后的相关日程；同类日程颜色会参考已有安排，但仍应核对待确认方案。
- Android 闹钟需要通知和精确闹钟权限，实际提醒还受系统后台及电池管理影响。网页版不提供原生闹钟。
- OPPO Reno6 系统桌面的组件添加尚未验证成功；不能保证所有厂商桌面都支持该组件。
- 会话配置用于减少空闲超时和重启后的重新登录；清除应用数据、Cookie 或服务端会话仍可能需要重新登录。

仓库入口：[ChenJiasen-123/Origin](https://github.com/ChenJiasen-123/Origin)。可用安装包请查看仓库的 [Releases](https://github.com/ChenJiasen-123/Origin/releases)。
