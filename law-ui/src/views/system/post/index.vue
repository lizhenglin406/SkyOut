<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="岗位编码" prop="postCode">
        <el-input
          v-model="queryParams.postCode"
          placeholder="请输入岗位编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="岗位名称" prop="postName">
        <el-input
          v-model="queryParams.postName"
          placeholder="请输入岗位名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="岗位状态" clearable>
          <el-option
            v-for="dict in dict.type.sys_normal_disable"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:post:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:post:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:post:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:post:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="postList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="岗位编号" align="center" prop="postId" />
      <el-table-column label="岗位编码" align="center" prop="postCode" />
      <el-table-column label="岗位名称" align="center" prop="postName" />
      <el-table-column label="岗位排序" align="center" prop="postSort" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:post:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-key"
            @click="handleAuthMenu(scope.row)"
            v-hasPermi="['system:post:edit']"
          >权限</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:post:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改岗位对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="岗位名称" prop="postName">
          <el-input v-model="form.postName" placeholder="请输入岗位名称" />
        </el-form-item>
        <el-form-item label="岗位编码" prop="postCode">
          <el-input v-model="form.postCode" placeholder="请输入编码名称" />
        </el-form-item>
        <el-form-item label="岗位顺序" prop="postSort">
          <el-input-number v-model="form.postSort" controls-position="right" :min="0" />
        </el-form-item>
        <el-form-item label="岗位状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in dict.type.sys_normal_disable"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 分配岗位菜单权限对话框 -->
    <el-dialog :title="title" :visible.sync="openMenu" width="600px" append-to-body>
      <el-form :model="form" label-width="80px">
        <el-form-item label="岗位名称">
          <el-input v-model="form.postName" :disabled="true" />
        </el-form-item>
        <el-form-item label="权限字符">
          <el-input v-model="form.postCode" :disabled="true" />
        </el-form-item>
        <el-form-item label="菜单权限">
          <el-checkbox
            v-model="menuExpand"
            @change="handleCheckedTreeExpand($event, 'menu')"
          >展开/折叠</el-checkbox>
          <el-checkbox
            v-model="menuNodeAll"
            @change="handleCheckedTreeNodeAll($event, 'menu')"
          >全选/全不选</el-checkbox>
          <el-checkbox
            v-model="menuCheckStrictly"
            @change="handleCheckedTreeConnect($event, 'menu')"
          >父子联动</el-checkbox>
          <el-tree
            class="tree-border"
            :data="menuOptions"
            show-checkbox
            ref="menu"
            node-key="id"
            :check-strictly="!menuCheckStrictly"
            empty-text="加载中，请稍候"
            :props="{ label: 'label', children: 'children' }"
          ></el-tree>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitAuthMenu">确 定</el-button>
        <el-button @click="cancelAuthMenu">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listPost, getPost, delPost, addPost, updatePost, getPostMenu, updatePostMenu } from "@/api/system/post"
import { treeselect as menuTreeselect } from "@/api/system/menu"

export default {
  name: "Post",
  dicts: ['sys_normal_disable'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 岗位表格数据
      postList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示菜单权限弹出层
      openMenu: false,
      // 菜单列表
      menuOptions: [],
      // 菜单权限
      menuExpand: false,
      menuNodeAll: false,
      menuCheckStrictly: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        postCode: undefined,
        postName: undefined,
        status: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        postName: [
          { required: true, message: "岗位名称不能为空", trigger: "blur" }
        ],
        postCode: [
          { required: true, message: "岗位编码不能为空", trigger: "blur" }
        ],
        postSort: [
          { required: true, message: "岗位顺序不能为空", trigger: "blur" }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询岗位列表 */
    getList() {
      this.loading = true
      listPost(this.queryParams).then(response => {
        this.postList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        postId: undefined,
        postCode: undefined,
        postName: undefined,
        postSort: 0,
        status: "0",
        remark: undefined
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.postId)
      this.single = selection.length!=1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加岗位"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const postId = row.postId || this.ids
      getPost(postId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改岗位"
      })
    },
    /** 提交按钮 */
    submitForm: function() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.postId != undefined) {
            updatePost(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addPost(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const postIds = row.postId || this.ids
      this.$modal.confirm('是否确认删除岗位编号为"' + postIds + '"的数据项？').then(function() {
        return delPost(postIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/post/export', {
        ...this.queryParams
      }, `post_${new Date().getTime()}.xlsx`)
    },
    /** 分配菜单权限操作 */
    handleAuthMenu(row) {
      this.reset()
      const postId = row.postId
      this.form = Object.assign({}, row)
      this.openMenu = true
      this.title = "分配菜单权限"
      console.log('开始分配菜单权限，岗位ID:', postId)
      
      // 先获取菜单树结构
      this.getMenuTreeselect().then(() => {
        console.log('菜单树结构获取完成，菜单选项数据长度:', this.menuOptions ? this.menuOptions.length : 0)
        
        // 等待对话框完全渲染
        this.$nextTick(() => {
          // 先清空菜单树的选中状态
          if (this.$refs.menu) {
            this.$refs.menu.setCheckedKeys([])
            console.log('对话框打开时清空菜单树选中状态')
          }
          
          setTimeout(() => {
            // 获取岗位权限数据
            this.getMenuTree(postId)
          }, 200)
        })
      })
    },
    /** 查询菜单树结构 */
    getMenuTreeselect() {
      return menuTreeselect().then(response => {
        console.log('菜单树结构响应:', response.data)
        console.log('菜单树结构类型:', typeof response.data)
        console.log('菜单树结构长度:', response.data ? response.data.length : 0)
        if (response.data && response.data.length > 0) {
          console.log('第一个菜单项:', response.data[0])
        }
        this.menuOptions = response.data
      }).catch(error => {
        console.error('获取菜单树结构失败:', error)
      })
    },
    /** 查询岗位菜单权限 */
    getMenuTree(postId) {
      return getPostMenu(postId).then(response => {
        console.log('岗位菜单权限响应:', response)
        console.log('checkedKeys类型:', typeof response.checkedKeys)
        console.log('checkedKeys长度:', response.checkedKeys ? response.checkedKeys.length : 0)
        
        // 确保DOM元素已经准备好
        this.$nextTick(() => {
          console.log('菜单树组件引用:', this.$refs.menu)
          console.log('菜单选项数据长度:', this.menuOptions ? this.menuOptions.length : 0)
          
          if (this.$refs.menu) {
            console.log('菜单树组件方法:', Object.getOwnPropertyNames(this.$refs.menu))
            
            // 等待菜单树完全渲染
            setTimeout(() => {
              let checkedKeys = response.checkedKeys || []
              console.log('准备设置选中的菜单节点:', checkedKeys)
              
              // 先清空所有选中状态
              this.$refs.menu.setCheckedKeys([])
              
              // 如果有选中的节点，则设置选中状态
              if (checkedKeys.length > 0) {
                this.$refs.menu.setCheckedKeys(checkedKeys)
                console.log('已设置选中的菜单节点:', checkedKeys)
              } else {
                console.log('没有选中的菜单节点，保持清空状态')
              }
              
              // 验证设置是否成功
              setTimeout(() => {
                const actualCheckedKeys = this.$refs.menu.getCheckedKeys()
                console.log('验证选中的节点:', actualCheckedKeys)
                console.log('期望的节点数量:', checkedKeys.length)
                console.log('实际的节点数量:', actualCheckedKeys.length)
              }, 200)
            }, 300)
          } else {
            console.error('菜单树组件未找到')
          }
        })
        return response
      }).catch(error => {
        console.error('获取岗位菜单权限失败:', error)
        throw error
      })
    },
    // 树权限（展开/折叠）
    handleCheckedTreeExpand(value, type) {
      if (type == 'menu') {
        let treeList = this.menuOptions
        for (let i = 0; i < treeList.length; i++) {
          this.$refs.menu.store.nodesMap[treeList[i].id].expanded = value
        }
      }
    },
    // 树权限（全选/全不选）
    handleCheckedTreeNodeAll(value, type) {
      if (type == 'menu') {
        this.$refs.menu.setCheckedNodes(value ? this.menuOptions : [])
      }
    },
    // 树权限（父子联动）
    handleCheckedTreeConnect(value, type) {
      if (type == 'menu') {
        this.menuCheckStrictly = value
      }
    },
    /** 提交按钮（菜单权限） */
    submitAuthMenu() {
      const postId = this.form.postId
      this.form.menuIds = this.getMenuAllCheckedKeys()
      console.log('提交岗位权限分配:', this.form)
      updatePostMenu(this.form).then(response => {
        console.log('权限分配成功:', response)
        this.$modal.msgSuccess("分配成功")
        this.openMenu = false
      }).catch(error => {
        console.error('权限分配失败:', error)
        this.$modal.msgError("分配失败")
      })
    },
    /** 取消按钮（菜单权限） */
    cancelAuthMenu() {
      this.openMenu = false
      this.reset()
    },
    // 获取所有选中节点
    getMenuAllCheckedKeys() {
      // 目前被选中的菜单节点
      let checkedKeys = this.$refs.menu.getCheckedKeys()
      // 半选中的菜单节点
      let halfCheckedKeys = this.$refs.menu.getHalfCheckedKeys()
      console.log('选中的菜单节点:', checkedKeys)
      console.log('半选中的菜单节点:', halfCheckedKeys)
      checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys)
      console.log('最终选中的菜单节点:', checkedKeys)
      return checkedKeys
    }
  }
}
</script>

<style scoped>
.tree-border {
  margin-top: 5px;
  border: 1px solid #e5e6e7;
  background: #ffffff none;
  border-radius: 4px;
  width: 100%;
}
</style>
