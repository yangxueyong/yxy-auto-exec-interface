<template>
  <div class="response-rules">
    <div class="header">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新建返参规则
      </el-button>
    </div>

    <el-table :data="tableData" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="ruleName" label="规则名称" width="200" />
      <el-table-column prop="spelExpression" label="SPEL表达式" min-width="300">
        <template #default="{ row }">
          <el-text type="info" style="font-family: monospace; font-size: 12px">
            {{ row.spelExpression }}
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
        width="50%"
        @close="resetForm"
    >
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px">
        <el-form-item label="规则名称" prop="ruleName">
          <el-input v-model="formData.ruleName" placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="SPEL表达式" prop="spelExpression">
          <el-input
              v-model="formData.spelExpression"
              type="textarea"
              :rows="4"
              placeholder="请输入SPEL表达式，例如：#result.code == 200"
              style="font-family: monospace"
          />
        </el-form-item>
        <el-form-item label="示例">
          <el-text type="info">
            #result.code == 200 表示返回结果中的code字段等于200时成功
          </el-text>
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
import { responseRulesApi } from '@/api/responseRules'

const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新建返参规则')
const submitLoading = ref(false)
const formRef = ref()

const formData = reactive({
  id: null,
  ruleName: '',
  spelExpression: ''
})

const rules = {
  ruleName: [{ required: true, message: '请输入规则名称', trigger: 'blur' }],
  spelExpression: [{ required: true, message: '请输入SPEL表达式', trigger: 'blur' }]
}

const loadData = async () => {
  try {
    const res = await responseRulesApi.list()
    tableData.value = res.data || []
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const handleAdd = () => {
  dialogTitle.value = '新建返参规则'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑返参规则'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除这条规则吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await responseRulesApi.delete(id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    if (formData.id) {
      await responseRulesApi.update(formData)
    } else {
      await responseRulesApi.create(formData)
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
  formData.spelExpression = ''
  formRef.value?.resetFields()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.response-rules {
  background-color: #fff;
  padding: 20px;
  border-radius: 4px;
}

.header {
  margin-bottom: 20px;
}
</style>