
// 定义结构体，需要使用 struct 关键字并为整个结构体提供一个名字。
// 定义每一部分数据的名字和类型，我们称为 字段
struct User {
    active: bool,
    username: String,
    email: String,
    sign_in_count: u64,
}

// 使用没有命名字段的元组结构体来创建不同的类型
// 元组结构体有着结构体名称提供的含义，但没有具体的字段名，只有字段的类型。
// 定义的每一个结构体有其自己的类型，即使结构体中的字段可能有着相同的类型
struct Color(i32,i32,i32);
struct Point(i32,i32,i32);

// 我们必须为结构体显式选择这个功能。为此，在结构体定义之前加上外部属性 #[derive(Debug)]
#[derive(Debug)]
struct Reatangle {
    width: u32,
    height: u32,
}

fn main() {
    let user1 = User{
        email: String::from("someone@example.com"),
        username: String::from("someoneusername123"),
        active: true,
        sign_in_count: 1,
    };
    println!("{}", user1.email);
    build_user(String::from("someone@example.com"), String::from("someoneusername123"));
    let user2 = User{
        active: user1.active,
        username: user1.username,
        email: String::from("someone@example.com"),
        sign_in_count: user1.sign_in_count,
    };
    // 结构更新语法就像带有 = 的赋值，因为它移动了数据
    // println!("{}",user1.username);
    println!("{}",user2.username);

    let black = Color(0,0,0);
    let origin = Point(0,0,0);
    println!("{}",black.0);
    println!("{}",origin.0);

    let width = 30;
    let height = 50;
    println!("The area of the reactangle is {} square pixels",area(width, height));
    let reat = (30, 90);
    println!("The area of the reactangle is {} square pixels",area_tup(reat));
    let rect1 = Reatangle{
        width: 30,
        height: 40,
    };
    // 访问对结构体的引用的字段不会移动字段的所有权，这就是为什么你经常看到对结构体的引用
    println!("The area of the reactangle is {} square pixels",area_struct(rect1));

    let rect2 = Reatangle{
        width: 30,
        height: 40,
    };
    let rect3 = Reatangle{
        width: 30,
        height: 40,
    };
    // 在 {} 中加入 :? 指示符告诉 println! 我们想要使用叫做 Debug 的输出格式。
    println!("{:#?}",rect2);

    // Debug 格式打印数值的方法是使用 dbg! 宏。dbg! 宏接收一个表达式的所有权
    dbg!(&rect2);

    println!("The area of the reactangle is {} square pixels",rect2.area());
    println!("The area of the reactangle is {} square pixels",rect2.width());
    println!("The area of the reactangle is {} square pixels",rect2.can_hold(&rect3));
    let sq = Reatangle::square(200);
    println!("The area of the reactangle is {} square pixels",sq.area());
}

// 通过派生 trait 增加实用功能
// println! 宏能处理很多类型的格式，不过，{} 默认告诉 println! 使用被称为 Display 的格式
// 意在提供给直接终端用户查看的输出。目前为止见过的基本类型都默认实现了 Display，
// 因为它就是向用户展示 1 或其他任何基本类型的唯一方式。


// 定义方法
// 它们使用 fn 关键字和名称声明，可以拥有参数和返回值，同时包含在某处调用该方法时会执行的代码。
// 第一个参数总是 self，它代表调用该方法的结构体实例。
// impl 块.impl 是 implementation 的缩写
// 这个 impl 块中的所有内容都将与 Rectangle 类型相关联。
impl Reatangle {
    // 所有在 impl 块中定义的函数被称为 关联函数（associated functions）
    // &self 实际上是 self: &Self 的缩写。
    // 在一个 impl 块中，Self 类型是 impl 块的类型的别名
    fn area(&self) -> u32 {
        self.width * self.height
    }
    fn width(&self) -> bool {
        self.width > 0
    }
    fn can_hold(&self,r: &Reatangle) -> bool {
        r.width <= self.width && r.height <= self.height
    }
}

// 多个 impl 块
// 每个结构体都允许拥有多个 impl 块,但每个方法有其自己的 impl 块。
impl Reatangle {
    // 定义不以 self 为第一参数的关联函数,因为它们并不作用于一个结构体的实例。
    // 不是方法的关联函数经常被用作返回一个结构体新实例的构造函数。
    // 这些函数的名称通常为 new ，但 new 并不是一个关键字。
    fn square(size: u32) -> Self{
        Self { width: size, height: size }
    }
}

fn area_struct(s: Reatangle)-> u32 {
    s.width * s.height
}

fn area_tup(dimensions: (u32,u32)) -> u32 {
    dimensions.0 * dimensions.1
}

fn build_user(email: String, username: String) -> User {
    User{
        email: email,
        username: username,
        active: true,
        sign_in_count: 1,
    }
}

fn area(width: u32, height: u32) -> u32 {
    width * height
}