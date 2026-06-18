<template>
  <div class="api-tests">
    <div class="header">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新建接口测试
      </el-button>
    </div>

    <el-table :data="tableData" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="testName" label="测试名称" width="200" />
      <el-table-column prop="apiName" label="接口名称" width="200" />
      <el-table-column prop="totalCount" label="总次数" width="100" />
      <el-table-column prop="successCount" label="成功次数" width="100" />
      <el-table-column prop="failCount" label="失败次数" width="100" />
      <el-table-column prop="avgTime" label="平均耗时(ms)" width="120" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="300">
        <template #default="{ row }">
          <el-button
              type="success"
              link
              @click="handleStart(row)"
              :disabled="row.status === 1"
          >
            <el-icon><VideoPlay /></el-icon>
            启动测试
          </el-button>
          <el-button
              type="primary"
              link
              @click="handleViewResult(row)"
              :disabled="row.status !== 2"
          >
            <el-icon><DataAnalysis /></el-icon>
            查看结果
          </el-button>
          <el-button type="danger" link @click="handleDelete(row.id)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新建对话框 -->
    <el-dialog v-model="dialogVisible" title="新建接口测试" width="40%" @close="resetForm">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px">
        <el-form-item label="测试名称" prop="testName">
          <el-input v-model="formData.testName" placeholder="请输入测试名称" />
        </el-form-item>
        <el-form-item label="选择接口" prop="apiDefinitionId">
          <el-select v-model="formData.apiDefinitionId" placeholder="选择接口定义" style="width: 100%">
            <el-option
                v-for="api in apiDefinitions"
                :key="api.id"
                :label="api.apiName"
                :value="api.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="总次数" prop="totalCount">
          <el-input-number v-model="formData.totalCount" :min="1" :max="10000" />
        </el-form-item>
        <el-form-item label="线程数" prop="threadCount">
          <el-input-number v-model="formData.threadCount" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="总时间(秒)" prop="testDuration">
          <el-input-number v-model="formData.testDuration" :min="1" :max="3600" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 结果查看对话框 -->
    <el-dialog v-model="resultDialogVisible" title="测试结果" width="50%">
      <div v-if="resultData" class="result-detail">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="result-item">
              <div class="label">总次数</div>
              <div class="value">{{ resultData.totalCount }}</div>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="result-item">
              <div class="label">成功次数</div>
              <div class="value success">{{ resultData.successCount }}</div>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="result-item">
              <div class="label">失败次数</div>
              <div class="value danger">{{ resultData.failCount }}</div>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="result-item">
              <div class="label">状态</div>
              <div class="value">{{ resultData.status }}</div>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="result-item">
              <div class="label">最长耗时</div>
              <div class="value">{{ resultData.maxTime }} ms</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="result-item">
              <div class="label">最短耗时</div>
              <div class="value">{{ resultData.minTime }} ms</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="result-item">
              <div class="label">平均耗时</div>
              <div class="value">{{ resultData.avgTime }} ms</div>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { apiTestsApi } from '@/api/apiTests'
import { apiDefinitionsApi } from '@/api/apiDefinitions'

const tableData = ref([])
const apiDefinitions = ref([])
const dialogVisible = ref(false)
const resultDialogVisible = ref(false)
const resultData = ref(null)
const submitLoading = ref(false)
const formRef = ref()

const formData = reactive({
  testName: '',
  apiDefinitionId: null,
  totalCount: 100,
  threadCount: 10,
  testDuration: 60
})

const rules = {
  testName: [{ required: true, message: '请输入测试名称', trigger: 'blur' }],
  apiDefinitionId: [{ required: true, message: '请选择接口定义', trigger: 'change' }],
  totalCount: [{ required: true, message: '请输入总次数', trigger: 'blur' }],
  threadCount: [{ required: true, message: '请输入线程数', trigger: 'blur' }],
  testDuration: [{ required: true, message: '请输入总时间', trigger: 'blur' }]
}

const getStatusType = (status) => {
  const types = {
    0: 'info',
    1: 'warning',
    2: 'success'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    0: '未执行',
    1: '执行中',
    2: '已完成'
  }
  return texts[status] || '未知'
}

const loadData = async () => {
  try {
    const [testRes, apiRes] = await Promise.all([
      apiTestsApi.list(),
      apiDefinitionsApi.list()
    ])
    tableData.value = testRes.data || []
    apiDefinitions.value = apiRes.data || []
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

const handleStart = async (row) => {
  try {
    await ElMessageBox.confirm('确定要启动此测试吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    await apiTestsApi.startTest(row.id)
    ElMessage.success('测试已启动')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '启动测试失败')
    }
  }
}

const handleViewResult = async (row) => {
  try {
    const res = await apiTestsApi.getResult(row.id)
    resultData.value = res.data
    resultDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取测试结果失败')
  }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除这条测试记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await apiTestsApi.delete(id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    await apiTestsApi.create(formData)
    ElMessage.success('创建成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '创建失败')
  } finally {
    submitLoading.value = false
  }
}

const resetForm = () => {
  formData.testName = ''
  formData.apiDefinitionId = null
  formData.totalCount = 100
  formData.threadCount = 10
  formData.testDuration = 60
  formRef.value?.resetFields()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.api-tests {
  background-color: #fff;
  padding: 20px;
  border-radius: 4px;
}

.header {
  margin-bottom: 20px;
}

.result-detail {
  padding: 20px;
}

.result-item {
  background-color: #f5f7fa;
  border-radius: 8px;
  padding: 16px;
  text-align: center;
  margin-bottom: 16px;
}

.result-item .label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.result-item .value {
  font-size: 24px;
  font-weight: bold;
}

.result-item .value.success {
  color: #67c23a;
}

.result-item .value.danger {
  color: #f56c6c;
}
</style>