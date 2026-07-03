<template>
  <!-- 创建表 -->
  <el-dialog title="テーブルの作成" v-model="visible" width="800px" top="5vh" append-to-body>
    <span>テーブル作成ステートメント(複数のテーブル作成ステートメントをサポート):</span>
    <el-input type="textarea" :rows="10" placeholder="テキストを入力してください" v-model="content"></el-input>
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="handleImportTable">OK</el-button>
        <el-button @click="visible = false">キャンセル</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { createTable } from "@/api/tool/gen"

const visible = ref(false)
const content = ref("")
const { proxy } = getCurrentInstance()
const emit = defineEmits(["ok"])

/** 显示弹框 */
function show() {
  visible.value = true
}

/** 导入按钮操作 */
function handleImportTable() {
  if (content.value === "") {
    proxy.$modal.msgError("テーブル作成ステートメントを入力してください")
    return
  }
  createTable({ sql: content.value, tplWebType: 'element-plus' }).then(res => {
    proxy.$modal.msgSuccess(res.msg)
    if (res.code === 200) {
      visible.value = false
      emit("ok")
    }
  })
}

defineExpose({
  show,
})
</script>
