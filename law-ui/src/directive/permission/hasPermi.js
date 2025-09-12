 /**
 * v-hasPermi 操作权限处理
 * Copyright (c) 2019 ruoyi
 */

import store from '@/store'

export default {
  inserted(el, binding, vnode) {
    const { value } = binding
    const all_permission = "*:*:*"
    const permissions = store.getters && store.getters.permissions

    console.log('=== 前端权限验证调试 ===')
    console.log('验证权限:', value)
    console.log('用户权限数量:', permissions ? permissions.length : 0)
    console.log('用户权限列表:', permissions)

    if (value && value instanceof Array && value.length > 0) {
      const permissionFlag = value

      const hasPermissions = permissions.some(permission => {
        return all_permission === permission || permissionFlag.includes(permission)
      })

      console.log('权限验证结果:', hasPermissions)
      console.log('=== 前端权限验证调试结束 ===')

      if (!hasPermissions) {
        el.parentNode && el.parentNode.removeChild(el)
      }
    } else {
      throw new Error(`请设置操作权限标签值`)
    }
  }
}
