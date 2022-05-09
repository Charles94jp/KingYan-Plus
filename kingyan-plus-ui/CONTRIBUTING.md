## Git提交消息格式

Git的提交信息有明确的格式要求，统一、规范的提交会让提交历史更易于阅读 。

每一个提交包含`header`,`body`,`footer`三部分，`header`是一定要填写的，`body`和`footer`是可选的。

```
<header>
<BLANK LINE>
<body>
<BLANK LINE>
<footer>
```

其中每一行信息都不超超过100个字符。

### Header

```
<type>: <short summary>
  │        │
  │        └─⫸ 提交内容的概要描述，末尾不需要标点符号。
  │
  └─⫸ 提交类型: build|chore|ci|docs|feat|fix|perf|refactor|style|test|style|revert
```

#### Type

可选项:

* **chore**: 除了src或test以外的改动
* **ci**: CI配置或脚本的改动 (比如: Circle, BrowserStack, SauceLabs)
* **docs**: 文档内容的改动
* **feat**: 新的功能
* **fix**: 问题修复
* **perf**: 提交性能的改动的代码变动
* **refactor**: 除了修复bug和增加功能的代码改动
* **test**: 测试用例的改动
* **style**: 代码格式的改动(空格, 格式, 分号等等)
* **revert**: 还原提交

#### Summary

用一句话简单说明自此改动的内容:

* 使用祈使句，现在时: "change" 而不是 "changed" 或者 "changes"
* 第一个字母不需要大写
* 不需要用句号结尾