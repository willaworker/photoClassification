import { mount } from '@vue/test-utils';
import Home from '../src/views/home.vue';

describe('File Sorting and Searching', () => {
  it('should sort files by name', () => {
    const wrapper = mount(Home);

    wrapper.vm.value1 = '文件名顺序';
    const sortedList = wrapper.vm.filteredFileList;

    expect(sortedList[0].file.name).toBe('77.jpg');
    expect(sortedList[1].file.name).toBe('八重.jpg');
  });

  it('should filter files by search keyword', () => {
    const wrapper = mount(Home);

    wrapper.vm.value2 = '胡桃';
    const filteredList = wrapper.vm.filteredFileList;

    expect(filteredList.length).toBe(1);
    expect(filteredList[0].file.name).toBe('胡桃.jpeg');
  });
});
