## **项目运行指南**

本项目使用的技术栈有pnpm、VUE、element plus、echarts、axios、less、sass，需要安装对应的环境。

**项目前提**：需要18.3或更高版本的node.js，可在windows powershell（管理员）内输入node -v查看，若无，则建议浏览器搜索安装方法。

打开windows powershell（管理员），运行以下内容：

​	pnpm安装：npm install -g pnpm

​	axios安装：pnpm install axios -g

​	若遇到环境变量相关问题还请自行搜索或询问gpt。

使用VScode打开项目文件夹（运行前端部分时打开front_end文件夹），在终端（按ctrl+`快捷键打开）中安装以下内容（均在front_end文件夹中进行安装和运行）：

​	element plus安装：pnpm install element-plus

​	echarts安装：pnpm install echarts

​	less安装：pnpm i -D less

​	sass安装：pnpm install sass

安装完毕后输入pnpm dev即可运行项目。

测试文件运行：npm run test
