// 第一种方式
/** @type {import('jest').Config} */
const config = {
    verbose: true,
    transform: {
      "^.+\\.jsx?$": "babel-jest",
      '^.+\\.vue$' : '@vue/vue3-jest' , 
    },
    testEnvironment: 'jsdom',
    testEnvironmentOptions: {
        customExportConditions: ["node", "node-addons"],
     },
     moduleNameMapper: {
      "\\.(jpg|jpeg|png|gif|eot|otf|webp|svg|ttf|woff|woff2|mp4|webm|wav|mp3|m4a|aac|oga)$": "<rootDir>/__mocks__/fileMock.cjs",
    },
  };
  
  module.exports = config;