use std::io;
// const 设置常量
const THREE_HOURS_IN_SECONDS: u32 = 60*60*3;

fn main() {
    // x 绑定到值 5
    let x = 5;
    println!("The value of x is {x}");
    // let x = 创建了一个新变量 x，获取初始值并加 1
    let x = x + 6;
    println!("The value of x is {x}");
    println!("const THREE_HOURS_IN_SECONDS : {THREE_HOURS_IN_SECONDS}");
    {
        // 内部作用域
        //  let 语句也隐藏了 x 并创建了一个新的变量，将之前的值乘以 2
        let x = x * 2;
        println!("The value of x in the inner scope is:{x}");
        // 作用域结束
    }
    // x 又返回到 5+6
    println!("The value of x is {x}");

    // f64
    let x = 2.0; 
    let y : f32 = 3.0;
    println!("float64:{x}, float32:{y}");
    // 整数除法会向下舍入到最接近的整数。
    // 加法
    let sum = 5 + 10;
    println!("加法 sum:{sum}");
    // 减法
    let difference = 95.5 - 4.3;
    println!("减法 difference:{difference}");
    // 乘法
    let product = 4 * 30;
    println!("乘法 product:{product}");
    // 除法
    let quotient = 56.7 / 32.2;
    println!("除法 quotient:{quotient}");
    let floored = 2/3;
    println!("除法 floored:{floored}");
    // 取余
    let remainder = 43 % 5;
    println!("取余 remainder:{remainder}");
    // 布尔值
    let t = true;
    let f: bool = false;
    println!("布尔值 t:{t},f:{f}");
    // 字符型
    let c = 'z';
    let z : char = 'ℤ';
    let heart_eyed_cat = '😻';
    println!("字符型 c:{c},z:{z},heart_eyed_cat:{heart_eyed_cat}");
    // 复合型
    // 元祖类型：元组是一个将多个其他类型的值组合进一个
    // 复合类型的主要方式。元组长度固定：一旦声明，其长度不会增大或缩小。
    let tup: (i32, f64, u8) = (500, 6.4, 1);
    // 错误输出方式：println!("复合类型=》元祖类型 tup{tup}");
    // tup 变量绑定到整个元组上，因为元组是一个单独的复合元素。
    // 为了从元组中获取单个值，可以使用模式匹配来解构元组值
    // 解构
    let (a,b,c) = tup;
    println!("The value of y is:{b}");
    // 也可以使用点号（.）后跟值的索引来直接访问它们
    let a = tup.0;
    println!("The value of x is:{a}");
    // 不带任何值的元组有个特殊的名称，叫做 单元（unit） 元组。
    // 这种值以及对应的类型都写作 ()，表示空值或空的返回类型。
    
    // 数组类型
    // 在栈上分配空间，固定数量的元素
    let a = [1, 2, 3, 4, 5];
    let first = a[0];
    println!("数组类型 a[0]:{first}");
    // 也可以像这样编写数组
    // 方括号中包含每个元素的类型，后跟分号，再后跟数组元素的数量
    let a: [i32; 5] = [1,2,3,4,5];
    // 创建一个每个元素都为相同值的数组
    let a = [3; 5]; // -> let a = [3, 3, 3, 3, 3];
    
    let mut index = String::new();

    io::stdin()
        .read_line(&mut index)
        .expect("Failed to read line");
    let index: usize = index
        .trim()
        .parse()
        .expect("Index entered was not a number");
    let element = a[index];
    println!("The value of the element at index {index} is: {element}");
    another_function(1333,'a');
    let b = five();
    println!("The return value of x is:{x}");

    // if 是一个表达式，我们可以在 let 语句的右侧使用它
    let condition = true;
    let number = if condition { 5 } else { 6 };
    println!("The value of number is:{number}");

    // loop 关键字告诉 Rust 一遍又一遍地执行一段代码直到你明确要求停止。
    // loop {
    //     println!("again")
    // }
    // 从 loop 中获取返回值
    let mut counter = 0;
    let result = loop {
        counter += 1;
        if counter > 10 {
            break counter*3;
        }
    };
    println!("The value of counter is:{result}");


    // 嵌套循环，break 和 continue 应用于此时最内层的循环
    // 循环上指定一个 循环标签
    // 然后将标签与 break 或 continue 一起使用
    let mut count = 0;
    'counting_up: loop {
        println!("count = {count}");
        let mut remaining  = 10;
        loop {
            println!("remaining = {remaining}");
            if remaining == 9 {
                break;
            }
            if count == 2 {
                break 'counting_up;
            }
            remaining -= 1;
        }
        count+=1;
    }
    println!("End count = {}", count);

    // while 条件循环
    let mut number = 3;
    while number != 0 {
        println!("number = {number}");
        number-=1;
    }
    println!("LIFTOFF!!!");
    let a = [10,20,30,40,50];
    let mut index = 0;
    // while 每次都进行条件检查，所以执行起来更慢
    while index < 5 {
        println!("while the value is:{}",a[index]);
        index += 1;
    }
    for ele in a {
        println!("for item The value is: {ele}");
    }
    // for 实现倒计时,rev 实现反转
    for ele in (1..10).rev() {
        println!("for item The value rev:{ele}");
    }
    println!("LIFTOFF!!!");
}

// fn 后面跟着函数名和一对圆括号来定义函数
// 大括号告诉编译器哪里是函数体的开始和结尾。
// 函数参数必须申明类型，多个参数用逗号隔开
fn another_function(x: i32, unit_label: char) {
    println!("Another function. The value of x is:{x},unit_label:{unit_label}");
}

// 具有返回值的函数
fn five() -> i32{
    5
}
