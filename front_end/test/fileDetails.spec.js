import { mount } from '@vue/test-utils';
import Home from '../src/views/home.vue';

describe('File Details', () => {
  it('should read file details correctly', () => {
    const wrapper = mount(Home);

    // 模拟点击文件以查看详细信息
    const file = wrapper.vm.fileList[0];
    wrapper.vm.readFile(file);

    expect(wrapper.vm.pictureName).toBe(file.file.name);
    expect(wrapper.vm.pictureSize).toBe('976.56 KB'); // 假设已经格式化
    expect(wrapper.vm.pictureTime).toBe('2021/10/29'); // 假设文件日期
  });
});
