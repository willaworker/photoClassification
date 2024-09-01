import { mount } from '@vue/test-utils';
import Home from '../src/views/home.vue';
import axios from 'axios';

jest.mock('axios');

describe('File Deletion', () => {
  it('should delete a file and update the fileList', async () => {
    const wrapper = mount(Home);

    // 模拟文件对象并触发删除操作
    const file = wrapper.vm.fileList[0];
    await wrapper.vm.handleAfterDelete(file);

    // 检查文件是否已从列表中删除
    expect(wrapper.vm.fileList).not.toContain(file);
  });
});
