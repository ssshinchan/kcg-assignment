<template>
  <el-dialog v-model="open" width="500px" title="ビルドタイプを選択してください" @open="onOpen" @close="onClose">
    <el-form ref="codeTypeForm" :model="formData" :rules="rules" label-width="100px">
      <el-form-item label="生成タイプ" prop="type">
        <el-radio-group v-model="formData.type">
          <el-radio-button v-for="(item, index) in typeOptions" :key="index" :label="item.value">
            {{ item.label }}
          </el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item v-if="showFileName" label="ファイル名" prop="fileName">
        <el-input v-model="formData.fileName" placeholder="ファイル名を入力してください" clearable />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="onClose">キャンセル</el-button>
      <el-button type="primary" @click="handelConfirm">OK</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
const open = defineModel()
const props = defineProps({
  showFileName: Boolean
})
const emit = defineEmits(['confirm'])
const formData = ref({
  fileName: undefined,
  type: 'file'
})
const codeTypeForm = ref()
const rules = {
  fileName: [{
    required: true,
    message: 'ファイル名を入力してください',
    trigger: 'blur'
  }],
  type: [{
    required: true,
    message: '生成された型を空にすることはできません',
    trigger: 'change'
  }]
}
const typeOptions = ref([
  {
    label: 'ページ',
    value: 'file'
  },
  {
    label: 'ポップアップウィンドウ',
    value: 'dialog'
  }
])
function onOpen() {
  if (props.showFileName) {
    formData.value.fileName = `${+new Date()}.vue`
  }
}
function onClose() {
  open.value = false
}
function handelConfirm() {
  codeTypeForm.value.validate(valid => {
    if (!valid) return
    emit('confirm', { ...formData.value })
    onClose()
  })
}
</script>