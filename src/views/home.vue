<script setup>
  import { ref } from 'vue'
  import userPic1 from '@/assets/img/userPic11.jpeg'

  const value = ref('')
  const search=(value)=>{
    console.log('success')
  }
  const value1 = ref('导入顺序')

  const afterRead = (file) => {
      file.status = 'uploading';
      file.message = '上传中...';

      setTimeout(() => {
        file.status = 'done';
        file.message = '上传完成';
      }, 1000);
      // 此时可以自行将文件上传至服务器
      console.log(file);
    };

    const fileList = ref([
      
    ]);

</script>

<template>
  <div class="main">
    <div class="title">照片分类</div>
    <div class="tab">
      <van-tabs v-model:active="active" shrink type="card" line-width="100vw">
        <div>
          <van-divider v-for="index in 4" :hairline="false" :style="{ color: '#1989fa', borderColor: '#1989fa', margin: '-0.5px',padding: '0 0 0 16.5px' } " />
        </div>
        <van-tab title="照片导入" class="input">
          <van-search v-model="value" placeholder="请输入搜索关键词" background="rgba(0,0,0,0)"  class="search" @search="search()"/>
          <el-dropdown placement="bottom" class="bottom-start" size="large">
            <el-button  class="listButton"> {{value1}} <el-icon class="el-icon--right"><arrow-down /></el-icon> </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item class="listButton">导入顺序</el-dropdown-item>
                <el-dropdown-item class="listButton">时间顺序</el-dropdown-item>
                <el-dropdown-item class="listButton">其他顺序</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <div class="box"></div>
          <van-uploader v-model="fileList" :after-read="afterRead" preview-size="10.5vw" :preview-full-image="true" :preview-options="{closeable: true}">
            <template #preview-cover="{ file }">
              <div class="preview-cover van-ellipsis">{{ file.name }}</div>
            </template>
          </van-uploader>
        </van-tab>
        <van-tab title="分类预览" class="preview">
          <div class="cell" v-for="index in 3">
            <div class="box2">
              <div class="className">20240821</div>
              <div class="more">查看更多</div>
            </div>
            <div class="arrowButton"><van-icon name="arrow-left" size="30px"/></div>
            <div class="pictureList">
              <van-image v-for="index in 10" class="picture" width="12vw" hidden="12vh" :src=userPic1 />
            </div>
            <div class="arrowButton"><van-icon name="arrow" size="30px"/></div>
          </div>
        </van-tab>
      </van-tabs>
    </div>
  </div>
  <div class="nav">
    <van-image
      width="17.1vw"
      :src=userPic1
    />
  <div class="littleTitle">图片名</div>
  <div class="classficiation">
    <van-tag v-for="index in 5" class="tag"  round size="large" color="rgb(204, 204, 204)" text-color="black" type="primary">标签</van-tag>
  </div>
  </div>
</template>

<style lang="less" scoped>
.main{
  float: left;
  width: 78vw;
  height: 100vh;
  background-color: rgb(243, 243, 243);
  .title{
    font-size: 1.8em;
    padding-top: 4px;
    padding-left: 18px;
    padding-bottom: 4px;
  }
  .tab{
    .input{
      position: relative;
      margin-left: 16px;
      padding-top: 10px;
      .search{
        width: 300px;
        height: 60px;
        float: left;
        margin-left: -10px;
        margin-top: -10px;
      }
      .box{
        height: 50px;
      }
      .preview-cover {
        position: absolute;
        bottom: 0;
        box-sizing: border-box;
        width: 100%;
        padding: 4px;
        color: #fff;
        font-size: 12px;
        text-align: center;
        background: rgba(0, 0, 0, 0.3);
      }
      .bottom-start{
        width: 220px;
        right: 15px;
        float: right;
        .listButton{
          width: 220px;
        }
      }
    }
    .preview{
      display:inline-block;
      margin-left: 16px;
      padding-top: 10px;
    }
  }
}
.cell{
  height: 30vh;
  width: 78vw;
  margin-top: 2vh;
  margin-bottom: 7vh;
  display:inline-block;
  .box2{
    width: 78vw;
    height: 5vh;
    .className{
      font-size: 1.6em;
      padding-bottom: 7px;
      font-weight: 100;
      padding-left: 3vw;
      width: 10vw;
      height: 3vh;
      float: left;
    }
    .more{
      font-size: 1.3em;
      margin-right: 1vw;
      height: 3vh;
      float: right;
    }
  }
}
.pictureList{
  width: 71vw;
  display:flex;
  justify-content:space-between;
  /* 设置超出滚动 */
  overflow-x:auto;
  float: left;
}
.picture{
  width:9.5vw;
  display:inline-block;
  margin-right:10px;
  /* 超出滚动的关键，没有它元素会自动缩小，不会滚动 */
  flex-shrink: 0;
}
::-webkit-scrollbar {
  /* 隐藏滚动条 */
  display: none;
}
.arrowButton{
  float: left ;
  height: 25vh;
  width: 3vw;
  display: flex;
  justify-content: center;
  align-items: center;
}
.nav{
  border-left: 4px solid rgb(41, 115, 243);
  padding-top: 10vh ;
  padding-left:1.5vw;
  width: 18.5vw;
  height: 90vh;
  float: right ;
  position: fixed;
  right: 0;
  .littleTitle{
    font-size: 1.1em;
    align-self:center;
    text-align: center;
  }
  .classficiation{
    margin: 8vh 2vw;
    text-align: center;
    transform: rotate(180deg);
    .tag{
      display: inline-block; 
      margin: 2px 10px;
      transform: rotate(180deg);
    }
  }
}
</style>