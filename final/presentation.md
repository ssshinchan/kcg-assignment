# 権限管理システムの紹介

- 発表時間：約6分
- 日本語レベル：N3程度
- スライド数：11枚
- メンバー: M25W7497 瀋普銘, M24W7300 孫易龍, M25W7111 王梓竜
- ウェブサイト：https://assignment.m25w7497.me/
- GitHubリポジトリ：https://github.com/ssshinchan/kcg-assignment

---

## 1ページ：システムの概要

### 要点

- 社内業務を支援する管理システム
- AIチャット、システム管理、システム監視を統合
- フロントエンドとバックエンドを分けた構成
- AWSとGitHub Actionsを使った運用

### 発表原稿（約25秒）

みなさん、こんにちは。今日は、私が開発した管理システムを紹介します。
このシステムは、AIチャット、システム管理、システム監視の三つを一つにまとめたものです。
フロントエンドとバックエンドを分けて開発し、AWS上で運用します。
また、GitHub Actionsでデプロイを自動化しています 。

---

## 2ページ：システム全体の構成

### 要点

```text
利用者
  ↓ HTTPS
Cloudflareのドメイン
  ├─ AWS Amplify → フロントエンド
  └─ Nginx → EC2上のDocker → Spring Boot
                                  ├─ AWS RDS MySQL
                                  ├─ Redis
                                  └─ LangChain4j → AIモデル
```

- フロントエンド：Vue 3、Vite、Element Plus、Pinia
- バックエンド：Spring Boot、Spring Security、MyBatis
- データベース：AWS RDS for MySQL
- AI接続：LangChain4j

### 発表原稿（約35秒）

これはシステム全体の構成です。
利用者はブラウザーからHTTPSでアクセスします。フロントエンドはAWS Amplifyに置き、バックエンドはEC2上のDockerコンテナで動かします。
NginxはリクエストをSpring Bootへ送ります。データはRDSのMySQLに保存し、ログイン情報とキャッシュにはRedisを使います。
AIチャットはLangChain4jを通してAIモデルに接続します。

---

## 3ページ：AIチャットの機能

### 要点

- 新しい会話の作成、一覧、名前変更、削除
- 過去のメッセージをMySQLに保存
- SSEでAIの回答を少しずつ表示
- ログインユーザーごとに会話を管理
- API：`/ai/chat/conversations`、`/ai/chat/stream`

### 発表原稿（約40秒）

AIチャットでは、新しい会話の作成、一覧表示、名前の変更、削除ができます。
質問を送ると、まず内容をMySQLに保存してから、バックエンドがAIモデルを呼び出します。
回答にはSSEを使っています。そのため、すべての回答が完成する前から、文章を少しずつ画面に表示できます。
また、ログイン中のユーザーIDを使うため、ユーザーごとに自分の会話を管理できます。

---

## 4ページ：LangChain4jとAIモデル

### 要点

- `StreamingChatLanguageModel`という共通インターフェースを使用
- DeepSeek、OpenAI、DashScopeに対応
- APIキー、モデル名、温度などを設定で管理
- OpenAI互換APIを持つ企業モデルにも拡張しやすい

### 発表原稿（約35秒）

バックエンドではLangChain4jを使っています。
これは、AIモデル会社ごとの差を小さくするライブラリです。現在はDeepSeek、OpenAI、DashScopeを選択できます。
APIキーは環境変数から読み、モデル名や回答の長さは設定ファイルで管理します。
この設計により、将来は企業向けのAIモデルにも拡張しやすくなります。ただし、専用URLや独自認証を使う場合は追加実装が必要です。

---

## 5ページ：ユーザー・ロール・メニュー管理

### 要点

- ユーザー：アカウント、部門、役職、状態を管理
- ロール：権限のグループを作成
- メニュー：画面、ボタン、ルート、権限文字列を管理
- Spring SecurityでAPI権限を確認
- ログイン後に利用可能な画面だけを表示

### 発表原稿（約35秒）

次はシステム管理機能です。
ユーザー管理では、アカウント、部門、役職、利用状態を管理します。ロール管理では、管理者や一般ユーザーなどの権限グループを作ります。
メニュー管理では、画面だけでなく、追加、変更、削除などのボタン権限も設定できます。
バックエンドはSpring SecurityでAPIの権限を確認し、フロントエンドは利用できる画面だけを表示します。

---

## 6ページ：組織と共通データの管理

### 要点

- 部門管理：会社やチームをツリー形式で管理
- 役職管理：役職と表示順を管理
- 辞書管理：状態や種類などの共通選択肢を管理
- パラメータ設定：設定値をキーと値で管理
- お知らせ・通知を管理

### 発表原稿（約30秒）

組織に関する機能もあります。
部門管理では、会社、部署、チームの関係をツリー形式で管理します。役職管理では、社員の役職と表示順を設定します。
辞書管理は、状態や種類など、多くの画面で使う共通データを管理する機能です。
パラメータ設定やお知らせ機能もあるため、コードを変更しなくても、業務に必要な情報を管理できます。

---

## 7ページ：ログ管理

### 要点

- 操作ログ：誰が、いつ、どの機能を使ったかを記録
- ログイン履歴：ユーザー名、IP、結果、時刻を記録
- `@Log`とAOPで操作を自動記録
- ログの検索、削除、エクスポートに対応

### 発表原稿（約30秒）

ログ管理には、操作ログとログイン履歴があります。
操作ログには、誰が、いつ、どの機能を使ったかを記録します。追加、変更、削除などの操作は、`@Log`とAOPを使って自動で保存します。
ログイン履歴には、ユーザー名、IPアドレス、ログイン結果、時刻を保存します。
問題が起きた時に原因を調べやすく、セキュリティ管理にも役立ちます。

---

## 8ページ：システム監視

### 要点

- オンラインユーザー：Redisのログインセッションを表示
- 管理者は指定ユーザーを強制ログアウト可能
- DruidでSQL、接続数、遅いSQLを確認
- CPU、メモリ、JVM、ディスクを確認
- 監視機能にも権限チェックを設定

### 発表原稿（約35秒）

システム監視では、現在の利用状態を確認できます。
オンラインユーザー画面はRedisのログイン情報を読み、管理者は必要な場合に強制ログアウトできます。
Druidでは、データベース接続数、実行されたSQL、遅いSQLを確認できます。
さらに、CPU、メモリ、JVM、ディスクの状態も表示します。これらの監視機能も、許可された管理者だけが利用できます。

---

## 9ページ：キャッシュ監視とキャッシュ一覧

### 要点

- Redisのバージョン、メモリ、接続数、キー数を表示
- Redisコマンドの実行回数をグラフで表示
- ログイントークン、設定、辞書、認証コードなどを分類
- 指定キーや分類ごとにキャッシュを削除可能

### 発表原稿（約25秒）

キャッシュ監視では、Redisのバージョン、使用メモリ、接続数、キー数を確認できます。
また、よく使われるRedisコマンドをグラフで表示します。
キャッシュ一覧では、ログイントークン、システム設定、辞書、認証コードなどを分類して確認できます。
必要なキャッシュだけを削除できますが、ログイン情報も消える可能性があるため、慎重な操作が必要です。

---

## 10ページ：AWSへのデプロイとCI/CD

### 要点

- Java 25のマルチステージDockerイメージを作成
- GitHub Actionsが`master`へのpushを検知
- DockerイメージをAmazon ECR Publicへpush
- SSHでEC2に接続し、最新コンテナを起動
- Nginx、Let's Encrypt、CloudflareでHTTPSを提供
- フロントエンドはAWS Amplifyでビルド・公開

### 発表原稿（約40秒）

次に、デプロイの仕組みを説明します。
バックエンドはJava 25のDockerイメージとして作成します。`master`ブランチへpushすると、GitHub Actionsがビルドを開始し、イメージをAmazon ECR Publicへ送ります。
その後、SSHでEC2へ接続し、最新のコンテナを起動します。
NginxとLet's EncryptでHTTPSを提供し、CloudflareでドメインとDNSを管理します。フロントエンドはAWS Amplifyでビルドして公開します。

---

## 11ページ：まとめと今後の改善

### 要点

#### 実現したこと

- AIチャットと管理機能を一つのシステムに統合
- ユーザー、ロール、メニューによる細かい権限管理
- ログ、サーバー、DB、Redisをまとめて監視
- Docker、ECR、EC2、GitHub Actionsによる自動デプロイ

#### 今後の改善

- 企業プライベートモデル用の接続URLと認証方式を設定可能にする
- AI会話削除時の所有権確認を強くする
- AI用テーブルとメニューの初期SQLを追加する
- Kubernetes運用とTerraformによるAWS設定のコード化を検討する

### 発表原稿（約30秒）

最後にまとめます。
このシステムでは、AIチャットと一般的な管理機能を統合しました。ユーザー、ロール、メニューによる細かい権限管理ができ、ログ、サーバー、データベース、Redisもまとめて監視できます。
また、Docker、ECR、EC2、GitHub Actionsを使ってデプロイを自動化しました。
今後はAI機能のセキュリティを強化し、企業向けAIモデルへの対応と、AWS設定のコード化を進めたいです。以上です。ご清聴ありがとうございました。

---

# コード分析メモ（PPTには必要な部分だけ使用）

## AIチャット

- LangChain4j依存：`assignment-ai/pom.xml:20`
- モデル設定：`assignment-ai/src/main/java/edu/kcg/system/config/AiModelConfig.java:40`
- AI設定値：`assignment-admin/src/main/resources/application.yml:153`
- 会話APIとSSE：`assignment-ai/src/main/java/edu/kcg/system/controller/AiChatController.java:37`
- 会話保存とモデル呼び出し：`assignment-ai/src/main/java/edu/kcg/system/service/impl/AiChatServiceImpl.java:55`
- フロント画面：`frontend/src/views/ai/chat.vue:1`

## システム管理・監視

- ユーザー管理：`assignment-admin/src/main/java/edu/kcg/web/controller/system/SysUserController.java`
- ロール管理：`assignment-admin/src/main/java/edu/kcg/web/controller/system/SysRoleController.java`
- メニュー管理：`assignment-admin/src/main/java/edu/kcg/web/controller/system/SysMenuController.java`
- 部門・役職・辞書・設定・通知：`assignment-admin/src/main/java/edu/kcg/web/controller/system/`
- 操作ログ・ログイン履歴：`assignment-admin/src/main/java/edu/kcg/web/controller/monitor/`
- オンラインユーザー：`assignment-admin/src/main/java/edu/kcg/web/controller/monitor/SysUserOnlineController.java:41`
- サービス監視：`assignment-admin/src/main/java/edu/kcg/web/controller/monitor/ServerController.java:19`
- キャッシュ監視：`assignment-admin/src/main/java/edu/kcg/web/controller/monitor/CacheController.java:49`
- Druid監視：`assignment-admin/src/main/resources/application-druid.yml:42`

## デプロイ

- Dockerビルド：`Dockerfile:1`
- ECR・EC2自動デプロイ：`.github/workflows/deploy-backend.yml:1`
- RDS接続用環境変数：`assignment-admin/src/main/resources/application-druid.yml:9`
- 本番API：`frontend/.env.production:8`

## 実装状況に関する注意

- 現在のフロントエンド実装はVue 3とViteですが、主要ファイルはJavaScriptです。TypeScriptへの完全移行は未完了です。
- リポジトリにはバックエンド用GitHub Actionsがあります。AmplifyはAWS側の連携設定で自動デプロイできますが、`amplify.yml`はありません。
- Nginx、Let's Encrypt、Cloudflareの設定ファイルはリポジトリ内にありません。これらはサーバー側・外部サービス側の設定です。
- AIモデルの接続先は現在コード内で定義されています。任意の企業プライベートモデルを使うには、接続URLや認証方式の追加対応が必要です。
