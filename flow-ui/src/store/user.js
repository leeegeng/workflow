import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, getUserInfo } from '@/api/auth'
import { setToken, getToken, removeToken } from '@/utils/auth'

export const useUserStore = defineStore('user', () => {
  // State
  const token = ref(getToken())
  const userInfo = ref(null)

  // Getters
  const isLoggedIn = computed(() => !!token.value)

  // Actions
  const setUserToken = (newToken) => {
    token.value = newToken
    setToken(newToken)
  }

  const setUserInfo = (info) => {
    userInfo.value = info
  }

  const loginAction = async (loginData) => {
    const res = await login(loginData)
    if (res.code === 200) {
      setUserToken(res.data.accessToken)
      setUserInfo(res.data.userInfo)
      return true
    }
    return false
  }

  const getUserInfoAction = async () => {
    // 可以从token解析或调用接口获取
    return userInfo.value
  }

  const logoutAction = () => {
    token.value = ''
    userInfo.value = null
    removeToken()
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    setUserToken,
    setUserInfo,
    loginAction,
    getUserInfoAction,
    logoutAction
  }
})
