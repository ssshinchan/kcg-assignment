<template>
   <el-form ref="pwdRef" :model="user" :rules="rules" label-width="120px">
      <el-form-item label="古いパスワード" prop="oldPassword">
         <el-input v-model="user.oldPassword" placeholder="古いパスワードを入力してください" type="password" show-password />
      </el-form-item>
      <el-form-item label="新しいパスワード" prop="newPassword">
         <el-input v-model="user.newPassword" placeholder="新しいパスワードを入力してください" type="password" show-password />
      </el-form-item>
      <el-form-item label="パスワードの確認" prop="confirmPassword">
         <el-input v-model="user.confirmPassword" placeholder="新しいパスワードを確認してください" type="password" show-password/>
      </el-form-item>
      <el-form-item>
      <el-button type="primary" @click="submit">保存する</el-button>
      <el-button type="danger" @click="close">閉じる</el-button>
      </el-form-item>
   </el-form>
</template>

<script setup>
import { updateUserPwd } from "@/api/system/user"

const { proxy } = getCurrentInstance()

const user = reactive({
  oldPassword: undefined,
  newPassword: undefined,
  confirmPassword: undefined
})

const equalToPassword = (rule, value, callback) => {
  if (user.newPassword !== value) {
    callback(new Error("2回入力したパスワードは一致しません"))
  } else {
    callback()
  }
}

const rules = ref({
  oldPassword: [{ required: true, message: "古いパスワードを空にすることはできません", trigger: "blur" }],
  newPassword: [{ required: true, message: "新しいパスワードを空にすることはできません", trigger: "blur" }, { min: 6, max: 20, message: "長さは 6 ～ 20 文字です", trigger: "blur" }, { pattern: /^[^<>"'|\\]+$/, message: "不正な文字を含めることはできません:< > \" ' \\\ |", trigger: "blur" }],
  confirmPassword: [{ required: true, message: "パスワードの確認を空にすることはできません", trigger: "blur" }, { required: true, validator: equalToPassword, trigger: "blur" }]
})

/** 提交按钮 */
function submit() {
  proxy.$refs.pwdRef.validate(valid => {
    if (valid) {
      updateUserPwd(user.oldPassword, user.newPassword).then(() => {
        proxy.$modal.msgSuccess("変更が成功しました")
      })
    }
  })
}

/** 关闭按钮 */
function close() {
  proxy.$tab.closePage()
}
</script>
