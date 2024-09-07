# 如何启动后端
1. 运行项目：路径为：
   back_end/src/main/java/com/photoclassificationsystem/PhotoClassificationSystemApplication.java

2. 本后端默认启用 **OpenGauss** ，但是由于该技术需要华为云提供支持，
   后续项目结束后，缺少资金支持，将自动关闭OpenGauss。
   若需要调用 **SQLite** 本地数据库，需完成以下的配置修改：
   1. 修改 **application.yml** 文件：
      路径：back_end/src/main/resources/application.yml
      需要将默认被注释掉的行3-行4取消注释，同时注释掉行5-行15
   2. 修改 **schema.sql** 文件：
      路径：back_end/src/main/resources/schema.sql
      需要将行1-行91的代码全部注释，同时取消注释行93-行107的代码
   3. 修改 **ResponseServiceImpl** 文件：
      路径：back_end/src/main/java/com/photoclassificationsystem/service/Impl/ResponseServiceImpl.java
      需要将行75进行注释，同时取消行77的注释