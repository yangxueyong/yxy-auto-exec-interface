<template>
  <div class="param-rules">
    <div class="header">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新建入参规则
      </el-button>
    </div>

    <el-table :data="tableData" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="ruleName" label="规则名称" width="200" />
      <el-table-column prop="paramJson" label="参数JSON" min-width="300">
        <template #default="{ row }">
          <el-text type="info" style="font-family: monospace; font-size: 12px">
            {{ row.paramJson }}
          </el-text>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新建/编辑对话框 -->
    <el-dialog
        v-model="dialogVisible"
        :title="dialogTitle"
        width="70%"
        @close="resetForm"
    >
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px">
        <el-form-item label="规则名称" prop="ruleName">
          <el-input v-model="formData.ruleName" placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="参数JSON" prop="paramJson">
          <el-input
              v-model="formData.paramJson"
              type="textarea"
              :rows="6"
              placeholder="请输入JSON格式的参数"
              style="font-family: monospace"
          />
        </el-form-item>
        <el-form-item label="参数配置" v-if="paramConfigs.length > 0">
          <div class="param-configs">
            <div
                v-for="(config, index) in paramConfigs"
                :key="index"
                class="config-item"
            >
              <div class="config-header">
                <span class="param-name">{{ config.paramName }}</span>
                <el-select
                    v-model="config.paramType"
                    placeholder="选择参数类型"
                    style="width: 160px"
                >
                  <el-option label="固定值" value="fixed" />
                  <el-option label="来源数据库" value="db" />
                  <el-option label="随机时间" value="random_time" />
                  <el-option label="随机日期" value="random_date" />
                  <el-option label="手动录入随机数据集" value="manual" />
                </el-select>
              </div>

              <div class="config-body" v-if="config.paramType">
                <!-- 固定值 -->
                <el-form-item label="固定值" v-if="config.paramType === 'fixed'">
                  <el-input v-model="config.fixedValue" placeholder="请输入固定值" />
                </el-form-item>

                <!-- 来源数据库 -->
                <div v-if="config.paramType === 'db'">
                  <el-form-item label="SQL规则">
                    <el-select
                        v-model="config.sqlRuleId"
                        placeholder="选择SQL规则"
                        style="width: 200px"
                    >
                      <el-option
                          v-for="rule in sqlRules"
                          :key="rule.id"
                          :label="rule.ruleName"
                          :value="rule.id"
                      />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="规则字段">
                    <el-select
                        v-model="config.sqlField"
                        placeholder="选择字段"
                        style="width: 200px"
                    >
                      <el-option
                          v-for="field in getSqlRuleFields(config.sqlRuleId)"
                          :key="field"
                          :label="field"
                          :value="field"
                      />
                    </el-select>
                  </el-form-item>
                </div>

                <!-- 手动录入随机数据集 -->
                <div v-if="config.paramType === 'manual'">
                  <el-form-item label="数据集">
                    <el-input
                        v-model="config.manualValuesStr"
                        placeholder="请输入数据，用逗号分隔"
                        style="width: 400px"
                    />
                  </el-form-item>
                </div>
              </div>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { paramRulesApi } from '@/api/paramRules'
import { sqlRulesApi } from '@/api/sqlRules'

const tableData = ref([])
const sqlRules = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新建入参规则')
const submitLoading = ref(false)
const formRef = ref()

const formData = reactive({
  id: null,
  ruleName: '',
  paramJson: '',
  paramConfigs: []
})

const paramConfigs = computed(() => formData.paramConfigs || [])

const rules = {
  ruleName: [{ required: true, message: '请输入规则名称', trigger: 'blur' }],
  paramJson: [{ required: true, message: '请输入参数JSON', trigger: 'blur' }]
}

const loadData = async () => {
  try {
    const [paramRes, sqlRes] = await Promise.all([
      paramRulesApi.list(),
      sqlRulesApi.list()
    ])
    tableData.value = paramRes.data || []
    sqlRules.value = sqlRes.data || []
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const getSqlRuleFields = (ruleId) => {
  const rule = sqlRules.value.find(r => r.id === ruleId)
  return rule ? rule.fieldList || [] : []
}

const parseParamJson = (jsonStr) => {
  try {
    const obj = JSON.parse(jsonStr)
    const configs = []
    const parseObj = (obj, prefix = '') => {
      for (const key in obj) {
        const fullKey = prefix ? `${prefix}.${key}` : key
        configs.push({
          paramName: fullKey,
          paramType: '',
          fixedValue: '',
          sqlRuleId: null,
          sqlField: '',
          manualValues: [],
          manualValuesStr: ''
        })
        if (typeof obj[key] === 'object' && obj[key] !== null) {
          parseObj(obj[key], fullKey)
        }
      }
    }
    parseObj(obj)
    return configs
  } catch (e) {
    ElMessage.error('JSON格式不正确')
    return []
  }
}

const handleAdd = () => {
  dialogTitle.value = '新建入参规则'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑入参规则'
  Object.assign(formData, {
    id: row.id,
    ruleName: row.ruleName,
    paramJson: row.paramJson,
    paramConfigs: row.paramConfigs || []
  })
  // 恢复manualValuesStr
  formData.paramConfigs.forEach(config => {
    if (config.paramType === 'manual' && config.manualValues) {
      config.manualValuesStr = config.manualValues.join(',')
    }
  })
  dialogVisible.value = true
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除这条规则吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await paramRulesApi.delete(id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

const handleSubmit = async () => {
  await formRef.value?.validate()

  // 处理manual数据
  formData.paramConfigs.forEach(config => {
    if (config.paramType === 'manual' && config.manualValuesStr) {
      config.manualValues = config.manualValuesStr.split(',').map(s => s.trim()).filter(Boolean)
    }
  })

  submitLoading.value = true
  try {
    if (formData.id) {
      await paramRulesApi.update(formData)
    } else {
      await paramRulesApi.create(formData)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    submitLoading.value = false
  }
}

const resetForm = () => {
  formData.id = null
  formData.ruleName = ''
  formData.paramJson = ''
  formData.paramConfigs = []
  formRef.value?.resetFields()
}

// 监听paramJson变化，自动解析
const watchParamJson = (newVal) => {
  if (newVal) {
    const configs = parseParamJson(newVal)
    if (configs.length > 0) {
      formData.paramConfigs = configs
    }
  }
}

// 使用watch
import { watch } from 'vue'
watch(() => formData.paramJson, watchParamJson)

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.param-rules {
  background-color: #fff;
  padding: 20px;
  border-radius: 4px;
}

.header {
  margin-bottom: 20px;
}

.param-configs {
  max-height: 400px;
  overflow-y: auto;
}

.config-item {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 16px;
  margin-bottom: 12px;
}

.config-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 12px;
}

.param-name {
  font-weight: bold;
  min-width: 100px;
}

.config-body {
  padding-left: 20px;
  border-left: 2px solid #409eff;
}
</style>