<template>
  <div class="app-container home">
    <el-row :gutter="40">
      <!-- プロジェクト紹介 -->
      <el-col :sm="24" :lg="12">
        <h2>管理システム</h2>
        <p class="description">
          この管理システムは、Spring Boot、Spring Security、Vue、Element-uiを採用した、フロントエンドとバックエンドが分離されたオープンソースの迅速な開発プラットフォームです。Webサイト管理、CMS、CRM、OAなど、あらゆるWebアプリケーションの開発基盤として利用可能で、高度なカスタマイズ性と優れた操作性を提供します。
        </p>
      </el-col>

      <!-- 技術スタック -->
      <el-col :sm="24" :lg="12">
        <h2>技術スタック</h2>
        <el-row :gutter="20">
          <el-col :span="12">
            <h4>バックエンド</h4>
            <ul class="tech-list">
              <li>SpringBoot</li>
              <li>Spring Security</li>
              <li>JWT</li>
              <li>MyBatis</li>
              <li>Druid</li>
              <li>Fastjson</li>
            </ul>
          </el-col>
          <el-col :span="12">
            <h4>フロントエンド</h4>
            <ul class="tech-list">
              <li>Vue</li>
              <li>Vuex</li>
              <li>Element-ui</li>
              <li>Axios</li>
              <li>Sass</li>
              <li>Quill</li>
            </ul>
          </el-col>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Index">
// ロジックなし
</script>

<style scoped lang="scss">
.home {
  padding: 40px;
  font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
  color: #303133;

  h2 {
    font-size: 22px;
    font-weight: 600;
    margin-top: 0;
    margin-bottom: 20px;
    color: #303133;
    border-bottom: 2px solid #409eff;
    padding-bottom: 8px;
    display: inline-block;
  }

  .description {
    font-size: 14px;
    line-height: 1.8;
    color: #606266;
    text-align: justify;
    margin: 0;
  }

  h4 {
    font-size: 16px;
    font-weight: 600;
    margin-top: 10px;
    margin-bottom: 10px;
    color: #909399;
  }

  .tech-list {
    padding-left: 20px;
    list-style-type: disc;
    margin: 0;
    line-height: 2;
    color: #606266;
    font-size: 14px;
  }
}
</style>
