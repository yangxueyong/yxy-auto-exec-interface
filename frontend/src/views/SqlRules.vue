<template>
  <div class="sql-rules">
    <div class="header">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新建SQL规则
      </el-button>
    </div>

    <el-table :data="tableData" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="ruleName" label="规则名称" width="200" />
      <el-table-column prop="ruleSql" label="SQL语句" min-width="300">
        <template #default="{ row }">
          <el-text type="info" style="font-family: monospace; font-size: 12px">
            {{ row.ruleSql }}
          </el-text>
        </template>
      </el-table-column>
      <el-table-column prop="ruleFields" label="查询字段" width="200" />
      <el-table-column label="操作" width="200" fixed="right">
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
        width="60%"
        @close="resetForm"
    >
      <el-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          label-width="100px"
      >
        <el-form-item label="规则名称" prop="ruleName">
          <el-input v-model="formData.ruleName" placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="SQL语句" prop="ruleSql">
          <el-input
              v-model="formData.ruleSql"
              type="textarea"
              :rows="6"
              placeholder="请输入查询类SQL语句"
              style="font-family: monospace"
          />
        </el-form-item>
        <el-form-item label="解析字段" v-if="formData.ruleFields">
          <el-tag
              v-for="field in formData.fieldList"
              :key="field"
              style="margin-right: 8px"
          >
            {{ field }}
          </el-tag>
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
import { sqlRulesApi } from '@/api/sqlRules'

const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新建SQL规则')
const submitLoading = ref(false)
const formRef = ref()

const formData = reactive({
  id: null,
  ruleName: '',
  ruleSql: '',
  ruleFields: '',
  fieldList: []
})

const rules = {
  ruleName: [{ required: true, message: '请输入规则名称', trigger: 'blur' }],
  ruleSql: [{ required: true, message: '请输入SQL语句', trigger: 'blur' }]
}

const loadData = async () => {
  try {
    const res = await sqlRulesApi.list()
    tableData.value = res.data || []
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const handleAdd = () => {
  dialogTitle.value = '新建SQL规则'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑SQL规则'
  Object.assign(formData, {
    id: row.id,
    ruleName: row.ruleName,
    ruleSql: row.ruleSql,
    ruleFields: row.ruleFields,
    fieldList: row.fieldList || []
  })
  dialogVisible.value = true
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除这条规则吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await sqlRulesApi.delete(id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    if (formData.id) {
      await sqlRulesApi.update(formData)
    } else {
      await sqlRulesApi.create(formData)
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
  formData.ruleSql = ''
  formData.ruleFields = ''
  formData.fieldList = []
  formRef.value?.resetFields()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.sql-rules {
  background-color: #fff;
  padding: 20px;
  border-radius: 4px;
}

.header {
  margin-bottom: 20px;
}
</style>