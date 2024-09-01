<script setup>
import { ref ,onMounted,onBeforeUnmount,computed,watch,nextTick} from 'vue'
import axios from 'axios';
// import EXIF from 'exif-js';
import defaultPic from '@/assets/img/default.jpg'
import userPic1 from '@/assets/img/userPic1.jpg'
import userPic2 from '@/assets/img/userPic2.jpeg'
import userPic3 from '@/assets/img/userPic3.jpg'
import userPic4 from '@/assets/img/userPic4.jpg'
import userPic5 from '@/assets/img/userPic5.jpg'
import userPic6 from '@/assets/img/userPic6.jpg'
import userPic7 from '@/assets/img/userPic7.jpeg'
import userPic8 from '@/assets/img/userPic8.jpg'
import userPic9 from '@/assets/img/userPic9.png'
import userPic10 from '@/assets/img/userPic10.jpg'

//页签栏
const active=ref(0)
const moreList=ref([])

//搜索
const value2 = ref('')
const value3 = ref('文件名')
const value4 = ref(true)
const sortList2=(sortType)=>{
  if(sortType!==value3){
    if(sortType==='文件名'){
      value3.value='文件名'
    }else if(sortType==='地点'){
      value3.value='地点'
    }else if(sortType==='设备'){
      value3.value='设备'
    }else{
      value3.value='标签'
    }
  }
}

//下拉菜单
const value1 = ref('导入顺序')
const sortList=(sortType)=>{
  if(sortType!==value1){
    if(sortType==='导入顺序'){
      value1.value='导入顺序'
    }else if(sortType==='时间顺序'){
      value1.value='时间顺序'
    }else if(sortType==='大小顺序'){
      value1.value='大小顺序'
    }else if(sortType==='文件名顺序'){
      value1.value='文件名顺序'
    }
  }
}

//真正的文件夹
const fileList = ref([
  {
    objectUrl: userPic1,
    status: 'uploading',
    message: '上传中...',
    uploadTime:1635475483259,
    place:'北京',
    device:'红米k40',
    file:{name:'八重.jpg',lastModified: 1635475483259,size:1000000,type:'image/jpeg',sort:['八重','狐狸','好看','原神','启动']}
  },
  {
    objectUrl: userPic2,
    status: 'failed',
    message: '上传失败',
    uploadTime:1672527600000,
    place:'上海',
    device:'华为',
    file:{name:'胡桃.jpeg',lastModified: 1709452800000,size:2000000,type:'image/jpeg',sort:['胡桃','堂主','可爱','原神','启动']}
  },
  {
    objectUrl: userPic4,
    status: 'done',
    message: '上传成功',
    uploadTime:1709452800000,
    place:'武汉',
    device:'锤子',
    file:{name:'77.jpg',lastModified: 1672527600000,size:3000000,type:'image/jpeg',sort:['77','僵尸','海报','原神','启动']}
  },
]);

//搜索和排序操作
const sortFileList = () => {
  let sortedList = [...fileList.value];

  if (value1.value === '导入顺序') {
      sortedList = sortedList.sort((a, b) => a.uploadTime - b.uploadTime);
  } else if (value1.value === '时间顺序') {
      sortedList = sortedList.sort((a, b) => b.file.lastModified - a.file.lastModified);
  } else if (value1.value === '大小顺序') {
      sortedList = sortedList.sort((a, b) => b.file.size - a.file.size);
  } else if (value1.value === '文件名顺序') {
      sortedList = sortedList.sort((a, b) => a.file.name.localeCompare(b.file.name));
  }

  if (value2.value) {
      if(value3.value==='文件名')sortedList = sortedList.filter(file => file.file.name.includes(value2.value));
      else  if(value3.value==='地点')sortedList = sortedList.filter(file => file.place.includes(value2.value));
      else  if(value3.value==='设备')sortedList = sortedList.filter(file => file.device.includes(value2.value));
      else sortedList = sortedList.filter(file => file.file.sort.some(tag => tag.includes(value2.value)))
  }

  if(!value4.value)sortedList.reverse()

  return sortedList;
};

//这是显示的预览文件夹，真正的文件夹被隐藏
const filteredFileList = computed(() => sortFileList());

//文件上传
const handleAfterRead = async (files) => {
  if (!Array.isArray(files)) {
    files = [files];
  }

  // 声明 uploadPromises 数组，用于存储每个文件上传的 Promise
  const uploadPromises = [];

for (const file of files) {
  const index = fileList.value.findIndex(item => item.objectUrl === file.objectUrl);
  if (index !== -1) {
    const selectedFile = fileList.value[index];
    selectedFile.status = 'uploading';
    selectedFile.message = '分类中';
  }

  const formData = new FormData();
  formData.append('image', file.file);

  // 创建上传的 Promise，并添加到 uploadPromises 数组中
  const uploadPromise = axios.post('http://localhost:5000/predict', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  }).then(async response => {  // 注意这里的 async
    console.log('Response Data:', response.data);
    const predictions = response.data;

    const index = fileList.value.findIndex(item => item.objectUrl === file.objectUrl);
    if (index !== -1) {
      const selectedFile = fileList.value[index];
      selectedFile.status = 'done';
      selectedFile.uploadTime = Date.now();
      selectedFile.file.sort = predictions.map(pred => pred.label);
      // 使用 nextTick 确保 UI 更新
      await nextTick();
    }
  }).catch(error => {
    console.error('Upload failed:', error.response ? error.response.data : error.message);
    const index = fileList.value.findIndex(item => item.objectUrl === file.objectUrl);
    if (index !== -1) {
      const selectedFile = fileList.value[index];
      selectedFile.status = 'failed';
      selectedFile.message = '分类失败';
    }
  });

  // 将每个上传 Promise 推入 uploadPromises 数组
  uploadPromises.push(uploadPromise);
}
  // 等待所有上传和处理完成
  await Promise.all(uploadPromises);
  const formData = new FormData();
  if (files.length === 1) {
    // 上传单个文件
    formData.append('image', files[0].file);
    formData.append('sort', JSON.stringify(files[0].file.sort)); // 添加 sort 信息
    formData.append('uploadTime', JSON.stringify(files[0].uploadTime)); // 添加 uploadTime 信息
    axios.post('http://localhost:8080/images/add', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
        .then(response => {
          console.log('文件上传成功', response.data);
          fetchFolderData();
        })
        .catch(error => {
          console.error('文件上传失败', error);
        });
  } else {
    // 上传多个文件
    files.forEach((file) => {
      formData.append('images', file.file);
      formData.append('sort', JSON.stringify(file.file.sort)); // 添加 sort 信息
      formData.append('uploadTime', JSON.stringify(file.uploadTime)); // 添加 uploadTime 信息
      console.log(file.uploadTime);
    });
    axios.post('http://localhost:8080/images/addBatch', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
        .then(response => {
          console.log('文件批量上传成功', response.data);
          fetchFolderData();
        })
        .catch(error => {
          console.error('文件批量上传失败', error);
        });
  }
};

//文件删除操作, 使用上传时候的时间戳
const handleAfterDelete = (file) => {
  console.log(file)
  const index = fileList.value.findIndex(item => item.uploadTime === file.uploadTime);
  if (index !== -1) {
    fileList.value.splice(index, 1);
  }
  const formData = new FormData();
  formData.append('timestamp', file.uploadTime);
  axios.post('http://localhost:8080/images/delete', formData)
      .then(response => {
        console.log('文件删除成功:', response.data);
        fetchFolderData();
      })
      .catch(error => {
        console.error('文件删除失败:', error.response ? error.response.data : error.message);
      });
};


//首页图片内容读取
const readFile = (file) => {
  const index = fileList.value.findIndex((item) => item.objectUrl === file.objectUrl);
  if (index !== -1) {
    const selectedFile = fileList.value[index];
    pictureName.value=selectedFile.file.name?selectedFile.file.name:'未知'
    pictureSize.value=selectedFile.file.size?toSize(selectedFile.file.size):'未知'
    pictureTime.value=selectedFile.file.lastModified?formatTimestamp(selectedFile.file.lastModified):'未知'
    pictureSort.value=selectedFile.file.sort?selectedFile.file.sort:[]
    pictureDevice.value=selectedFile.device?selectedFile.device:''
    picturePlace.value=selectedFile.place?selectedFile.place:''
    picture.value=selectedFile.objectUrl?selectedFile.objectUrl:defaultPic
  }
};

//分类预览界面图片内容读取
const readFile2=(item2)=>{
    pictureName.value=item2.name?item2.name:'未知'
    picture.value=item2.objectUrl?item2.objectUrl:defaultPic
    pictureSize.value=item2.size?toSize(item2.size):'未知'
    pictureTime.value=item2.lastModified?formatTimestamp(item2.lastModified):'未知'
    pictureSort.value=item2.sort?item2.sort:[]
    pictureDevice.value=item2.device?item2.device:''
    picturePlace.value=item2.place?item2.place:''
}

//大小格式转化
const toSize=(size)=>{
  if (size === 0) pictureSize.value='0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(size) / Math.log(k));
  return parseFloat((size / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

//时间格式转化
const formatTimestamp = (timestamp) => {
  // 如果是 yyyy/MM/dd 的字符串则直接返回
  if (typeof timestamp === 'string' && /^\d{4}\/\d{2}\/\d{2}$/.test(timestamp)) {
    return timestamp;
  }
  const date = new Date(timestamp);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0'); // 月份从 0 开始
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}/${month}/${day}`;
};

 // //文件夹
 // const list=ref([
 //    {listName:'20240821',pictureList:[{objectUrl:userPic1,name:'name',size:1000000,lastModified:1635475483259,sort:['八重','狐狸','好看','原神','启动']},{objectUrl:userPic2,name:'name2',size:2000000,lastModified:1709452800000,sort:['胡桃','堂主','可爱','原神','启动']},{objectUrl:userPic3,name:'name3',size:3000000,lastModified:1672527600000,sort:['胡桃','海报','原神','启动']}]},
 //    {listName:'20240822',pictureList:[{objectUrl:userPic1,name:'name',size:1000000,lastModified:1635475483259,sort:['八重','狐狸','好看','原神','启动']},{objectUrl:userPic2,name:'name2',size:2000000,lastModified:1709452800000,sort:['胡桃','堂主','可爱','原神','启动']},{objectUrl:userPic3,name:'name3',size:3000000,lastModified:1672527600000,sort:['胡桃','海报','原神','启动']}]},
 //    {listName:'20240823',pictureList:[{objectUrl:userPic1,name:'name',size:1000000,lastModified:1635475483259,sort:['八重','狐狸','好看','原神','启动']},{objectUrl:userPic2,name:'name2',size:2000000,lastModified:1709452800000,sort:['胡桃','堂主','可爱','原神','启动']},{objectUrl:userPic3,name:'name3',size:3000000,lastModified:1672527600000,sort:['胡桃','海报','原神','启动']}]}
 //  ])

const list = ref([]);
// 获取文件夹数据
const fetchFolderData = () => {
  axios.get('http://localhost:8080/images/folderData')
      .then(response => {
        list.value = response.data.map(folder => {
          return {
            listName: folder.folderName,
            pictureList: folder.files.map(file => ({
              objectUrl: `http://localhost:8080/${folder.folderName}/${file.nameOfUrl}`,
              name: file.name,
              lastModified: file.formattedPhotoTime, // 格式化后的拍摄时间, 格式为 yyyy/MM/dd
              size: parseInt(file.size, 10), // 将 file.size 从字符串转换为整数
              place: file.place, // 拍摄地点 String
              device: file.device, // 拍摄设备 String
              formatType: file.formatType, // 文件格式 String
              sort: JSON.parse(file.category), // 图片分类 String, 也就是sort
            }))
          };
        });
      })
      .catch(error => {
        console.error('获取文件夹数据失败:', error);
      });
};

const scrollContainers = ref([]);
const isAtStart = ref([0]);
const isAtEnd = ref([]);

const scrollLeft=(index)=>{
  const container = scrollContainers.value[index];
  if (container) container.scrollLeft -= 900; // 控制左移的距离
}
const scrollRight=(index)=>{
  const container = scrollContainers.value[index];
  if (container) container.scrollLeft += 900; // 控制右移的距离
}
const  handleScroll=(index)=>{
  const container = scrollContainers.value[index];
  if (container) {
    isAtStart.value[index] = container.scrollLeft !== 0;
    isAtEnd.value[index] = container.scrollWidth - container.scrollLeft <= container.clientWidth+1;
  }
}

// 添加滚动事件监听
onMounted(() => {
  scrollContainers.value.forEach((container, index) => {
    if (container) {
      container.addEventListener('scroll', () => handleScroll(index));
      // 初始化状态
      handleScroll(index);
    }
  });
});

// 移除滚动事件监听
onBeforeUnmount(() => {
  scrollContainers.value.forEach((container, index) => {
    if (container) {
      container.removeEventListener('scroll', () => handleScroll(index));
    }
  });
});

//查看更多
const more=async(item)=>{
  moreList.value.push({
    listName:item.listName,
    fileList:item.pictureList.map(picture => ({
      objectUrl: picture.objectUrl,
      device:picture.device,
      place:picture.place,
      file: {
        name: picture.name,
        size: picture.size,
        lastModified: picture.lastModified,
        type: picture.type?picture.type:'image/jpeg',
        sort: picture.sort
      }
    }))
  })
  await nextTick()
  active.value=moreList.value.length+1
}

//删除页签
const deleteTabs = (index) => {
  if (index+2 === active.value) {
    // 如果删除的是当前选项卡，调整 active 的值
    active.value = index > 0 ? index + 1 : 1;
  }

  moreList.value.splice(index, 1);

  // 确保 active 的值在有效范围内
  if (active.value >= moreList.value.length+1) {
    active.value = moreList.value.length + 1;
  }
};

//新增页签图片内容读取
const readFile3 = (file) => {
    try {
      pictureName.value = file.file?.name || '未知';
      pictureSize.value = file.file?.size ? toSize(file.file.size) : '未知';
      pictureTime.value = file.file?.lastModified ? formatTimestamp(file.file.lastModified) : '未知';
      pictureSort.value = file.file?.sort || [];
      pictureDevice.value=file.device?file.device:''
      picturePlace.value=file.place?file.place:''
      picture.value = file.objectUrl || defaultPic
    } catch (error) {
      console.error('Error handling file click:', error);
    }
};

//图片详情
const picture=ref(userPic1)
const pictureName=ref('图片名')
const pictureSize=ref('200MB')
const pictureTime=ref('2024/08/24')
const pictureDevice=ref('红米')
const picturePlace=ref('北京')
const pictureSort=ref(['八重','狐狸','好看','原神','启动'])
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
          <van-search v-model="value2" show-action placeholder="请输入搜索关键词" background="rgba(0,0,0,0)"  class="search" :clearable="false" >
            <template #action>
              <van-icon v-if="value4" name="arrow-down" @click="()=>{value4=false}"/>
              <van-icon v-if="!value4" name="arrow-up" @click="()=>{value4=true}"/>
            </template>
          </van-search>
          <el-dropdown placement="bottom" class="bottom-start1" size="mini" v-model="value3">
            <el-button  class="listButton1">{{value3}}</el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item class="listButton" @click="sortList2('文件名')">文件名</el-dropdown-item>
                <el-dropdown-item class="listButton" @click="sortList2('地点')">地点</el-dropdown-item>
                <el-dropdown-item class="listButton" @click="sortList2('设备')">设备</el-dropdown-item>
                <el-dropdown-item class="listButton" @click="sortList2('标签')">标签</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-dropdown placement="bottom" class="bottom-start" size="large" v-model="value1">
            <el-button  class="listButton">{{value1}}</el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item class="listButton" @click="sortList('导入顺序')">导入顺序</el-dropdown-item>
                <el-dropdown-item class="listButton" @click="sortList('时间顺序')">时间顺序</el-dropdown-item>
                <el-dropdown-item class="listButton" @click="sortList('大小顺序')">大小顺序</el-dropdown-item>
                <el-dropdown-item class="listButton" @click="sortList('文件名顺序')">文件名顺序</el-dropdown-item>
                <el-dropdown-item class="listButton" @click="sortList('其他顺序')" disabled>其他顺序</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <div class="box"></div>
          <van-uploader v-model="filteredFileList" preview-size="10.5vw" :preview-full-image="false" class="up" @click-preview="readFile" @delete="handleAfterDelete">
            <template #preview-cover="{ file }">
              <div class="preview-cover van-ellipsis" >{{ file.name||'未命名图片' }}</div>
            </template>
            <div></div>
          </van-uploader>
          <van-uploader v-model="fileList" :after-read="handleAfterRead" preview-size="10.5vw" :preview-full-image="false" multiple @click-preview="readFile" accept="image/*,.cr2,.cr3,.nef,.nrw,.arw,.srf,.sr2,.raf,.orf,.rw2,.pef,.dng,.raw,.srw,.kdc,.erf,.rwl,.mef,.tif,.tiff" :preview-image="false">
            <template #preview-cover="{ file }">
              <div class="preview-cover van-ellipsis" >{{ file.name||'未命名图片' }}</div>
            </template>
          </van-uploader>
        </van-tab>
        <van-tab title="分类预览" class="preview">
          <div class="cell" v-for="(item,index) in list" :key="item.listName" :style="{userSelect:'none'}">
            <div class="box2">
              <div class="className">{{item.listName}}</div>
              <button class="more" @click="more(item)">查看更多</button>
            </div>
            <div calss="box3"></div>
            <button class="arrowButton" v-show="isAtStart[index]" @click="()=>scrollLeft(index)"><van-icon name="arrow-left" size="30px"/></button>
            <div class="arrowButton2" v-show="!isAtStart[index]"></div>
            <div class="pictureList" :ref="el => scrollContainers[index] = el" @scroll="() => handleScroll(index)">
              <van-image v-for="(item2) in item.pictureList" :key="item.listName" class="picture" width="150px" :src=item2.objectUrl @dragstart="($event)=>{$event.preventDefault();}" @click="readFile2(item2)"/>
            </div>
            <button class="arrowButton" v-show="!isAtEnd[index]"  @click="()=>scrollRight(index)"><van-icon name="arrow" size="30px"/></button>
            <div class="arrowButton2" v-show="isAtEnd[index]"></div>
          </div>
        </van-tab>
        <van-tab v-for="(item,index) in moreList" class="input">
          <template #title>{{ item.listName }}<button class="closeButton" @click="deleteTabs(index)"><van-icon name="close" /></button></template>
          <van-search v-model="value2" placeholder="请输入搜索关键词" background="rgba(0,0,0,0)"  class="search"/>
          <el-dropdown placement="bottom" class="bottom-start" size="large" v-model="value1">
            <el-button  class="listButton">{{value1}}</el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item class="listButton" @click="sortList('导入顺序')">导入顺序</el-dropdown-item>
                <el-dropdown-item class="listButton" @click="sortList('时间顺序')">时间顺序</el-dropdown-item>
                <el-dropdown-item class="listButton" @click="sortList('大小顺序')">大小顺序</el-dropdown-item>
                <el-dropdown-item class="listButton" @click="sortList('文件名顺序')">文件名顺序</el-dropdown-item>
                <el-dropdown-item class="listButton" @click="sortList('其他顺序')" disabled>其他顺序</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <div class="box"></div>
          <van-uploader v-model="item.fileList" preview-size="10.5vw" :preview-full-image="false" @click-preview="readFile3">
            <template #preview-cover="{ file }">
              <div class="preview-cover2 van-ellipsis" >{{ file.name||'未命名图片' }}</div>
            </template>
            <div></div>
          </van-uploader>
        </van-tab>
      </van-tabs>
    </div>
  </div>
  <div class="nav">
    <van-image
      @dragstart="($event)=>{$event.preventDefault();}"
      style="min-width: 138px; max-height: 50vh;width: 17.1vw;object-fit: contain;overflow: hidden;"
      :src=picture
      />
    <div class="littleTitle">{{pictureName}}</div>
    <div class="littleSize">大小：{{pictureSize}}</div>
    <div class="littleSize" v-show="picturePlace">拍摄地点：{{picturePlace}}</div>
    <div class="littleSize" v-show="pictureDevice">拍摄设备：{{pictureDevice}}</div>
    <div class="littleSize">创建时间：{{pictureTime}}</div>
    <div class="classficiation">
      <div class="tag" v-for="(item,index) in pictureSort" :key="index"><van-tag  round size="large" color="rgb(204, 204, 204)" text-color="black" type="primary">{{ item }}</van-tag></div>
    </div>
  </div>
</template>

<style lang="less" scoped>
.main{
  // background-color: #301515;
  float: left;
  width: 78vw;
  // min-width: 839px;
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
        margin-left: -12px;
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
      .up{
        float: left;
      }
      .bottom-start1{
        width: 70px;
        top:5px;
        float: left;
        .listButton1{
          background-color: rgba(0,0,0,0.3);
          font-size: 0.9em;
          font-weight: 500;
          width: 70px;
        }
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
      padding-left: 1vw;
    }
  }
}
.cell{
  height: 180px;
  width: 78vw;
  margin-top: 25px;
  margin-bottom: 25px;
  display:inline-block;
  .box2{
    width: 77vw;
    height: 35px;
    // background-color: #797979;
    .className{
      // background-color: #797979;
      font-size: 1.6em;
      margin-bottom: 7px;
      font-weight: 100;
      padding-left: 3vw;
      height: 28px;
      float: left;
    }
    .more{
      border: none;
      background-color: rgb(204, 204, 204);
      align-items: center;
      font-size: 1.2em;
      // margin-right: 1vw;
      height: 28px;
      width: 90px;
      font-weight: 400;
      float: right;
      cursor: pointer;
      // display: inline-block;

      &:hover{
        background-color: #b3b2b2;
      }
    }
  }
}
.pictureList{
  width: 71vw;
  display:flex;
  justify-content:left;
  /* 设置超出滚动 */
  overflow-x:auto;
  scroll-behavior: smooth;
  float: left;
}
.picture{
  cursor: pointer;
  // width:7.5vw;
  height: 150px;
  display:inline-block;
  margin-right:0.3vw;
  /* 超出滚动的关键，没有它元素会自动缩小，不会滚动 */
  flex-shrink: 0;
}
::-webkit-scrollbar {
  /* 隐藏滚动条 */
  display: none;
}
.arrowButton{
  color: rgb(147, 147, 147);
  border: none;
  float: left ;
  height: 160px;
  margin-top: -5px;
  width: 3vw;
  display: flex;
  justify-content: center;
  align-items: center;
  &:hover{
    background-color: #797979;
    color: #eeeeee;
  }
}
.arrowButton2{
  border: none;
  float: left ;
  height: 160px;
  margin-top: -5px;
  width: 3vw;
}
.preview-cover2 {
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
.closeButton{
  border: none;
  background-color: rgba(0,0,0,0);
  cursor: pointer;
  &:hover{
    color: rgba(0, 0, 0, 0.8);
  }
}
.nav{
  // align-items: center; /* 水平居中对齐 */
  border-left: 4px solid rgb(41, 115, 243);
  padding-top: 54px ;
  padding-left:1.5vw;
  width: 18.6vw;
  min-width: 150px;
  height: 100vh;
  position: fixed;
  right: 0;
  user-select: none;
  .littleTitle{
    margin-left: -0.7vw;
    font-size: 1.1em;
    align-self:center;
    text-align: center;
    margin-bottom: 20px;
  }
  .littleSize{
    margin-left: -0.7vw;
    padding-bottom: 10px;
    font-size: 1em;
    align-self:center;
    text-align: center;
  }
  .classficiation{
    margin: 2vh 2vw;
    padding-right: 0.7vw;
    text-align: center;
    // background-color: aqua;
    .tag{
      display: inline-block;
      margin: 4px 0.5vw;
    }
  }
}
</style>