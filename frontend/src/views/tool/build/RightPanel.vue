<template>
  <div class="right-board">
    <el-tabs v-model="currentTab" stretch class="center-tabs">
      <el-tab-pane label="コンポーネントのプロパティ" name="field" />
      <el-tab-pane label="フォームのプロパティ" name="form" />
    </el-tabs>
    <div class="field-box">
      <a class="document-link" target="_blank" :href="documentLink" title="コンポーネントのドキュメントを表示する">
        <el-icon>
          <Link />
        </el-icon>
      </a>
      <el-scrollbar class="right-scrollbar">
        <!-- 组件属性 -->
        <el-form v-show="currentTab === 'field' && showField" size="default" label-width="90px" label-position="top"
          style="">
          <el-form-item v-if="activeData.changeTag" label="コンポーネントの種類">
            <el-select v-model="activeData.tagIcon" placeholder="コンポーネントのタイプを選択してください" :style="{ width: '100%' }" @change="tagChange">
              <el-option-group v-for="group in tagList" :key="group.label" :label="group.label">
                <el-option v-for="item in group.options" :key="item.label" :label="item.label" :value="item.tagIcon">
                  <svg-icon class="node-icon" :icon-class="item.tagIcon" style="margin-right: 10px;" />
                  <span> {{ item.label }}</span>
                </el-option>
              </el-option-group>
            </el-select>
          </el-form-item>
          <el-form-item v-if="activeData.vModel !== undefined" label="フィールド名">
            <el-input v-model="activeData.vModel" placeholder="フィールド名 (v-model) を入力してください" />
          </el-form-item>
          <el-form-item v-if="activeData.componentName !== undefined" label="コンポーネント名">
            {{ activeData.componentName }}
          </el-form-item>
          <el-form-item v-if="activeData.label !== undefined" label="タイトル">
            <el-input v-model="activeData.label" placeholder="タイトルを入力してください" />
          </el-form-item>
          <el-form-item v-if="activeData.placeholder !== undefined" label="プレースホルダーリマインダー">
            <el-input v-model="activeData.placeholder" placeholder="プレースホルダーのヒントを入力してください" />
          </el-form-item>
          <el-form-item v-if="activeData['start-placeholder'] !== undefined" label="スペースを占有し始める">
            <el-input v-model="activeData['start-placeholder']" placeholder="プレースホルダーのヒントを入力してください" />
          </el-form-item>
          <el-form-item v-if="activeData['end-placeholder'] !== undefined" label="プレースホルダーの終わり">
            <el-input v-model="activeData['end-placeholder']" placeholder="プレースホルダーのヒントを入力してください" />
          </el-form-item>
          <el-form-item v-if="activeData.span !== undefined" label="フォームグリッド">
            <el-slider v-model="activeData.span" :max="24" :min="1" :marks="{ 12: '' }" @change="spanChange" />
          </el-form-item>
          <el-form-item v-if="activeData.layout === 'rowFormItem'" label="グリッド間隔">
            <el-input-number v-model="activeData.gutter" :min="0" placeholder="グリッド間隔" />
          </el-form-item>

          <el-form-item v-if="activeData.justify !== undefined" label="横に並べる">
            <el-select v-model="activeData.justify" placeholder="横配置を選択してください" :style="{ width: '100%' }">
              <el-option v-for="(item, index) in justifyOptions" :key="index" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item v-if="activeData.align !== undefined" label="縦配置">
            <el-radio-group v-model="activeData.align">
              <el-radio-button label="top" />
              <el-radio-button label="middle" />
              <el-radio-button label="bottom" />
            </el-radio-group>
          </el-form-item>
          <el-form-item v-if="activeData.labelWidth !== undefined" label="ラベル幅">
            <el-input v-model.number="activeData.labelWidth" type="number" placeholder="ラベルの幅を入力してください" />
          </el-form-item>
          <el-form-item v-if="activeData.style && activeData.style.width !== undefined" label="コンポーネントの幅">
            <el-input v-model="activeData.style.width" placeholder="コンポーネントの幅を入力してください" clearable />
          </el-form-item>
          <el-form-item v-if="activeData.vModel !== undefined" label="デフォルト値">
            <el-input :value="setDefaultValue(activeData.defaultValue)" placeholder="デフォルト値を入力してください"
              @input="onDefaultValueInput" />
          </el-form-item>
          <el-form-item v-if="activeData.tag === 'el-checkbox-group'" label="少なくとも選択する必要があります">
            <el-input-number :value="activeData.min" :min="0" placeholder="少なくとも選択する必要があります"
              @input="$set(activeData, 'min', $event ? $event : undefined)" />
          </el-form-item>
          <el-form-item v-if="activeData.tag === 'el-checkbox-group'" label="せいぜいオプション">
            <el-input-number :value="activeData.max" :min="0" placeholder="せいぜいオプション"
              @input="$set(activeData, 'max', $event ? $event : undefined)" />
          </el-form-item>
          <el-form-item v-if="activeData.prepend !== undefined" label="接頭辞">
            <el-input v-model="activeData.prepend" placeholder="プレフィックスを入力してください" />
          </el-form-item>
          <el-form-item v-if="activeData.append !== undefined" label="接尾語">
            <el-input v-model="activeData.append" placeholder="サフィックスを入力してください" />
          </el-form-item>
          <el-form-item v-if="activeData['prefix-icon'] !== undefined" label="フロントアイコン">
            <el-input v-model="activeData['prefix-icon']" placeholder="以前のアイコン名を入力してください">
              <template #append>
                <el-button icon="Pointer" @click="openIconsDialog('prefix-icon')">
                  選択してください
                </el-button>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item v-if="activeData['suffix-icon'] !== undefined" label="戻るアイコン">
            <el-input v-model="activeData['suffix-icon']" placeholder="アイコン名を入力してください">
              <template #append>
                <el-button icon="Pointer" @click="openIconsDialog('suffix-icon')">
                  選択してください
                </el-button>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item v-if="activeData.tag === 'el-cascader'" label="オプション区切り文字">
            <el-input v-model="activeData.separator" placeholder="オプションの区切り文字を入力してください" />
          </el-form-item>
          <el-form-item v-if="activeData.autosize !== undefined" label="最小行数">
            <el-input-number v-model="activeData.autosize.minRows" :min="1" placeholder="最小行数" />
          </el-form-item>
          <el-form-item v-if="activeData.autosize !== undefined" label="最大行数">
            <el-input-number v-model="activeData.autosize.maxRows" :min="1" placeholder="最大行数" />
          </el-form-item>
          <el-form-item v-if="activeData.min !== undefined" label="最小値">
            <el-input-number v-model="activeData.min" placeholder="最小値" />
          </el-form-item>
          <el-form-item v-if="activeData.max !== undefined" label="最大値">
            <el-input-number v-model="activeData.max" placeholder="最大値" />
          </el-form-item>
          <el-form-item v-if="activeData.step !== undefined" label="ステップサイズ">
            <el-input-number v-model="activeData.step" placeholder="歩数" />
          </el-form-item>
          <el-form-item v-if="activeData.tag === 'el-input-number'" label="精度">
            <el-input-number v-model="activeData.precision" :min="0" placeholder="精度" />
          </el-form-item>
          <el-form-item v-if="activeData.tag === 'el-input-number'" label="ボタンの位置">
            <el-radio-group v-model="activeData['controls-position']">
              <el-radio-button label="">
                デフォルト
              </el-radio-button>
              <el-radio-button label="right">
                右側
              </el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item v-if="activeData.maxlength !== undefined" label="最大入力">
            <el-input v-model="activeData.maxlength" placeholder="文字の長さを入力してください">
              <template slot="append">
                キャラクター
              </template>
            </el-input>
          </el-form-item>
          <el-form-item v-if="activeData['active-text'] !== undefined" label="プロンプトをオンにする">
            <el-input v-model="activeData['active-text']" placeholder="アクティベーションプロンプトを入力してください" />
          </el-form-item>
          <el-form-item v-if="activeData['inactive-text'] !== undefined" label="プロンプトを閉じる">
            <el-input v-model="activeData['inactive-text']" placeholder="終了プロンプトを入力してください" />
          </el-form-item>
          <el-form-item v-if="activeData['active-value'] !== undefined" label="開値">
            <el-input :value="setDefaultValue(activeData['active-value'])" placeholder="有効にする値を入力してください"
              @input="onSwitchValueInput($event, 'active-value')" />
          </el-form-item>
          <el-form-item v-if="activeData['inactive-value'] !== undefined" label="終値">
            <el-input :value="setDefaultValue(activeData['inactive-value'])" placeholder="終値を入力してください"
              @input="onSwitchValueInput($event, 'inactive-value')" />
          </el-form-item>
          <el-form-item v-if="activeData.type !== undefined && 'el-date-picker' === activeData.tag" label="時間タイプ">
            <el-select v-model="activeData.type" placeholder="時間タイプを選択してください" :style="{ width: '100%' }"
              @change="dateTypeChange">
              <el-option v-for="(item, index) in dateOptions" :key="index" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item v-if="activeData.name !== undefined" label="ファイルフィールド名">
            <el-input v-model="activeData.name" placeholder="アップロードファイルのフィールド名を入力してください" />
          </el-form-item>
          <el-form-item v-if="activeData.accept !== undefined" label="ファイルの種類">
            <el-select v-model="activeData.accept" placeholder="ファイルの種類を選択してください" :style="{ width: '100%' }" clearable>
              <el-option label="写真" value="image/*" />
              <el-option label="ビデオ" value="video/*" />
              <el-option label="オーディオ" value="audio/*" />
              <el-option label="excel" value=".xls,.xlsx" />
              <el-option label="word" value=".doc,.docx" />
              <el-option label="pdf" value=".pdf" />
              <el-option label="txt" value=".txt" />
            </el-select>
          </el-form-item>
          <el-form-item v-if="activeData.fileSize !== undefined" label="ファイルサイズ">
            <el-input v-model.number="activeData.fileSize" placeholder="ファイルサイズを入力してください">
              <el-select slot="append" v-model="activeData.sizeUnit" :style="{ width: '66px' }">
                <el-option label="KB" value="KB" />
                <el-option label="MB" value="MB" />
                <el-option label="GB" value="GB" />
              </el-select>
            </el-input>
          </el-form-item>
          <el-form-item v-if="activeData.action !== undefined" label="アップロードアドレス">
            <el-input v-model="activeData.action" placeholder="アップロードアドレスを入力してください" clearable />
          </el-form-item>
          <el-form-item v-if="activeData['list-type'] !== undefined" label="リストタイプ">
            <el-radio-group v-model="activeData['list-type']" size="small">
              <el-radio-button label="text">
                text
              </el-radio-button>
              <el-radio-button label="picture">
                picture
              </el-radio-button>
              <el-radio-button label="picture-card">
                picture-card
              </el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item v-if="activeData.buttonText !== undefined" v-show="'picture-card' !== activeData['list-type']"
            label="ボタンのテキスト">
            <el-input v-model="activeData.buttonText" placeholder="ボタンのテキストを入力してください" />
          </el-form-item>
          <el-form-item v-if="activeData['range-separator'] !== undefined" label="区切り文字">
            <el-input v-model="activeData['range-separator']" placeholder="区切り文字を入力してください" />
          </el-form-item>
          <el-form-item v-if="activeData['picker-options'] !== undefined" label="期間">
            <el-input v-model="activeData['picker-options'].selectableRange" placeholder="期間を入力してください" />
          </el-form-item>
          <el-form-item v-if="activeData.format !== undefined" label="時間形式">
            <el-input :value="activeData.format" placeholder="時刻形式を入力してください" @input="setTimeValue($event)" />
          </el-form-item>
          <template v-if="['el-checkbox-group', 'el-radio-group', 'el-select'].indexOf(activeData.tag) > -1">
            <el-divider>オプション</el-divider>
            <draggable :list="activeData.options" :animation="340" group="selectItem" handle=".option-drag"
              item-key="label">
              <template #item="{ element, index }">
                <div :key="index" class="select-item">
                  <div class="select-line-icon option-drag">
                    <i class="el-icon-s-operation" />
                  </div>
                  <el-input v-model="element.label" placeholder="オプション名" size="small" />
                  <el-input placeholder="オプションの値" size="small" :value="element.value"
                    @input="setOptionValue(element, $event)" />
                  <div class="close-btn select-line-icon" @click="activeData.options.splice(index, 1)">
                    <el-icon>
                      <Remove />
                    </el-icon>
                  </div>
                </div>
              </template>
            </draggable>
            <div>
              <el-button icon="CirclePlus" style="margin-left: 8px; margin-top: 10px;" text bg type="primary"
                @click="addSelectItem">
                オプションの追加
              </el-button>
            </div>
            <el-divider />
          </template>

          <template v-if="['el-cascader'].indexOf(activeData.tag) > -1">
            <el-divider>オプション</el-divider>
            <el-form-item label="データ型">
              <el-radio-group v-model="activeData.dataType" size="small">
                <el-radio-button label="dynamic">
                  動的データ
                </el-radio-button>
                <el-radio-button label="static">
                  静的データ
                </el-radio-button>
              </el-radio-group>
            </el-form-item>

            <template v-if="activeData.dataType === 'dynamic'">
              <el-form-item label="タグキー名">
                <el-input v-model="activeData.labelKey" placeholder="タグキー名を入力してください" />
              </el-form-item>
              <el-form-item label="値キー名">
                <el-input v-model="activeData.valueKey" placeholder="値のキー名を入力してください" />
              </el-form-item>
              <el-form-item label="子キーの名前">
                <el-input v-model="activeData.childrenKey" placeholder="子キーの名前を入力してください" />
              </el-form-item>
            </template>

            <el-tree v-if="activeData.dataType === 'static'" draggable :data="activeData.options" node-key="id"
              :expand-on-click-node="false" :render-content="renderContent" />
            <div v-if="activeData.dataType === 'static'">
              <el-button icon="CirclePlus" style="margin-left: 0; margin-top: 10px;" type="primary" text bg
                @click="addTreeItem">
                親の追加
              </el-button>
            </div>
            <el-divider />
          </template>

          <el-form-item v-if="activeData.optionType !== undefined" label="オプションスタイル">
            <el-radio-group v-model="activeData.optionType">
              <el-radio-button label="default">
                デフォルト
              </el-radio-button>
              <el-radio-button label="button">
                ボタン
              </el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item v-if="activeData['active-color'] !== undefined" label="カラーをオンにする">
            <el-color-picker v-model="activeData['active-color']" />
          </el-form-item>
          <el-form-item v-if="activeData['inactive-color'] !== undefined" label="色をオフにする">
            <el-color-picker v-model="activeData['inactive-color']" />
          </el-form-item>

          <el-form-item v-if="activeData['allow-half'] !== undefined" label="半分の選択を許可する">
            <el-switch v-model="activeData['allow-half']" />
          </el-form-item>
          <el-form-item v-if="activeData['show-text'] !== undefined" label="補助テキスト">
            <el-switch v-model="activeData['show-text']" @change="rateTextChange" />
          </el-form-item>
          <el-form-item v-if="activeData['show-score'] !== undefined" label="スコアを表示">
            <el-switch v-model="activeData['show-score']" @change="rateScoreChange" />
          </el-form-item>
          <el-form-item v-if="activeData['show-stops'] !== undefined" label="不連続性を表示する">
            <el-switch v-model="activeData['show-stops']" />
          </el-form-item>
          <el-form-item v-if="activeData.range !== undefined" label="範囲選択">
            <el-switch v-model="activeData.range" @change="rangeChange" />
          </el-form-item>
          <el-form-item v-if="activeData.border !== undefined && activeData.optionType === 'default'" label="国境を設けるかどうか">
            <el-switch v-model="activeData.border" />
          </el-form-item>
          <el-form-item v-if="activeData.tag === 'el-color-picker'" label="カラーフォーマット">
            <el-select v-model="activeData['color-format']" placeholder="カラー形式を選択してください" :style="{ width: '100%' }"
              @change="colorFormatChange">
              <el-option v-for="(item, index) in colorFormatOptions" :key="index" :label="item.label"
                :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item v-if="activeData.size !== undefined &&
            (activeData.optionType === 'button' ||
              activeData.border ||
              activeData.tag === 'el-color-picker')" label="オプションのサイズ">
            <el-radio-group v-model="activeData.size">
              <el-radio-button label="large">
                より大きな
              </el-radio-button>
              <el-radio-button label="default">
                デフォルト
              </el-radio-button>
              <el-radio-button label="small">
                小さい
              </el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item v-if="activeData['show-word-limit'] !== undefined" label="統計を入力してください">
            <el-switch v-model="activeData['show-word-limit']" />
          </el-form-item>
          <el-form-item v-if="activeData.tag === 'el-input-number'" label="厳密なステップ数">
            <el-switch v-model="activeData['step-strictly']" />
          </el-form-item>
          <el-form-item v-if="activeData.tag === 'el-cascader'" label="複数選択するかどうか">
            <el-switch v-model="activeData.props.props.multiple" />
          </el-form-item>
          <el-form-item v-if="activeData.tag === 'el-cascader'" label="フルパスを表示">
            <el-switch v-model="activeData['show-all-levels']" />
          </el-form-item>
          <el-form-item v-if="activeData.tag === 'el-cascader'" label="フィルタリングできます">
            <el-switch v-model="activeData.filterable" />
          </el-form-item>
          <el-form-item v-if="activeData.clearable !== undefined" label="クリアできるでしょうか">
            <el-switch v-model="activeData.clearable" />
          </el-form-item>
          <el-form-item v-if="activeData.showTip !== undefined" label="ヒントを表示">
            <el-switch v-model="activeData.showTip" />
          </el-form-item>
          <el-form-item v-if="activeData.multiple !== undefined" label="ファイルの複数選択">
            <el-switch v-model="activeData.multiple" />
          </el-form-item>
          <el-form-item v-if="activeData['auto-upload'] !== undefined" label="自動アップロード">
            <el-switch v-model="activeData['auto-upload']" />
          </el-form-item>
          <el-form-item v-if="activeData.readonly !== undefined" label="読み取り専用ですか?">
            <el-switch v-model="activeData.readonly" />
          </el-form-item>
          <el-form-item v-if="activeData.disabled !== undefined" label="無効にするかどうか">
            <el-switch v-model="activeData.disabled" />
          </el-form-item>
          <el-form-item v-if="activeData.tag === 'el-select'" label="検索可能ですか?">
            <el-switch v-model="activeData.filterable" />
          </el-form-item>
          <el-form-item v-if="activeData.tag === 'el-select'" label="複数選択するかどうか">
            <el-switch v-model="activeData.multiple" @change="multipleChange" />
          </el-form-item>
          <el-form-item v-if="activeData.required !== undefined" label="必要ですか?">
            <el-switch v-model="activeData.required" />
          </el-form-item>

          <template v-if="activeData.layoutTree">
            <el-divider>レイアウトツリー</el-divider>
            <el-tree :data="[activeData]" :props="layoutTreeProps" node-key="renderKey" default-expand-all draggable>
              <template #default="{ node, data }">
                <span class="node-label">
                  <svg-icon class="node-icon" :icon-class="data.tagIcon" style="margin-right: 5px;" />
                  {{ node.label }}
                </span>
              </template>
            </el-tree>
          </template>

          <template v-if="activeData.layout === 'colFormItem'">
            <el-divider>定期点検</el-divider>
            <div v-for="(item, index) in activeData.regList" :key="index" class="reg-item">
              <span class="close-btn" @click="activeData.regList.splice(index, 1)">
                <el-icon>
                  <Close />
                </el-icon>
              </span>
              <el-form-item label="表現">
                <el-input v-model="item.pattern" placeholder="正規表現を入力してください" />
              </el-form-item>
              <el-form-item label="エラーメッセージ" style="margin-bottom:0">
                <el-input v-model="item.message" placeholder="エラーメッセージを入力してください" />
              </el-form-item>
            </div>
            <div>
              <el-button icon="CirclePlus" style="margin-left: 0; margin-top: 10px;" type="primary" text bg
                @click="addReg">
                ルールの追加
              </el-button>
            </div>
          </template>
        </el-form>
        <!-- 表单属性 -->
        <el-form v-show="currentTab === 'form'" label-width="90px" label-position="top">
          <el-form-item label="フォーム名">
            <el-input v-model="formConf.formRef" placeholder="フォーム名（参照）を入力してください" />
          </el-form-item>
          <el-form-item label="フォームモデル">
            <el-input v-model="formConf.formModel" placeholder="データモデルを入力してください" />
          </el-form-item>
          <el-form-item label="モデルの検証">
            <el-input v-model="formConf.formRules" placeholder="検証モデルを入力してください" />
          </el-form-item>
          <el-form-item label="フォームサイズ">
            <el-radio-group v-model="formConf.size">
              <el-radio-button label="large" value="より大きな" />
              <el-radio-button label="default" value="デフォルト" />
              <el-radio-button label="small" value="小さい" />
            </el-radio-group>
          </el-form-item>
          <el-form-item label="ラベルの配置">
            <el-radio-group v-model="formConf.labelPosition">
              <el-radio-button label="left" value="左揃え" />
              <el-radio-button label="right" value="右揃え" />
              <el-radio-button label="top" value="上揃え" />
            </el-radio-group>
          </el-form-item>
          <el-form-item label="ラベル幅">
            <el-input-number v-model="formConf.labelWidth" placeholder="ラベル幅" />
          </el-form-item>
          <el-form-item label="グリッド間隔">
            <el-input-number v-model="formConf.gutter" :min="0" placeholder="グリッド間隔" />
          </el-form-item>
          <el-form-item label="フォームを無効にする">
            <el-switch v-model="formConf.disabled" />
          </el-form-item>
          <el-form-item label="フォームボタン">
            <el-switch v-model="formConf.formBtns" />
          </el-form-item>
          <el-form-item label="選択されていないコンポーネントの境界線を表示する">
            <el-switch v-model="formConf.unFocusedComponentBorder" />
          </el-form-item>
        </el-form>
      </el-scrollbar>
    </div>
    <icons-dialog v-model="iconsVisible" :current="activeData[currentIconModel]" @select="setIcon" />
    <treeNode-dialog v-model="dialogVisible" @commit="addNode" />

  </div>
</template>

<script setup>
import draggable from "vuedraggable/dist/vuedraggable.common"
import { isNumberStr } from '@/utils/index'
import IconsDialog from './IconsDialog'
import TreeNodeDialog from './TreeNodeDialog'
import { inputComponents, selectComponents } from '@/utils/generator/config'

const { proxy } = getCurrentInstance()
const dateTimeFormat = {
  date: 'YYYY-MM-DD',
  week: 'YYYY週ww',
  month: 'YYYY-MM',
  year: 'YYYY',
  datetime: 'YYYY-MM-DD HH:mm:ss',
  daterange: 'YYYY-MM-DD',
  monthrange: 'YYYY-MM',
  datetimerange: 'YYYY-MM-DD HH:mm:ss'
}
const props = defineProps({
  showField: Boolean,
  activeData: Object,
  formConf: Object
})

const data = reactive({
  currentTab: 'field',
  currentNode: null,
  dialogVisible: false,
  iconsVisible: false,
  currentIconModel: null,
  dateTypeOptions: [
    {
      label: '日(date)',
      value: 'date'
    },
    {
      label: '週(week)',
      value: 'week'
    },
    {
      label: '月(month)',
      value: 'month'
    },
    {
      label: '年(year)',
      value: 'year'
    },
    {
      label: '日付時刻(datetime)',
      value: 'datetime'
    }
  ],
  dateRangeTypeOptions: [
    {
      label: '日付範囲(daterange)',
      value: 'daterange'
    },
    {
      label: '月の範囲(monthrange)',
      value: 'monthrange'
    },
    {
      label: '日付と時刻の範囲(datetimerange)',
      value: 'datetimerange'
    }
  ],
  colorFormatOptions: [
    {
      label: 'hex',
      value: 'hex'
    },
    {
      label: 'rgb',
      value: 'rgb'
    },
    {
      label: 'rgba',
      value: 'rgba'
    },
    {
      label: 'hsv',
      value: 'hsv'
    },
    {
      label: 'hsl',
      value: 'hsl'
    }
  ],
  justifyOptions: [
    {
      label: 'start',
      value: 'start'
    },
    {
      label: 'end',
      value: 'end'
    },
    {
      label: 'center',
      value: 'center'
    },
    {
      label: 'space-around',
      value: 'space-around'
    },
    {
      label: 'space-between',
      value: 'space-between'
    }
  ],
  layoutTreeProps: {
    label(data, node) {
      return data.componentName || `${data.label}: ${data.vModel}`
    }
  }
})

const { currentTab, currentNode, dialogVisible, iconsVisible, currentIconModel, dateTypeOptions, dateRangeTypeOptions, colorFormatOptions, justifyOptions, layoutTreeProps } = toRefs(data)

const documentLink = computed(() => props.activeData.document || 'https://element-plus.org/zh-CN/guide/installation')

const dateOptions = computed(() => {
  if (props.activeData.type !== undefined && props.activeData.tag === 'el-date-picker') {
    if (props.activeData['start-placeholder'] === undefined) {
      return dateTypeOptions.value
    }
    return dateRangeTypeOptions.value
  }
  return []
})

const tagList = ref([
  {
    label: '入力コンポーネント',
    options: inputComponents
  },
  {
    label: '選択的なコンポーネント',
    options: selectComponents
  }
])

const emit = defineEmits(['tag-change'])

function addReg() {
  props.activeData.regList.push({
    pattern: '',
    message: ''
  })
}
function addSelectItem() {
  props.activeData.options.push({
    label: '',
    value: ''
  })
}

function addTreeItem() {
  ++proxy.idGlobal
  dialogVisible.value = true
  currentNode.value = props.activeData.options
}

function renderContent(h, { node, data, store }) {
  return h('div', {
    class: "custom-tree-node"
  }, [
    h('span', node.label),
    h('span', {
      class: "node-operation"
    }, [
      h(resolveComponent('el-link'), {
        type: "primary",
        icon: "Plus",
        underline: false,
        onClick: () => {
          append(data)

        }
      }),
      h(resolveComponent('el-link'), {
        type: "danger",
        icon: "Delete",
        underline: false,
        style: "margin-left: 5px;",
        onClick: () => {
          remove(node, data)
        }
      })
    ])
  ])
}
function append(data) {
  if (!data.children) {
    data.children = []
  }
  dialogVisible.value = true
  currentNode.value = data.children
}
function remove(node, data) {
  const { parent } = node
  const children = parent.data.children || parent.data
  const index = children.findIndex(d => d.id === data.id)
  children.splice(index, 1)
}
function addNode(data) {
  currentNode.value.push(data)
}

function setOptionValue(item, val) {
  item.value = isNumberStr(val) ? +val : val
}
function setDefaultValue(val) {
  if (Array.isArray(val)) {
    return val.join(',')
  }
  if (['string', 'number'].indexOf(val) > -1) {
    return val
  }
  if (typeof val === 'boolean') {
    return `${val}`
  }
  return val
}

function onDefaultValueInput(str) {
  if (Array.isArray(props.activeData.defaultValue)) {
    // 数组
    props.activeData.defaultValue = str.split(',').map(val => (isNumberStr(val) ? +val : val))
  } else if (['true', 'false'].indexOf(str) > -1) {
    // 布尔
    props.activeData.defaultValue = JSON.parse(str)
  } else {
    // 字符串和数字
    props.activeData.defaultValue = isNumberStr(str) ? +str : str
  }
}

function onSwitchValueInput(val, name) {
  if (['true', 'false'].indexOf(val) > -1) {
    props.activeData[name] = JSON.parse(val)
  } else {
    props.activeData[name] = isNumberStr(val) ? +val : val
  }
}

function setTimeValue(val, type) {
  const valueFormat = type === 'week' ? dateTimeFormat.date : val
  props.activeData.defaultValue = null
  props.activeData['value-format'] = valueFormat
  props.activeData.format = val
}

function spanChange(val) {
  props.formConf.span = val
}

function multipleChange(val) {
  props.activeData.defaultValue = val ? [] : ''
}

function dateTypeChange(val) {
  setTimeValue(dateTimeFormat[val], val)
}

function rangeChange(val) {
  props.activeData.defaultValue = val ? [props.activeData.min, props.activeData.max] : props.activeData.min
}

function rateTextChange(val) {
  if (val) props.activeData['show-score'] = false
}

function rateScoreChange(val) {
  if (val) props.activeData['show-text'] = false
}

function colorFormatChange(val) {
  props.activeData.defaultValue = null
  props.activeData['show-alpha'] = val.indexOf('a') > -1
  props.activeData.renderKey = +new Date() // 更新renderKey,重新渲染该组件
}

function openIconsDialog(model) {
  iconsVisible.value = true
  currentIconModel.value = model
}

function setIcon(val) {
  props.activeData[currentIconModel.value] = val
}

function tagChange(tagIcon) {
  let target = inputComponents.find(item => item.tagIcon === tagIcon)
  if (!target) target = selectComponents.find(item => item.tagIcon === tagIcon)
  emit('tag-change', target)
}
</script>

<style lang="scss" scoped>
.right-board {
  width: 350px;
  position: absolute;
  right: 0;
  top: 0;
  padding-top: 3px;

  &:deep() {
    .el-tabs__header {
      margin: 0;
    }

    .el-input-group__append .el-button {
      display: inline-flex;
    }
  }

  .field-box {
    position: relative;
    height: calc(100vh - 50px - 40px - 42px);
    box-sizing: border-box;
    overflow: hidden;
  }

  .el-scrollbar {
    height: 100%;

    &:deep() {
      .el-scrollbar__view {
        padding: 30px 20px;
      }

    }
  }
}

.reg-item {
  padding: 12px 6px;
  background: var(--el-border-color-extra-light);
  position: relative;
  border-radius: 4px;

  .close-btn {
    position: absolute;
    right: -6px;
    top: -6px;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 16px;
    height: 16px;
    line-height: 16px;
    background: rgba(0, 0, 0, .2);
    border-radius: 50%;
    color: #fff;
    z-index: 1;
    cursor: pointer;
    font-size: 12px;
  }
}

.select-item {
  display: flex;
  border: 1px dashed #fff;
  box-sizing: border-box;

  & .close-btn {
    cursor: pointer;
    color: #f56c6c;
  }

  & .el-input+.el-input {
    margin-left: 4px;
  }
}

.select-item+.select-item {
  margin-top: 4px;
}

.select-item.sortable-chosen {
  border: 1px dashed #409eff;
}

.select-line-icon {
  line-height: 32px;
  font-size: 22px;
  padding: 0 4px;
  color: #777;
}

.option-drag {
  cursor: move;
}

.time-range {
  .el-date-editor {
    width: 227px;
  }

  :deep() {
    .el-icon-time {
      display: none;
    }
  }
}

.document-link {
  position: absolute;
  display: flex;
  width: 26px;
  height: 26px;
  top: 0;
  left: 0;
  cursor: pointer;
  background: #409eff;
  z-index: 1;
  border-radius: 0 0 6px 0;
  justify-content: center;
  align-items: center;
  color: #fff;
  font-size: 18px;
}

.node-label {
  font-size: 14px;
}

.node-icon {
  color: #bebfc3;
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}
</style>