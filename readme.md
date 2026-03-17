## 笔记

### 什么是异步操作？
异步操作（Asynchronous Operation）是一种**非阻塞**的执行模式，核心特点是：**发起操作后，无需等待操作完成，就能继续执行后续代码**；当操作最终完成（成功/失败）时，会通过回调、通知等方式告知调用方结果。

为了让你快速理解，先通过**生活场景对比**和**代码示例**讲清楚核心逻辑：

#### 一、先看对比：同步 vs 异步
| 模式       | 核心逻辑                                  | 生活例子                                  | 编程例子                                  |
|------------|-------------------------------------------|-------------------------------------------|-------------------------------------------|
| **同步**   | 发起操作 → 阻塞等待完成 → 再执行后续代码  | 点外卖后，一直盯着手机等，啥也不干        | 调用 `Thread.sleep(3000)` 后，才执行下一行 |
| **异步**   | 发起操作 → 立即执行后续代码 → 操作完成后回调 | 点外卖后，继续看书/工作，外卖到了会收到电话 | 调用 `future.addListener()` 后，主线程继续执行 |

#### 二、异步操作的核心特征
1. **非阻塞**：发起操作的线程不会被卡住，能立刻去做其他事；
2. **结果延后处理**：操作的结果不会立即返回，而是在操作完成后通过「回调函数」「Future/Promise」等方式通知；
3. **并发效率高**：避免了线程因等待（如IO、网络请求）而闲置，提升系统吞吐量（这也是Netty、Node.js等框架高性能的核心原因）。

#### 三、代码示例：同步 vs 异步（Java 版）
##### 1. 同步操作（阻塞）
```java
public class SyncDemo {
    public static void main(String[] args) {
        System.out.println("开始执行主线程");
        
        // 同步执行耗时操作（比如网络请求、文件读写）
        String result = doSyncTask(); // 这里会阻塞，直到doSyncTask执行完
        
        System.out.println("耗时操作结果：" + result);
        System.out.println("主线程继续执行其他逻辑"); // 只有上面执行完，才会到这一步
    }

    // 同步耗时任务（比如模拟网络请求，耗时2秒）
    private static String doSyncTask() {
        try {
            Thread.sleep(2000); // 阻塞2秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "同步任务完成";
    }
}
```
**执行结果顺序**：
```
开始执行主线程
（阻塞2秒）
耗时操作结果：同步任务完成
主线程继续执行其他逻辑
```

##### 2. 异步操作（非阻塞）
用Netty的Future/Promise实现（贴近你之前问的Netty场景）：
```java
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Promise;

public class AsyncDemo {
    public static void main(String[] args) {
        System.out.println("开始执行主线程");
        
        // 创建Netty的线程池（用于执行异步任务）
        EventExecutorGroup executor = new DefaultEventExecutorGroup(1);
        Promise<String> promise = executor.newPromise();

        // 异步执行耗时任务（主线程不阻塞）
        executor.submit(() -> {
            try {
                Thread.sleep(2000); // 耗时操作，在子线程执行
                promise.setSuccess("异步任务完成"); // 任务完成，设置结果
            } catch (InterruptedException e) {
                promise.setFailure(e); // 任务失败，设置异常
            }
        });

        // 给异步任务添加回调（任务完成后才执行）
        promise.addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("耗时操作结果：" + future.getNow());
            } else {
                System.out.println("任务失败：" + future.cause().getMessage());
            }
            executor.shutdownGracefully();
        });

        // 主线程不阻塞，立刻执行这行
        System.out.println("主线程继续执行其他逻辑");
        
        // 防止主线程提前退出（仅演示用）
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```
**执行结果顺序**：
```
开始执行主线程
主线程继续执行其他逻辑
（2秒后）
耗时操作结果：异步任务完成
```

#### 四、异步操作在Netty中的核心价值
Netty是高性能网络框架，所有IO操作（连接、读写、关闭）都是异步的：
- 比如调用 `channel.connect()` 不会阻塞当前线程，而是立刻返回 `ChannelFuture`；
- 线程不用等待连接建立完成，可继续处理其他请求；
- 当连接成功/失败后，Netty会触发 `ChannelFuture` 的回调函数，处理结果。

这种模式让少量线程就能处理大量并发连接（比如几万、几十万），避免了同步模式下「一个连接占用一个线程」的资源浪费。

### 总结
1. **异步操作核心**：发起操作后不阻塞，后续代码立刻执行，操作结果通过回调/通知处理；
2. **对比同步**：同步是「排队等」，异步是「先干别的，等通知」；
3. **Netty中的异步**：所有IO操作都是异步的，通过 `Future/Promise` 处理结果，是Netty高性能的关键。


## 简单介绍一下  netty中的Future和Promise

### Netty 中的 Future 和 Promise 极简解析
Netty 对 JDK 原生 `Future` 做了增强，并扩展出 `Promise`，二者是 Netty 异步编程的核心，核心分工：
- **Future**：「只读」视图，用于**监听/获取**异步操作结果；
- **Promise**：「可写」的 Future，用于**主动设置**异步操作的结果（成功/失败）。

#### 一、Netty Future（io.netty.util.concurrent.Future）
1. **核心定位**：继承 JDK `Future`，解决了原生 Future 只能阻塞获取结果的痛点，新增**非阻塞回调**能力，是「结果消费者」使用的对象。
2. **核心增强（对比 JDK Future）**：
    - 支持添加回调（`addListener()`），操作完成时自动触发，无需阻塞；
    - 提供 `isSuccess()`/`isFailed()` 快速判断结果状态；
    - 非阻塞获取结果（`getNow()`），未完成时返回 `null`。
3. **典型场景**：调用 `channel.connect()`/`writeAndFlush()` 会返回 `ChannelFuture`（Netty Future 的子类），你通过它添加回调处理结果。

#### 二、Netty Promise（io.netty.util.concurrent.Promise）
1. **核心定位**：继承 Netty `Future`，是「结果生产者」使用的对象——异步操作的执行方通过它主动设置结果（成功/失败）。
2. **核心能力**：
    - `setSuccess(V result)`：设置操作成功的结果；
    - `setFailure(Throwable cause)`：设置操作失败的异常；
    - 完全兼容 Future 的所有回调、状态判断方法。

#### 三、极简示例（核心逻辑）
```java
// 1. 创建线程池（Netty 事件执行器）
EventExecutorGroup executor = new DefaultEventExecutorGroup(1);

// 2. 创建 Promise（可写的 Future）
Promise<String> promise = executor.newPromise();

// 3. 生产者：异步执行任务，主动设置结果
executor.submit(() -> {
    try {
        Thread.sleep(1000); // 模拟耗时操作
        promise.setSuccess("任务完成"); // 成功：设置结果
    } catch (Exception e) {
        promise.setFailure(e); // 失败：设置异常
    }
});

// 4. 消费者：通过 Future（Promise）监听结果（非阻塞）
promise.addListener(future -> {
    if (future.isSuccess()) {
        System.out.println("结果：" + future.getNow());
    } else {
        System.out.println("失败：" + future.cause());
    }
});
```

### 总结
1. **Netty Future**：增强版 JDK Future，核心是「非阻塞回调」，只能读结果；
2. **Netty Promise**：继承 Future，新增「写结果」能力，是生产/消费异步结果的完整模型；
3. 核心关系：Promise 是可写的 Future，生产者用 Promise 设结果，消费者用 Future 拿结果。
