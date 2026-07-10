- 用n3水平的日语
- 仅仅关注下面的功能,结合项目里代码分析和介绍:
  AIチャット(用langchain4j连接大模型, 可以使用企业私有大模型)
  システム管理
  ユーザー管理
  ロール管理
  メニュー管理
  部門管理
  役職管理
  辞書管理
  パラメータ設定
  お知らせ・通知
  ログ管理
  操作ログ
  ログイン履歴
  システム監視
  オンラインユーザー
  データ監視(Druid)
  サービス監視
  キャッシュ監視
  キャッシュ一覧
- 后端使用docker, 镜像上传到AWS Elastic Container Registry, 容器部署在EC2, Nginx反向代理, 使用免费的Let's Encrypt证书, 域名放在cloudflare上
- 数据库使用AWS RDS MySQL
- 前端使用Vue3 + Vite + TypeScript, 部署在AWS Amplify
- 使用Github Actions进行CI/CD, 提交代码自动构建和部署前后端
- 生成10分钟的演讲ppt的markdown文字版, 说明每页的标题和要点, 用于后面制作ppt