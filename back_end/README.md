# ����������
1. ������Ŀ��·��Ϊ��
   back_end/src/main/java/com/photoclassificationsystem/PhotoClassificationSystemApplication.java

2. �����Ĭ������ **OpenGauss** ���������ڸü�����Ҫ��Ϊ���ṩ֧�֣�
   ������Ŀ������ȱ���ʽ�֧�֣����Զ��ر�OpenGauss��
   ����Ҫ���� **SQLite** �������ݿ⣬��������µ������޸ģ�
   1. �޸� **application.yml** �ļ���
      ·����back_end/src/main/resources/application.yml
      ��Ҫ��Ĭ�ϱ�ע�͵�����3-��4ȡ��ע�ͣ�ͬʱע�͵���5-��15
   2. �޸� **schema.sql** �ļ���
      ·����back_end/src/main/resources/schema.sql
      ��Ҫ����1-��91�Ĵ���ȫ��ע�ͣ�ͬʱȡ��ע����93-��107�Ĵ���
   3. �޸� **ResponseServiceImpl** �ļ���
      ·����back_end/src/main/java/com/photoclassificationsystem/service/Impl/ResponseServiceImpl.java
      ��Ҫ����75����ע�ͣ�ͬʱȡ����77��ע��