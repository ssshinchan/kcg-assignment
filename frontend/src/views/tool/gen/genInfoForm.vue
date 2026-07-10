<template>
  <el-form ref="genInfoForm" :model="info" :rules="rules" label-width="150px">
    <el-row>
      <el-col :span="12">
        <el-form-item prop="tplCategory">
          <template #label>テンプレートの生成</template>
          <el-select v-model="info.tplCategory" @change="tplSelectChange">
            <el-option label="単一テーブル (追加、削除、変更、クエリ)" value="crud" />
            <el-option label="ツリー テーブル (追加、削除、変更、クエリ)" value="tree" />
            <el-option label="マスターテーブルと子テーブル (追加、削除、変更、クエリ)" value="sub" />
          </el-select>
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item prop="tplWebType">
          <template #label>フロントエンドの種類</template>
          <el-select v-model="info.tplWebType">
            <el-option label="Vue2 Element UIテンプレート" value="element-ui" />
            <el-option label="Vue3 Element プラステンプレート" value="element-plus" />
            <el-option label="Vue3 Element Plus TypeScript テンプレート" value="element-plus-typescript" />
          </el-select>
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item prop="packageName">
          <template #label>
            パッケージパスを生成する
            <el-tooltip content="たとえば、どの Java パッケージが生成されるか edu.kcg.system" placement="top">
              <el-icon><question-filled /></el-icon>
            </el-tooltip>
          </template>
          <el-input v-model="info.packageName" />
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item prop="moduleName">
          <template #label>
            モジュール名の生成
            <el-tooltip content="サブシステム名として理解できます。たとえば、 system" placement="top">
              <el-icon><question-filled /></el-icon>
            </el-tooltip>
          </template>
          <el-input v-model="info.moduleName" />
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item prop="businessName">
          <template #label>
            ビジネス名の生成
            <el-tooltip content="関数の英語名として理解できます。たとえば、 user" placement="top">
              <el-icon><question-filled /></el-icon>
            </el-tooltip>
          </template>
          <el-input v-model="info.businessName" />
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item prop="functionName">
          <template #label>
            関数名の生成
            <el-tooltip content="クラスの説明として使用されます。ユーザー" placement="top">
              <el-icon><question-filled /></el-icon>
            </el-tooltip>
          </template>
          <el-input v-model="info.functionName" />
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item prop="genType">
          <template #label>
            コードの生成方法
            <el-tooltip content="デフォルトでは、zip 圧縮パッケージがダウンロードされます。生成パスをカスタマイズすることもできます" placement="top">
              <el-icon><question-filled /></el-icon>
            </el-tooltip>
          </template>
          <el-radio v-model="info.genType" value="0">zip圧縮パッケージ</el-radio>
          <el-radio v-model="info.genType" value="1">カスタムパス</el-radio>
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item>
          <template #label>
            前のメニュー
            <el-tooltip content="システム管理などの特定のメニューに割り当てられます。" placement="top">
              <el-icon><question-filled /></el-icon>
            </el-tooltip>
          </template>
          <el-tree-select
            v-model="info.parentMenuId"
            :data="menuOptions"
            :props="{ value: 'menuId', label: 'menuName', children: 'children' }"
            value-key="menuId"
            placeholder="システムメニューを選択してください"
            check-strictly
          />
        </el-form-item>
      </el-col>

      <el-col :span="24" v-if="info.genType == '1'">
        <el-form-item prop="genPath">
          <template #label>
            カスタムパス
            <el-tooltip content="ディスクへの絶対パスを入力します。入力しない場合は、現在の Web プロジェクトの下に生成されます。" placement="top">
              <el-icon><question-filled /></el-icon>
            </el-tooltip>
          </template>
          <el-input v-model="info.genPath">
            <template #append>
              <el-dropdown>
                <el-button type="primary">
                  最近のルートを簡単に選択
                  <i class="el-icon-arrow-down el-icon--right"></i>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="info.genPath = '/'">デフォルトのビルドベースパスを復元する</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </el-input>
        </el-form-item>
      </el-col>
    </el-row>
    
    <template v-if="info.tplCategory == 'tree'">
      <h4 class="form-header">その他の情報</h4>
      <el-row v-show="info.tplCategory == 'tree'">
        <el-col :span="12">
          <el-form-item>
            <template #label>
              ツリーエンコードフィールド
              <el-tooltip content="ツリーに表示されるエンコードされたフィールド名。次のようになります。dept_id" placement="top">
                <el-icon><question-filled /></el-icon>
              </el-tooltip>
            </template>
            <el-select v-model="info.treeCode" placeholder="選択してください">
              <el-option
                v-for="(column, index) in info.columns"
                :key="index"
                :label="column.columnName + ':' + column.columnComment"
                :value="column.columnName"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item>
            <template #label>
              ツリーの親エンコードフィールド
              <el-tooltip content="ツリーに表示される親エンコーディング フィールド名。次のようになります。parent_Id" placement="top">
                <el-icon><question-filled /></el-icon>
              </el-tooltip>
            </template>
            <el-select v-model="info.treeParentCode" placeholder="選択してください">
              <el-option
                v-for="(column, index) in info.columns"
                :key="index"
                :label="column.columnName + ':' + column.columnComment"
                :value="column.columnName"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item>
            <template #label>
              ツリー名フィールド
              <el-tooltip content="ツリー ノードの表示名フィールド名。例:dept_name" placement="top">
                <el-icon><question-filled /></el-icon>
              </el-tooltip>
            </template>
            <el-select v-model="info.treeName" placeholder="選択してください">
              <el-option
                v-for="(column, index) in info.columns"
                :key="index"
                :label="column.columnName + ':' + column.columnComment"
                :value="column.columnName"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </template>

    <template v-if="info.tplCategory == 'sub'">
      <h4 class="form-header">関連情報</h4>
      <el-row>
        <el-col :span="12">
          <el-form-item>
            <template #label>
              関連付けられたサブテーブルのテーブル名
              <el-tooltip content="関連するサブテーブルのテーブル名。例:sys_user" placement="top">
                <el-icon><question-filled /></el-icon>
              </el-tooltip>
            </template>
            <el-select v-model="info.subTableName" placeholder="選択してください" @change="subSelectChange">
              <el-option
                v-for="(table, index) in tables"
                :key="index"
                :label="table.tableName + ':' + table.tableComment"
                :value="table.tableName"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item>
            <template #label>
              サブテーブルに関連付けられた外部キー名
              <el-tooltip content="サブテーブルに関連付けられた外部キー名。たとえば、次のとおりです。user_id" placement="top">
                <el-icon><question-filled /></el-icon>
              </el-tooltip>
            </template>
            <el-select v-model="info.subTableFkName" placeholder="選択してください">
              <el-option
                v-for="(column, index) in subColumns"
                :key="index"
                :label="column.columnName + ':' + column.columnComment"
                :value="column.columnName"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </template>

  </el-form>
</template>

<script setup>
import { listMenu } from "@/api/system/menu"

const subColumns = ref([])
const menuOptions = ref([])
const { proxy } = getCurrentInstance()

const props = defineProps({
  info: {
    type: Object,
    default: null
  },
  tables: {
    type: Array,
    default: null
  }
})

// 表单校验
const rules = ref({
  tplCategory: [{ required: true, message: "生成テンプレートを選択してください", trigger: "blur" }],
  packageName: [{ required: true, message: "生成されたパッケージへのパスを入力してください", trigger: "blur" }],
  moduleName: [{ required: true, message: "生成されたモジュール名を入力してください", trigger: "blur" }],
  businessName: [{ required: true, message: "生成されたビジネス名を入力してください", trigger: "blur" }],
  functionName: [{ required: true, message: "生成された関数名を入力してください", trigger: "blur" }]
})

function subSelectChange(value) {
  props.info.subTableFkName = ""
}

function tplSelectChange(value) {
  if (value !== "sub") {
    props.info.subTableName = ""
    props.info.subTableFkName = ""
  }
}

function setSubTableColumns(value) {
  for (var item in props.tables) {
    const name = props.tables[item].tableName
    if (value === name) {
      subColumns.value = props.tables[item].columns
      break
    }
  }
}

/** 查询菜单下拉树结构 */
function getMenuTreeselect() {
  listMenu().then(response => {
    menuOptions.value = proxy.handleTree(response.data, "menuId")
  })
}

onMounted(() => {
  getMenuTreeselect()
})

watch(() => props.info.subTableName, val => {
  setSubTableColumns(val)
})

watch(() => props.info.tplWebType, val => {
  if (val === '') {
    props.info.tplWebType = "element-plus"
  }
})
</script>
