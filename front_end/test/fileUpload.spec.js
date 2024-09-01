import { mount } from '@vue/test-utils';
import Home from '../src/views/home.vue';
import axios from 'axios';

jest.mock('axios');

describe('File Upload', () => {
  it('should upload a file and update the fileList', async () => {
    const wrapper = mount(Home);

    // 模拟文件对象
    const file = new File(['file contents'], 'test.jpg', { type: 'image/jpeg' });

    // 模拟上传后的响应数据
    const responseData = [{ label: 'testLabel' }];
    axios.post.mockResolvedValue({ data: responseData });

    await wrapper.vm.handleAfterRead(file);

    // 检查文件状态是否更新为 'done'
    expect(wrapper.vm.fileList[0].status).toBe('done');
    // 检查是否正确添加了分类标签
    expect(wrapper.vm.fileList[0].file.sort).toContain('testLabel');
  });

  it('should handle upload failure', async () => {
    const wrapper = mount(Home);
    const file = new File(['file contents'], 'test.jpg', { type: 'image/jpeg' });

    // 模拟上传失败的响应
    axios.post.mockRejectedValue(new Error('Upload failed'));

    await wrapper.vm.handleAfterRead(file);

    // 检查文件状态是否更新为 'failed'
    expect(wrapper.vm.fileList[0].status).toBe('failed');
  });
});

