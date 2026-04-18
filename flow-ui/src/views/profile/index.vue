<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <!-- 左侧个人信息 -->
      <el-col :span="8">
        <el-card>
          <div class="profile-header">
            <el-avatar :size="80" :src="userInfo.avatar || defaultAvatar" />
            <h3>{{ userInfo.realName || userInfo.username }}</h3>
            <p>{{ userInfo.deptName }}</p>
          </div>
          <div class="profile-info">
            <div class="info-item">
              <span class="label">用户名:</span>
              <span>{{ userInfo.username }}</span>
            </div>
            <div class="info-item">
              <span class="label">手机号:</span>
              <span>{{ userInfo.phone || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">邮箱:</span>
              <span>{{ userInfo.email || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">角色:</span>
              <el-tag
                v-for="role in userInfo.roles"
                :key="role"
                size="small"
                style="margin-right: 5px"
              >
                {{ role }}
              </el-tag>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧设置 -->
      <el-col :span="16">
        <el-card>
          <el-tabs v-model="activeTab">
            <el-tab-pane label="基本信息" name="basic">
              <el-form :model="profileForm" label-width="100px">
                <el-form-item label="真实姓名">
                  <el-input v-model="profileForm.realName" />
                </el-form-item>
                <el-form-item label="手机号">
                  <el-input v-model="profileForm.phone" />
                </el-form-item>
                <el-form-item label="邮箱">
                  <el-input v-model="profileForm.email" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="saveProfile">保存</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <el-tab-pane label="修改密码" name="password">
              <el-form
                ref="passwordFormRef"
                :model="passwordForm"
                :rules="passwordRules"
                label-width="100px"
              >
                <el-form-item label="原密码" prop="oldPassword">
                  <el-input
                    v-model="passwordForm.oldPassword"
                    type="password"
                    show-password
                  />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input
                    v-model="passwordForm.newPassword"
                    type="password"
                    show-password
                  />
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input
                    v-model="passwordForm.confirmPassword"
                    type="password"
                    show-password
                  />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="changePassword">修改</el-button>
                  <el-button @click="resetPasswordForm">重置</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const activeTab = ref('basic')
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const userInfo = ref({})

const profileForm = reactive({
  realName: '',
  phone: '',
  email: ''
})

const passwordFormRef = ref()
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const loadUserInfo = () => {
  userInfo.value = userStore.userInfo || {}
  profileForm.realName = userInfo.value.realName || ''
  profileForm.phone = userInfo.value.phone || ''
  profileForm.email = userInfo.value.email || ''
}

const saveProfile = () => {
  // TODO: 调用更新个人信息接口
  ElMessage.success('保存成功')
}

const changePassword = async () => {
  const valid = await passwordFormRef.value.validate().catch(() => false)
  if (!valid) return

  // TODO: 调用修改密码接口
  ElMessage.success('密码修改成功')
  resetPasswordForm()
}

const resetPasswordForm = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordFormRef.value?.resetFields()
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped lang="scss">
.profile-container {
  .profile-header {
    text-align: center;
    padding: 20px 0;

    h3 {
      margin: 10px 0 5px;
    }

    p {
      color: #909399;
      margin: 0;
    }
  }

  .profile-info {
    padding: 20px;

    .info-item {
      display: flex;
      justify-content: space-between;
      padding: 10px 0;
      border-bottom: 1px solid #ebeef5;

      &:last-child {
        border-bottom: none;
      }

      .label {
        color: #909399;
      }
    }
  }
}
</style>
