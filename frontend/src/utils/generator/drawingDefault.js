export const drawingDefaultValue = []

export function initDrawingDefaultValue() {
  if (drawingDefaultValue.length === 0) {
    drawingDefaultValue.push({
      layout: 'colFormItem',
      tagIcon: 'input',
      label: '携帯電話番号',
      vModel: 'mobile',
      formId: 6,
      tag: 'el-input',
      placeholder: '携帯電話番号を入力してください',
      defaultValue: '',
      span: 24,
      style: {width: '100%'},
      clearable: true,
      prepend: '',
      append: '',
      'prefix-icon': 'Cellphone',
      'suffix-icon': '',
      maxlength: 11,
      'show-word-limit': true,
      readonly: false,
      disabled: false,
      required: true,
      changeTag: true,
      regList: [{
        pattern: '/^1(3|4|5|7|8|9)\\d{9}$/',
        message: '携帯電話番号の形式エラー'
      }]
    })
  }
}

export function cleanDrawingDefaultValue() {
  drawingDefaultValue.splice(0, drawingDefaultValue.length)
}
