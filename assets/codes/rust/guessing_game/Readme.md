
Rust 有一个静态强类型系统，同时也有类型推断

# Cargo.lock 文件确保构建是可重现的

Cargo 有一个机制来确保任何人在任何时候重新构建代码，都会产生相同的结果：Cargo 只会使用你指定的依赖版本，
除非你又手动指定了别的。例如，如果下周 `rand crate` 的 `0.8.4` 版本出来了，它修复了一个重要的 `bug`，同
时也含有一个会破坏代码运行的缺陷。为了处理这个问题，`Rust`在你第一次运行 `cargo build` 时建立了 
`Cargo.lock` 文件，我们现在可以在 `guessing_game` 目录找到它。

当你 确实 需要升级 `crate` 时，`Cargo` 提供了这样一个命令，`update`，它会忽略 `Cargo.lock` 文件，并计算
出所有符合 `Cargo.toml` 声明的最新版本。`Cargo` 接下来会把这些版本写入 `Cargo.lock` 文件。不过，`Cargo` 
默认只会寻找大于 `0.8.3` 而小于 `0.9.0` 的版本。如果 `rand crate` 发布了两个新版本，`0.8.4` 和 `0.9.0`，
在运行 `cargo update` 时会出现如下内容：

```bash
cargo update
    Updating crates.io index
    Updating rand v0.8.3 -> v0.8.4
```



