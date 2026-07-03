<template>
   <el-form ref="userRef" :model="form" :rules="rules" label-width="120px">
      <el-form-item label="ユーザーのニックネーム" prop="nickName">
         <el-input v-model="form.nickName" maxlength="30" />
      </el-form-item>
      <el-form-item label="携帯電話番号" prop="phonenumber">
         <el-input v-model="form.phonenumber" maxlength="11" />
      </el-form-item>
      <el-form-item label="電子メール" prop="email">
         <el-input v-model="form.email" maxlength="50" />
      </el-form-item>
      <el-form-item label="性別">
         <el-radio-group v-model="form.sex">
            <el-radio value="0">男性</el-radio>
            <el-radio value="1">女性</el-radio>
         </el-radio-group>
      </el-form-item>
      <el-form-item>
      <el-button type="primary" @click="submit">保存する</el-button>
      <el-button type="danger" @click="close">閉じる</el-button>
      </el-form-item>
   </el-form>
</template>

<script setup>
import { updateUserProfile } from "@/api/system/user"

const props = defineProps({
  user: {
    type: Object
  }
})

const { proxy } = getCurrentInstance()

const form = ref({})
const rules = ref({
  nickName: [{ required: true, message: "ユーザーニックネームを空にすることはできません", trigger: "blur" }],
  email: [{ required: true, message: "メールアドレスを空にすることはできません", trigger: "blur" }, { type: "email", message: "正しいメールアドレスを入力してください", trigger: ["blur", "change"] }],
  phonenumber: [{ required: true, message: "携帯電話番号を空白にすることはできません", trigger: "blur" }, { pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: "正しい携帯電話番号を入力してください", trigger: "blur" }],
})

/** 提交按钮 */
function submit() {
  proxy.$refs.userRef.validate(valid => {
    if (valid) {
      updateUserProfile(form.value).then(() => {
        proxy.$modal.msgSuccess("変更が成功しました")
        props.user.phonenumber = form.value.phonenumber
        props.user.email = form.value.email
      })
    }
  })
}

/** 关闭按钮 */
function close() {
  proxy.$tab.closePage()
}

// 回显当前登录用户信息
watch(() => props.user, user => {
  if (user) {
    form.value = { nickName: user.nickName, phonenumber: user.phonenumber, email: user.email, sex: user.sex }
  }
},{ immediate: true })
</script>
