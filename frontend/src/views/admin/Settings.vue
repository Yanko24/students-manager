<template>
  <div class="settings-container">
    <div class="page-header">
      <h2>系统设置</h2>
    </div>

    <el-card class="settings-card">
      <el-tabs v-model="activeTab" class="settings-tabs">
        <!-- 基本设置 -->
        <el-tab-pane label="基本设置" name="basic">
          <el-form :model="basicSettings" label-width="120px" class="settings-form">
            <el-form-item label="系统名称">
              <el-input v-model="basicSettings.systemName" style="width: 100%; max-width: 400px;" />
            </el-form-item>
            <el-form-item label="系统Logo">
              <el-upload class="logo-uploader" action="/api/upload" :show-file-list="false"
                :on-success="handleLogoSuccess" :before-upload="beforeLogoUpload">
                <img v-if="basicSettings.logo" :src="basicSettings.logo" class="logo" />
                <el-icon v-else class="logo-uploader-icon">
                  <Plus />
                </el-icon>
              </el-upload>
              <div class="upload-tip">建议尺寸：200x200px，支持 jpg、png 格式</div>
            </el-form-item>
            <el-form-item label="系统描述">
              <el-input v-model="basicSettings.description" type="textarea" :rows="3"
                style="width: 100%; max-width: 400px;" />
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 邮件设置 -->
        <el-tab-pane label="邮件设置" name="email">
          <el-form :model="emailSettings" label-width="120px" class="settings-form">
            <el-form-item label="SMTP服务器">
              <el-input v-model="emailSettings.smtpServer" style="width: 100%; max-width: 400px;" />
            </el-form-item>
            <el-form-item label="SMTP端口">
              <el-input v-model="emailSettings.smtpPort" style="width: 100%; max-width: 200px;" />
            </el-form-item>
            <el-form-item label="发件人邮箱">
              <el-input v-model="emailSettings.senderEmail" style="width: 100%; max-width: 400px;" />
            </el-form-item>
            <el-form-item label="邮箱密码">
              <el-input v-model="emailSettings.emailPassword" type="password" show-password
                style="width: 100%; max-width: 400px;" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="testEmailSettings">测试邮件发送</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 安全设置 -->
        <el-tab-pane label="安全设置" name="security">
          <el-form :model="securitySettings" label-width="120px" class="settings-form">
            <el-form-item label="密码最小长度">
              <el-input-number v-model="securitySettings.minPasswordLength" :min="6" :max="20" style="width: 200px;" />
            </el-form-item>
            <el-form-item label="密码复杂度">
              <el-checkbox-group v-model="securitySettings.passwordRules" class="checkbox-group">
                <el-checkbox value="uppercase">必须包含大写字母</el-checkbox>
                <el-checkbox value="lowercase">必须包含小写字母</el-checkbox>
                <el-checkbox value="numbers">必须包含数字</el-checkbox>
                <el-checkbox value="special">必须包含特殊字符</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
            <el-form-item label="登录失败次数">
              <el-input-number v-model="securitySettings.maxLoginAttempts" :min="1" :max="10" style="width: 200px;" />
            </el-form-item>
            <el-form-item label="账号锁定时间">
              <div class="lockout-duration">
                <el-input-number v-model="securitySettings.lockoutDuration" :min="1" :max="1440"
                  style="width: 200px;" />
                <span class="unit-label">分钟</span>
              </div>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>

      <div class="form-actions">
        <el-button type="primary" @click="saveSettings">保存设置</el-button>
        <el-button @click="resetSettings">重置</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
  import { ref, reactive } from 'vue'
  import { ElMessage } from 'element-plus'
  import { Plus } from '@element-plus/icons-vue'

  const activeTab = ref('basic')

  // 基本设置
  const basicSettings = reactive({
    systemName: '知行教务',
    logo: '',
    description: '面向高校教学运行与学生培养的教务管理系统'
  })

  // 邮件设置
  const emailSettings = reactive({
    smtpServer: 'smtp.example.com',
    smtpPort: '587',
    senderEmail: 'admin@example.com',
    emailPassword: ''
  })

  // 安全设置
  const securitySettings = reactive({
    minPasswordLength: 8,
    passwordRules: ['uppercase', 'lowercase', 'numbers'],
    maxLoginAttempts: 5,
    lockoutDuration: 30
  })

  // 方法
  const handleLogoSuccess = (response) => {
    basicSettings.logo = response.url
    ElMessage.success('Logo上传成功')
  }

  const beforeLogoUpload = (file) => {
    const isImage = file.type.startsWith('image/')
    const isLt2M = file.size / 1024 / 1024 < 2

    if (!isImage) {
      ElMessage.error('只能上传图片文件！')
      return false
    }
    if (!isLt2M) {
      ElMessage.error('图片大小不能超过 2MB！')
      return false
    }
    return true
  }

  const testEmailSettings = () => {
    // TODO: 实现邮件测试功能
    ElMessage.success('测试邮件发送成功')
  }

  const saveSettings = () => {
    // TODO: 实现保存设置功能
    ElMessage.success('设置保存成功')
  }

  const resetSettings = () => {
    // TODO: 实现重置设置功能
    ElMessage.warning('确定要重置所有设置吗？')
  }
</script>

<style lang="scss" scoped>
  .settings-container {
    height: 100%;
    padding: 20px;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;

    .page-header {
      margin-bottom: 20px;

      h2 {
        margin: 0;
        font-size: 24px;
        color: #303133;
      }
    }

    .settings-card {
      flex: 1;
      display: flex;
      flex-direction: column;

      :deep(.el-card__body) {
        height: 100%;
        display: flex;
        flex-direction: column;
      }

      .settings-tabs {
        flex: 1;
        display: flex;
        flex-direction: column;

        :deep(.el-tabs__content) {
          flex: 1;
          overflow-y: auto;
          padding: 20px;
        }

        :deep(.el-tabs__header) {
          margin: 0;
        }
      }

      .settings-form {
        max-width: 800px;
        margin: 0 auto;
        width: 100%;

        .lockout-duration {
          display: flex;
          align-items: center;
          gap: 8px;

          .unit-label {
            color: #606266;
            white-space: nowrap;
          }
        }
      }

      .logo-uploader {
        :deep(.el-upload) {
          border: 1px dashed #d9d9d9;
          border-radius: 6px;
          cursor: pointer;
          position: relative;
          overflow: hidden;
          transition: var(--el-transition-duration-fast);

          &:hover {
            border-color: var(--el-color-primary);
          }
        }
      }

      .logo-uploader-icon {
        font-size: 28px;
        color: #8c939d;
        width: 100px;
        height: 100px;
        text-align: center;
        line-height: 100px;
      }

      .logo {
        width: 100px;
        height: 100px;
        display: block;
      }

      .upload-tip {
        margin-top: 10px;
        color: #909399;
        font-size: 14px;
      }

      .checkbox-group {
        display: flex;
        flex-direction: column;
        gap: 10px;
      }

      .form-actions {
        margin-top: auto;
        text-align: center;
        padding: 20px;
        border-top: 1px solid #ebeef5;
      }
    }
  }

  @media screen and (max-width: 768px) {
    .settings-container {
      padding: 10px;

      .settings-card {
        .settings-tabs {
          :deep(.el-tabs__content) {
            padding: 10px;
          }
        }

        .settings-form {
          padding: 0;
        }

        .el-form-item {
          margin-bottom: 18px;
        }
      }
    }
  }
</style>
