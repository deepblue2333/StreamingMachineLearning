# Streaming Machine Learning

本项目将实现一个支持常见的在线机器学习算法的流计算引擎。

并希望实现以下特性：
1. 支持流式训练
2. 一套容易开发定制的机器学习策略的API
3. 支持窗口计算，watermark机制，并基于此实现双流join
4. 分布式可扩展的

### 目前已实现的在线学习算法
[OnlineKMeans](./Doc/OnlineKMeans.md)

### API手册


### 需求记录

- 空值策略配置化：通过Builder模式允许定义字段是否可为null。 
```java
tableRowEvent.addField("score", DataType.INT)
.nullable(true)
.setValue(null);
```
- 默认值支持：在字段定义时指定默认值。
```java
tableRowEvent.addField("status", DataType.STRING)
.defaultValue("pending");
```


### 开发日志
20250315 开始记录


### 参考资料
本项目计算引擎设计部分参考了 《Grokking Streaming Systems》 中的设计。