<template>
  <div class="header">
    <!-- 折叠按钮 -->
    <div class="collapse-btn" @click="collapseChange">
      <el-icon v-if="!collapse">
        <Fold />
      </el-icon>
      <el-icon v-else>
        <Expand />
      </el-icon>
    </div>
    <div class="logo"><span style="color: #434040;font-weight: bold;">Flow</span>Engine</div>
    <div class="header-right">
      <div class="header-user-con">
        <!-- 用户头像 -->
        <div class="user-avator">
          <img src="../images/header.jpg" />
        </div>
        <!-- 用户名下拉菜单 -->
        <el-dropdown class="user-name" trigger="click" @command="handleCommand">
          <span class="el-dropdown-link">
            {{ username }}
            <el-icon>
              <CaretBottom />
            </el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item divided command="loginout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>
<script lang="ts" setup name="CompHeader">
import { computed, onMounted, ref } from "vue";
import { userStore } from "../stores/counter";
import { useRouter } from "vue-router";
import screenfull from "screenfull";

const message = 2;
const store = userStore();
const collapse = computed(() => store.meunIsCollapsed);
const username = computed(() => store.loginUser);

// 侧边栏折叠
const collapseChange = () => {
  store.setMeunIsCollapsed(!collapse.value);
};

onMounted(() => {
  if (document.body.clientWidth < 1500) {
    collapseChange();
  }
});

// 用户名下拉菜单选择事件
const router = useRouter();
const handleCommand = (command: string) => {
  if (command == "loginout") {
    localStorage.removeItem("ms_username");
    store.clearAllTags();
    router.push("/login");
  } else if (command == "user") {
    router.push("/user");
  }
};

</script>
<style lang="less" scoped>
.header {
  position: relative;
  box-sizing: border-box;
  width: 100%;
  height: 50px;
  font-size: 22px;
  color: #434040;
  background-color: #F2F2F2;

  .logo {
    float: left;
    width: 250px;
    line-height: 50px;
    font-family: 'Roboto', sans-serif; 
  }

  .collapse-btn {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100%;
    float: left;
    padding: 0 21px;
    cursor: pointer;
  }

  .header-right {
    float: right;
    padding-right: 30px;

    .header-user-con {
      display: flex;
      height: 50px;
      align-items: center;
      font-size: 24px;

      .btn-bell {
        margin-left: 5px;
        position: relative;
        text-align: center;
        width: 24px;
        height: 24px;
        cursor: pointer;

        .el-icon {
          color: #fff;
          position: absolute;
          top: 0;
          left: 0;
        }

        .btn-bell-badge {
          position: absolute;
          right: 0;
          top: -2px;
          width: 8px;
          height: 8px;
          border-radius: 4px;
          background: #f56c6c;
          color: #fff;
        }
      }

      .user-avator {
        margin-left: 20px;

        img {
          display: block;
          width: 32px;
          height: 32px;
          border-radius: 50%;
        }
      }

      .user-name {
        margin-left: 10px;

        .el-dropdown-link {
          color: #434040;
          cursor: pointer;
        }
      }
    }
  }
}
</style>
