<template>
  <div class="api-definitions">
    <div class="header">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新建接口定义
      </el-button>
    </div>

    <el-table :data="tableData" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="apiName" label="接口名称" width="200" />
      <el-table-column prop="apiUrl" label="接口URL" width="300" />
      <el-table-column prop="apiMethod" label="请求方法" width="100">
        <template #default="{ row }">
          <el-tag :type="getMethodType(row.apiMethod)">{{ row.apiMethod }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="paramRuleName" label="入参规则" width="150" />
      <el-table-column prop="responseRuleNames" label="返参规则" min-width="150" />
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
        width="50%"
        @close="resetForm"
    >
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px">
        <el-form-item label="接口名称" prop="apiName">
          <el-input v-model="formData.apiName" placeholder="请输入接口名称" />
        </el-form-item>
        <el-form-item label="接口URL" prop="apiUrl">
          <el-input v-model="formData.apiUrl" placeholder="请输入接口URL" />
        </el-form-item>
        <el-form-item label="请求方法" prop="apiMethod">
          <el-select v-model="formData.apiMethod" placeholder="选择请求方法">
            <el-option label="GET" value="GET" />
            <el-option label="POST" value="POST" />
            <el-option label="PUT" value="PUT" />
            <el-option label="DELETE" value="DELETE" />
          </el-select>
        </el-form-item>
        <el-form-item label="入参规则" prop="paramRuleId">
          <el-select v-model="formData.paramRuleId" placeholder="选择入参规则" style="width: 100%">
            <el-option
                v-for="rule in paramRules"
                :key="rule.id"
                :label="rule.ruleName"
                :value="rule.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="返参规则" prop="responseRuleIds">
          <el-select
              v-model="formData.responseRuleIds"
              multiple
              placeholder="选择返参规则"
              style="width: 100%"
          >
            <el-option
                v-for="rule in responseRules"
                :key="rule.id"
                :label="rule.ruleName"
                :value="rule.id"
            />
          </el-select>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { apiDefinitionsApi } from '@/api/apiDefinitions'
import { paramRulesApi } from '@/api/paramRules'
import { responseRulesApi } from '@/api/responseRules'

const tableData = ref([])
const paramRules = ref([])
const responseRules = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新建接口定义')
const submitLoading = ref(false)
const formRef = ref()

const formData = reactive({
  id: null,
  apiName: '',
  apiUrl: '',
  apiMethod: 'GET',
  paramRuleId: null,
  responseRuleIds: []
})

const rules = {
  apiName: [{ required: true, message: '请输入接口名称', trigger: 'blur' }],
  apiUrl: [{ required: true, message: '请输入接口URL', trigger: 'blur' }],
  apiMethod: [{ required: true, message: '请选择请求方法', trigger: 'change' }],
  paramRuleId: [{ required: true, message: '请选择入参规则', trigger: 'change' }],
  responseRuleIds: [{ required: true, message: '请选择返参规则', trigger: 'change' }]
}

const getMethodType = (method) => {
  const types = {
    GET: 'success',
    POST: 'primary',
    PUT: 'warning',
    DELETE: 'danger'
  }
  return types[method] || ''
}

const loadData = async () => {
  try {
    const [apiRes, paramRes, responseRes] = await Promise.all([
      apiDefinitionsApi.list(),
      paramRulesApi.list(),
      responseRulesApi.list()
    ])
    tableData.value = apiRes.data || []
    paramRules.value = paramRes.data || []
    responseRules.value = responseRes.data || []
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const handleAdd = () => {
  dialogTitle.value = '新建接口定义'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑接口定义'
  Object.assign(formData, {
    ...row,
    responseRuleIds: row.responseRuleIds ? row.responseRuleIds.split(',').map(Number) : []
  })
  dialogVisible.value = true
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除这条接口定义吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await apiDefinitionsApi.delete(id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    const data = {
      ...formData,
      responseRuleIds: formData.responseRuleIds.join(',')
    }
    if (formData.id) {
      await apiDefinitionsApi.update(data)
    } else {
      await apiDefinitionsApi.create(data)
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
  formData.apiName = ''
  formData.apiUrl = ''
  formData.apiMethod = 'GET'
  formData.paramRuleId = null
  formData.responseRuleIds = []
  formRef.value?.resetFields()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.api-definitions {
  background-color: #fff;
  padding: 20px;
  border-radius: 4px;
}

.header {
  margin-bottom: 20px;
}
</style>