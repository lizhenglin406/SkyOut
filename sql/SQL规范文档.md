# SQL规范文档

## 1. 概述

本文档基于若依框架的SQL文件分析，制定了统一的SQL开发规范，旨在提高代码质量、可读性和可维护性。

## 2. 命名规范

### 2.1 表命名规范

- **表名格式**: 使用小写字母和下划线分隔
- **表名前缀**: 系统表使用 `sys_` 前缀，业务表使用业务模块前缀
- **命名示例**:
  ```sql
  sys_user          -- 用户表
  sys_dept          -- 部门表
  sys_role          -- 角色表
  QRTZ_JOB_DETAILS  -- Quartz任务详情表
  ```

### 2.2 字段命名规范

- **字段名格式**: 使用小写字母和下划线分隔
- **主键字段**: 统一使用 `表名_id` 格式
- **外键字段**: 使用 `关联表名_id` 格式
- **状态字段**: 使用 `status` 或 `del_flag`
- **时间字段**: 使用 `create_time`、`update_time`
- **操作人字段**: 使用 `create_by`、`update_by`

### 2.3 索引命名规范

- **主键索引**: 自动生成，无需命名
- **唯一索引**: `uk_表名_字段名`
- **普通索引**: `idx_表名_字段名`
- **复合索引**: `idx_表名_字段1_字段2`

## 3. 表结构规范

### 3.1 基础字段规范

每个业务表必须包含以下基础字段：

```sql
-- 主键
id bigint(20) not null auto_increment comment '主键ID',

-- 状态字段
status char(1) default '0' comment '状态（0正常 1停用）',
del_flag char(1) default '0' comment '删除标志（0代表存在 2代表删除）',

-- 审计字段
create_by varchar(64) default '' comment '创建者',
create_time datetime comment '创建时间',
update_by varchar(64) default '' comment '更新者',
update_time datetime comment '更新时间',

-- 备注字段
remark varchar(500) default null comment '备注'
```

### 3.2 数据类型规范

| 用途 | 数据类型 | 长度 | 说明 |
|------|----------|------|------|
| 主键ID | bigint(20) | 20 | 自增主键 |
| 用户ID | bigint(20) | 20 | 用户相关ID |
| 状态 | char(1) | 1 | 固定长度状态字段 |
| 删除标志 | char(1) | 1 | 固定长度删除标志 |
| 用户名 | varchar(30) | 30 | 用户名长度限制 |
| 密码 | varchar(100) | 100 | 加密后密码长度 |
| 邮箱 | varchar(50) | 50 | 邮箱地址长度 |
| 手机号 | varchar(11) | 11 | 手机号固定长度 |
| IP地址 | varchar(128) | 128 | IP地址长度 |
| 操作人 | varchar(64) | 64 | 操作人字段长度 |
| 备注 | varchar(500) | 500 | 备注字段长度 |
| 长文本 | longtext | - | 大文本内容 |

### 3.3 约束规范

```sql
-- 主键约束
primary key (id)

-- 唯一约束
unique key uk_table_field (field_name)

-- 外键约束
foreign key (field_name) references parent_table(parent_field)

-- 非空约束
not null

-- 默认值约束
default '0'
```

## 4. 注释规范

### 4.1 表注释

```sql
create table table_name (
    -- 字段定义
) engine=innodb comment = '表功能描述';
```

### 4.2 字段注释

```sql
field_name varchar(50) not null comment '字段功能描述'
```

### 4.3 索引注释

```sql
key idx_table_field (field_name) comment '索引说明'
```

## 5. 索引规范

### 5.1 必须创建的索引

- **主键索引**: 自动创建
- **外键索引**: 所有外键字段必须创建索引
- **状态字段索引**: `status`、`del_flag` 字段
- **时间字段索引**: `create_time`、`update_time` 字段
- **查询字段索引**: 经常用于查询条件的字段

### 5.2 索引命名示例

```sql
-- 单字段索引
key idx_sys_user_status (status),
key idx_sys_user_create_time (create_time)

-- 复合索引
key idx_sys_oper_log_bt (business_type),
key idx_sys_oper_log_s (status),
key idx_sys_oper_log_ot (oper_time)
```

## 6. 存储引擎规范

- **统一使用**: InnoDB 存储引擎
- **原因**: 支持事务、行级锁定、外键约束

```sql
engine=innodb
```

## 7. 字符集规范

- **数据库字符集**: utf8mb4
- **排序规则**: utf8mb4_general_ci
- **原因**: 支持完整的UTF-8字符集，包括emoji等特殊字符

## 8. SQL语句规范

### 8.1 DDL语句规范

#### 8.1.1 创建表语句

```sql
-- 表结构注释
-- ----------------------------
-- 1、表功能描述
-- ----------------------------

-- 删除表（如果存在）
drop table if exists table_name;

-- 创建表
create table table_name (
    id bigint(20) not null auto_increment comment '主键ID',
    -- 其他字段定义
    primary key (id),
    -- 索引定义
    key idx_table_field (field_name)
) engine=innodb auto_increment=100 comment = '表功能描述';
```

#### 8.1.2 修改表语句

```sql
-- 添加字段
alter table table_name add column field_name varchar(50) default '' comment '字段描述';

-- 修改字段
alter table table_name modify column field_name varchar(100) default '' comment '字段描述';

-- 删除字段
alter table table_name drop column field_name;

-- 添加索引
alter table table_name add index idx_table_field (field_name);

-- 删除索引
alter table table_name drop index idx_table_field;
```

### 8.2 DML语句规范

#### 8.2.1 插入语句

```sql
-- 单条插入
insert into table_name (field1, field2, field3) values (value1, value2, value3);

-- 批量插入
insert into table_name (field1, field2, field3) values 
(value1, value2, value3),
(value4, value5, value6);
```

#### 8.2.2 更新语句

```sql
update table_name 
set field1 = value1, 
    field2 = value2,
    update_time = now()
where condition;
```

#### 8.2.3 删除语句

```sql
-- 物理删除（谨慎使用）
delete from table_name where condition;

-- 逻辑删除（推荐）
update table_name set del_flag = '2' where condition;
```

### 8.3 查询语句规范

#### 8.3.1 基本查询

```sql
select field1, field2, field3
from table_name
where condition
order by field1 desc
limit 10;
```

#### 8.3.2 关联查询

```sql
select t1.field1, t2.field2
from table1 t1
left join table2 t2 on t1.id = t2.table1_id
where t1.status = '0'
order by t1.create_time desc;
```

## 9. 数据初始化规范

### 9.1 初始化数据格式

```sql
-- ----------------------------
-- 初始化-表名数据
-- ----------------------------
insert into table_name values(value1, value2, value3);
insert into table_name values(value4, value5, value6);
```

### 9.2 系统数据规范

- **管理员账号**: 默认创建admin用户
- **默认角色**: 创建超级管理员和普通角色
- **系统配置**: 初始化必要的系统参数
- **字典数据**: 初始化系统字典类型和数据

## 10. 性能优化规范

### 10.1 查询优化

- 避免使用 `select *`，明确指定需要的字段
- 合理使用索引，避免全表扫描
- 使用 `limit` 限制返回结果数量
- 避免在 `where` 子句中使用函数

### 10.2 索引优化

- 为经常查询的字段创建索引
- 避免创建过多索引，影响写入性能
- 定期分析索引使用情况，删除无用索引

### 10.3 表结构优化

- 选择合适的数据类型，避免浪费存储空间
- 合理设计表结构，避免过度规范化或反规范化
- 定期清理历史数据，控制表大小

## 11. 安全规范

### 11.1 数据安全

- 敏感数据必须加密存储
- 密码字段使用不可逆加密
- 定期备份重要数据

### 11.2 SQL注入防护

- 使用参数化查询
- 对用户输入进行验证和转义
- 避免动态拼接SQL语句

## 12. 版本控制规范

### 12.1 脚本命名

- 格式: `V版本号_日期_功能描述.sql`
- 示例: `V1.0.0_20250101_初始化系统数据.sql`

### 12.2 脚本内容

- 包含完整的表结构定义
- 包含必要的数据初始化
- 包含索引和约束定义
- 添加详细的注释说明

## 13. 常见问题及解决方案

### 13.1 字符集问题

**问题**: 中文字符显示乱码
**解决**: 确保数据库、表、字段都使用utf8mb4字符集

### 13.2 索引问题

**问题**: 查询性能慢
**解决**: 分析查询语句，为常用查询字段添加索引

### 13.3 数据类型问题

**问题**: 数据精度丢失
**解决**: 选择合适的数据类型，如decimal用于金额字段

## 14. 附录

### 14.1 常用字段类型对照表

| MySQL类型 | 长度 | 用途 | 示例 |
|-----------|------|------|------|
| bigint | 20 | 主键、ID | 1234567890123456789 |
| varchar | 30 | 用户名 | admin |
| varchar | 50 | 邮箱 | user@example.com |
| varchar | 11 | 手机号 | 13800138000 |
| char | 1 | 状态 | 0, 1 |
| datetime | - | 时间 | 2025-01-01 12:00:00 |
| text | - | 长文本 | 文章内容 |
| longtext | - | 超长文本 | 富文本内容 |

### 14.2 状态码规范

| 状态码 | 含义 | 使用场景 |
|--------|------|----------|
| 0 | 正常/启用 | 通用状态 |
| 1 | 停用/禁用 | 通用状态 |
| 2 | 删除 | 逻辑删除标志 |

### 14.3 操作类型规范

| 操作码 | 含义 | 说明 |
|--------|------|------|
| 0 | 其他 | 其他操作 |
| 1 | 新增 | 新增操作 |
| 2 | 修改 | 修改操作 |
| 3 | 删除 | 删除操作 |
| 4 | 授权 | 授权操作 |
| 5 | 导出 | 导出操作 |
| 6 | 导入 | 导入操作 |
| 7 | 强退 | 强退操作 |
| 8 | 生成代码 | 生成操作 |
| 9 | 清空数据 | 清空操作 |

---

**文档版本**: V1.0  
**创建日期**: 2025-01-01  
**最后更新**: 2025-01-01  
**维护人员**: 开发团队