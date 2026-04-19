<template>
  <div class="role-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增角色
          </el-button>
        </div>
      </template>

      <el-table :data="roleList" border v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="roleName" label="角色名称" min-width="150" />
        <el-table-column prop="roleCode" label="角色编码" min-width="150" />
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link @click="handleAssignMenu(row)">分配菜单</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="formData.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="formData.roleCode" placeholder="请输入角色编码" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配菜单对话框 -->
    <el-dialog
      v-model="menuDialogVisible"
      title="分配菜单权限"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-tree
        ref="menuTreeRef"
        :data="menuTreeData"
        :props="{ label: 'menuName', children: 'children' }"
        node-key="id"
        show-checkbox
        default-expand-all
        :default-checked-keys="checkedMenuIds"
      />
      <template #footer>
        <el-button @click="menuDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="menuSubmitLoading" @click="handleSaveMenu">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoleList, addRole, updateRole, deleteRole, assignRoleMenus, getRoleMenus } from '@/api/role'
import { getMenuTree } from '@/api/menu'

const roleList = ref([])
const loading = ref(false)

const loadData = async () => {
  loading.value = true
  try {
    const res = await getRoleList()
    if (res.code === 200) {
      roleList.value = res.data || []
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('获取角色列表失败')
  } finally {
    loading.value = false
  }
}

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref()

const formData = reactive({
  id: null,
  roleName: '',
  roleCode: '',
  description: '',
  status: 1
})

const formRules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

const resetForm = () => {
  formData.id = null
  formData.roleName = ''
  formData.roleCode = ''
  formData.description = ''
  formData.status = 1
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增角色'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑角色'
  resetForm()
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    const api = isEdit.value ? updateRole : addRole
    const res = await api(formData.id, formData)
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
      dialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除角色"${row.roleName}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await deleteRole(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error('删除失败')
    }
  }
}

// 菜单分配相关
const menuDialogVisible = ref(false)
const menuSubmitLoading = ref(false)
const menuTreeData = ref([])
const checkedMenuIds = ref([])
const currentRoleId = ref(null)
const menuTreeRef = ref()

// 加载菜单树
const loadMenuTree = async () => {
  try {
    const res = await getMenuTree()
    if (res.code === 200) {
      menuTreeData.value = res.data || []
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('获取菜单列表失败')
  }
}

// 打开分配菜单对话框
const handleAssignMenu = async (row) => {
  currentRoleId.value = row.id
  await loadMenuTree()
  
  // 获取角色已分配的菜单
  try {
    const res = await getRoleMenus(row.id)
    if (res.code === 200) {
      checkedMenuIds.value = res.data || []
      menuDialogVisible.value = true
      
      // 等待树组件渲染完成后设置选中状态
      nextTick(() => {
        menuTreeRef.value?.setCheckedKeys(checkedMenuIds.value)
      })
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('获取角色菜单失败')
  }
}

// 保存菜单分配
const handleSaveMenu = async () => {
  if (!currentRoleId.value) return
  
  menuSubmitLoading.value = true
  try {
    // 获取选中的菜单ID（包括半选中的父节点）
    const checkedKeys = menuTreeRef.value.getCheckedKeys()
    const halfCheckedKeys = menuTreeRef.value.getHalfCheckedKeys()
    const allMenuIds = [...checkedKeys, ...halfCheckedKeys]
    
    const res = await assignRoleMenus(currentRoleId.value, allMenuIds)
    if (res.code === 200) {
      ElMessage.success('菜单分配成功')
      menuDialogVisible.value = false
    } else {
      ElMessage.error(res.message || '分配失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('分配失败')
  } finally {
    menuSubmitLoading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
