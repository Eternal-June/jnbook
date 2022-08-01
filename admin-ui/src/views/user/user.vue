<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input
        v-model="listQuery.username"
        clearable
        class="filter-item"
        style="width: 200px"
        placeholder="请输入用户名"
      />
      <el-input
        v-model="listQuery.mobile"
        clearable
        class="filter-item"
        style="width: 200px"
        placeholder="请输入手机号"
      />
      <el-input
        v-model="listQuery.compusname"
        clearable
        class="filter-item"
        style="width: 200px"
        placeholder="请输入院校名称"
      />
      <el-button
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="handleFilter"
        >查找</el-button
      >
      <el-button
        :loading="downloadLoading"
        class="filter-item"
        type="primary"
        icon="el-icon-download"
        @click="handleDownload"
        >导出</el-button
      >
    </div>

    <!-- 查询结果 -->
    <el-table
      v-loading="listLoading"
      :data="list"
      size="small"
      element-loading-text="正在查询中。。。"
      border
      fit
      highlight-current-row
    >
      <el-table-column align="center" width="100px" label="用户ID" prop="id" sortable />

      <el-table-column align="center" label="用户名" prop="username" />

      <el-table-column align="center" label="手机号码" prop="mobile" />

      <el-table-column align="center" label="所在院校" prop="compusname" />

      <el-table-column align="center" label="用户身份" prop="userLevel" width="80">
        <template slot-scope="scope">
          <el-tag>{{ levelDic[scope.row.userLevel] }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="身份操作" width="100">
        <template slot-scope="scope">
          <el-button
            v-permission="['GET /admin/user/detailApprove']"
            v-if="scope.row.status == 0 && scope.row.userLevel == 2"
            type="primary"
            size="mini"
            @click="handleDetail(scope.row)"
            >取消代理</el-button
          >
          <el-button
            v-permission="['POST /admin/user/approveAgency']"
            v-else-if="
              scope.row.status == 3 &&
              (scope.row.userLevel == 0 || scope.row.userLevel == 1)
            "
            type="primary"
            size="mini"
            @click="handleApproveAgency(scope.row)"
            >审批代理</el-button
          >
        </template>
      </el-table-column>

      <el-table-column align="center" label="状态" prop="status">
        <template slot-scope="scope">
          <el-tag>{{ statusDic[scope.row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="状态操作" prop="status" width="80">
        <template slot-scope="scope">
          <el-button
            v-permission="['POST /admin/user/ban']"
            v-if="scope.row.status == 1"
            type="primary"
            size="mini"
            @click="handleBan(scope.row)"
            >解禁</el-button
          >
          <el-button
            v-permission="['POST /admin/user/ban']"
            v-else-if="scope.row.status == 0"
            type="danger"
            size="mini"
            @click="handleBan(scope.row)"
            >禁用</el-button
          >
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        label="操作"
        width="80"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope">
          <el-button
            v-permission="['POST /admin/user/delete']"
            type="danger"
            size="mini"
            @click="handleDelete(scope.row)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.limit"
      @pagination="getList"
    />
  </div>
</template>

<script>
import { fetchList, approveAgency, cancelAgency, deleteUser, banUser } from "@/api/user";
import Pagination from "@/components/Pagination"; // Secondary package based on el-pagination

export default {
  name: "User",
  components: { Pagination },
  data() {
    return {
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        username: undefined,
        mobile: undefined,
        compusname: undefined,
        sort: "add_time",
        order: "desc",
      },
      downloadLoading: false,
      genderDic: ["未知", "男", "女"],
      levelDic: ["普通用户", "普通用户", "校区代理"],
      statusDic: ["可用", "禁用", "注销", "申请"],
      detailDialogVisible: false,
      agencyDetail: {},
      approveDialogVisible: false,
      approveForm: {
        userId: undefined,
        settlementRate: undefined,
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.listLoading = true;
      fetchList(this.listQuery)
        .then((response) => {
          this.list = response.data.data.items;
          this.total = response.data.data.total;
          this.listLoading = false;
        })
        .catch(() => {
          this.list = [];
          this.total = 0;
          this.listLoading = false;
        });
    },
    handleFilter() {
      this.listQuery.page = 1;
      this.getList();
    },
    handleDetail(row) {
      cancelAgency(row)
        .then((response) => {
          this.$notify.success({
            title: "成功",
            message: "取消成功",
          });
          this.getList();
        })
        .catch((response) => {
          this.$notify.error({
            title: "取消失败",
            message: response.data.errmsg,
          });
        });
    },
    handleApproveAgency(row) {
      approveAgency(row)
        .then((response) => {
          this.$notify.success({
            title: "成功",
            message: "审批成功",
          });
          this.getList();
        })
        .catch((response) => {
          this.$notify.error({
            title: "审批失败",
            message: response.data.errmsg,
          });
        });
    },
    handleDelete(row) {
      deleteUser(row)
        .then((response) => {
          this.$notify.success({
            title: "成功",
            message: "删除用户成功",
          });
          const index = this.list.indexOf(row);
          this.list.splice(index, 1);
        })
        .catch((response) => {
          this.$notify.error({
            title: "失败",
            message: response.data.errmsg,
          });
        });
    },
    handleBan(row) {
      banUser(row)
        .then((response) => {
          this.$notify.success({
            title: "成功",
            message: "操作成功",
          });
          const index = this.list.indexOf(row);
          this.list.splice(index, 1);
        })
        .catch((response) => {
          this.$notify.error({
            title: "操作失败",
            message: response.data.errmsg,
          });
        });
    },
    handleDownload() {
      this.downloadLoading = true;
      import("@/vendor/Export2Excel").then((excel) => {
        const tHeader = ["用户名", "手机号码", "性别", "生日", "状态", "所在院校"];
        const filterVal = [
          "username",
          "mobile",
          "gender",
          "birthday",
          "status",
          "compusname",
        ];
        excel.export_json_to_excel2(tHeader, this.list, filterVal, "用户信息");
        this.downloadLoading = false;
      });
    },
  },
};
</script>
