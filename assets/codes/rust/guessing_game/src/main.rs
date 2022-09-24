use rand::Rng;
use std::io; // 引入标准输入输出库到当前作用域。io 库来自标准库，标准库被称为 std // 引入第三方库 rand . Rng 是一个 trait
                                                                                  // 定了随机数生成器应实现的方法，想使用这个方法，trait 必须在作用域中。
use std::cmp::Ordering; // 从标准库引入 std::cmp::Ordering 的类型到作用域中。
                        // Ordering 也是一个枚举，不过他的成员是 Less、Greater 和 Equal

fn main() {
    println!("Guess the number!");

    // rand::thread_rng 函数提供实际使用的随机数生成器
    // 位于当前执行线程的本地环境中，并从操作系统获取 seed。接着调用随机数生成器的 gen_range 方法
    // gen_range 方法获取一个范围表达式 start..=end
    //
    let secret_number = rand::thread_rng().gen_range(1..=10);

    // println!("The secret number is: {secret_number}");

    // 创建 apples 的变量并绑定值 5.
    // rust 默认变量是不可变得，也就是说变量赋值后不可以再修改了
    let apples = 5;
    println!("apples :{apples}");

    loop {
        println!("Please input you guess.");

        // 创建一个变量用于存储用户输入
        // let 语句用来存储变量
        // mut 是一个变量可变
        // String::new 是一个函数，会返回一个新的 String 的实例
        // ::new 代表了关联函数，也就是 String 的 关联函数 new()。可以理解为静态方法
        let mut guess = String::new();

        // reas_line 方法从标准输入句柄获取用户输入。
        // &mut guess 作为参数传递个 read_line() 函数。让其用户输入存储到这个字符串中
        // read_line 是一个追加的过程，不会覆盖
        // & 表示是这个参数的引用，这里有坑的感觉！！！
        //
        // read_line 是一个函数，会返回一个 Result 状态类型枚举值。同时 Result 也有
        // 自己的关联函数expect()。如果 Result 实例的值是 Ok，expect 会获取 Ok 中的值
        // 并原样返回，否则就处理戳去
        io::stdin()
            .read_line(&mut guess)
            .expect("Failed to read line");

        // 字符串的 parse 方法 将字符串转换成其他类型
        // let guess: u32 指定。guess 后面的冒号（:）告诉 Rust 我们指定了变量的类型
        let guess: u32 = match guess.trim().parse() {
            Ok(num) => num,
            Err(_) => continue,
        };

        println!("You guessed: {guess}");

        // loop 代表当前是循环
        // match 表达式，根据 cmp 返回的 Ordering 成员决定接下来做什么
        // match 结构和模式是 Rust 中强大的功能，它体现了代码可能遇到的多种情形，
        // 并帮助你确保没有遗漏处理。
        match guess.cmp(&secret_number) {
            Ordering::Less => println!("Too small!"),
            Ordering::Greater => println!("Too big!"),
            Ordering::Equal => {
                println!("You win");
                break;
            }
        }
    }
}
