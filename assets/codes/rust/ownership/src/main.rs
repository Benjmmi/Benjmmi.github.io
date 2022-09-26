fn main() {
    println!("Hello world!");
    // 变量与数据交互的方式（一）：移动
    {
        // 将 5 绑定到 x；接着生成一个值 x 的拷贝并绑定到 y
        let x = 5;
        let y = x;
        // 因为整数是有已知固定大小的简单值，所以这两个 5 被放入了栈中
        // 像整型这样的在编译时已知大小的类型被整个存储在栈上，所以拷贝其实际的值是快速的。
        // Rust 有一个叫做 Copy trait 的特殊注解，可以用在类似整型这样的存储在栈上的类型上
        println!("stack {},{}",x,y)
    }
    {
        // 这里不是浅拷贝
        // 因为 Rust 同时使第一个变量无效了，这个操作被称为 移动（move），
        // 而不是浅拷贝。
        // 这里的例子可以解读为 s1 被 移动 到了 s2 中。
        let s1 = String::from("hello world");
        let s2 = s1;
        println!("move {}",s2);
    }
    // 变量与数据交互的方式（二）：克隆
    {
        // 我们 确实 需要深度复制 String 中堆上的数据，而不仅仅是栈上的数据，
        // 可以使用一个叫做 clone 的通用函数。
        let s1 = String::from("hello world");
        let s2 = s1.clone();
        println!("clone {},{}",s1,s2);  
    }
    // 所有权与函数
    // 所以调用一次函数如果没有发生 copy 那么就相当于转移了所有权？
    {
        let s1 = String::from("hello world");
        // let s2 = s1.clone();
        takes_ownership(s1);// 这里也是移动，所以在下面的函数作用域中被释放
        // println!("{}",s1); // 应为被下面函数释放了，所以这里的 s1 编译不会通过
    }
    {
        let x = 5;   // 这里是栈 copy 所以下面可以继续使用
        makes_copy(x);
        println!("{x}");
    }
    {
        let s1 = give_ownership();
        println!("{}",s1);
        let s2 = String::from("hello");
        let s2 = takes_and_gives_back(s2);
    }
}

fn takes_ownership(some_string: String) {
    println!("takes_ownership {}",some_string);
}

fn makes_copy(x: i32) {
    println!("makes_copy {}",x);    
}
// 将所有权移动到调用方
fn give_ownership() -> String{  // give_ownership 的作用域
    let some_string = String::from("yours"); // some_string 进入作用域
    some_string // 返回 some_string 并移出所有权给调用方
}

// takes_and_gives_back 将传入字符串并返回该值
fn takes_and_gives_back(some_string: String) -> String { // 进入 give_ownership 作用域，并持有所有权
    some_string // 返回 some_string 并移出所有权给调用方
}