## **项目运行指南**

**项目前提**：需要18.3或更高版本的node.js，可在windows powershell（管理员）内输入node -v查看，若无，则建议浏览器搜索安装方法。

#### **前端运行：**

可见front_end文件夹README文件，也可见下文：

打开windows powershell（管理员），运行以下内容：

​	pnpm安装：npm install -g pnpm

使用VScode打开项目文件夹（运行前端部分时打开front_end文件夹），在终端（按ctrl+`快捷键打开）中安装以下内容（均在front_end文件夹中进行安装和运行）：

​	可先运行 pnpm install 安装环境，然后尝试 pnpm dev 启动，若缺少依赖，可根据需求安装以下内容：

​	axios安装：pnpm install axios

​	element plus安装：pnpm install element-plus

​	less安装：pnpm i -D less

​	sass安装：pnpm install sass

安装完毕后输入pnpm dev即可运行项目。

测试文件运行：npm run test

#### **ai运行：**

见ai文件夹README文件

#### 后端运行：

见back_end文件夹README文件

