<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8" />
  <title>登录</title>
  <link rel="stylesheet" href="https://unpkg.com/element-plus/dist/index.css">
  <script src="https://unpkg.com/vue@3"></script>
  <script src="https://unpkg.com/element-plus"></script>
  <script src="https://unpkg.com/axios/dist/axios.min.js"></script>
</head>
<body>
<div id="app" style="max-width: 420px;margin: 60px auto;">
  <el-card>
    <h3>用户登录</h3>
    <el-form label-width="80px">
      <el-form-item label="账号">
        <el-input v-model="username"></el-input>
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="password" type="password" show-password></el-input>
      </el-form-item>
      <el-button type="primary" @click="login">登录</el-button>
    </el-form>
    <div style="margin-top:10px;color:#666;">
      测试账号：admin / 123456
    </div>
  </el-card>
</div>

<script>
const { createApp, ref } = Vue;

createApp({
  setup(){
    const username = ref("");
    const password = ref("");

    async function login(){
      const form = new URLSearchParams();
      form.append("username", username.value);
      form.append("password", password.value);

      const res = await axios.post("<%=request.getContextPath()%>/api/auth/login", form);
      if(res.data.code === 0){
        window.location.href = "<%=request.getContextPath()%>/pages/products.jsp";
      }else{
        ElementPlus.ElMessage.error(res.data.msg);
      }
    }

    return { username, password, login };
  }
}).use(ElementPlus).mount("#app");
</script>
</body>
</html>
