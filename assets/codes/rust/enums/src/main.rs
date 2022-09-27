
#[derive(Debug)]
enum IpAddrKind {
    V4,
    V6,
}

#[derive(Debug)]
enum IpAddr {
    V4(String),
    // IpAddr 枚举的新定义表明了 V4 和 V6 成员都关联了 String 值
    V6(String),
    // IpAddr 枚举的新定义表明了 V4 和 V6 成员都关联了 String 值
    // 如果我们想要将 V4 地址存储为四个 u8 值而 V6 地址仍然表现为一个 String，
    // 这就不能使用结构体了。枚举则可以轻易的处理这个情况：
    v4(u8, u8, u8, u8),

}

// 更多枚举样式
enum Message {
    Quit,
    // 没有关联任何数据。
    Move { x: i32, y: i32 },
    // 类似结构体包含命名字段。
    Write(String),
    // 包含单独一个 String
    ChangeColor(i32, i32, i32), //  包含三个 i32
}

// 也可以在枚举上定义方法，与结构体一样
impl Message {
    fn call(&self) -> String {
        // 在这里定义方法体
        String::from("fadsfads")
    }
}

fn main() {
    let four = IpAddrKind::V4;
    let six = IpAddrKind::V6;
    route(four);
    println!("{:?}", six);
    // IpAddr::V4() 是一个获取 String 参数并返回 IpAddr 类型实例的函数调用。
    let home = IpAddr::V4(String::from("127.0.0"));
    let loopback = IpAddr::V6(String::from("::1"));
    println!("{:?},{:?}",home,loopback);
    let home2 = IpAddr::v4(127, 0, 0, 1);
    let loopback2 = IpAddr::V6(String::from("::1"));
    println!("{:?},{:?}",home2,loopback2);
    let m = Message::Write(String::from("Hello"));
    m.call();

    // some_number 的类型是 Option<i32>
    let some_number = Some(5);
    println!("{:?}",some_number);
    // some_char 的类型是 Option<char>
    let some_char = Some('3');
    println!("{:?}",some_char);
    // 编译器只通过 None 值无法推断出 Some 成员保存的值的类型
    // 所以告诉 Rust 希望 absent_number 是 Option<i32> 类型的
    // 有个 None 值时，在某种意义上，它跟空值具有相同的意义：并没有一个有效的值。
    let absend_number: Option<i32> = None;
    println!("{:?}",absend_number);
//     match 的极为强大的控制流运算符，
//      它允许我们将一个值与一系列的模式相比较，并根据相匹配的模式执行相应代码。
    let penny = value_in_cents(Coin::Penny);
    println!("{}",penny);
    let penny = value_in_cents(Coin::Quarter(UsState::Alabama));
    println!("{}",penny);

    let five = Some(5);
    let six = plus_one(five);
    println!("{:?}",six);
    let none = plus_one(None);
    println!("{:?}",none);
}


// 绑定值的模式,如何从枚举成员中提取值的
#[derive(Debug)]
enum UsState{
    Alabama,
    Alasks
}

impl UsState {
    fn Display(){
        println!("Hello Display")
    }
}

enum Coin {
    Penny,
    Nickel,
    Dime,
    Quarter(UsState),
}

fn value_in_cents(coin: Coin) -> u8 {
    match coin {
        //模式是值 Coin::Penny
        // => 运算符将模式和将要运行的代码分开
        Coin::Penny => {
            println!("Lucky penny!");
            1
        },
        Coin::Nickel => 5,
        Coin::Dime => 10,
        // 增加了一个叫做 state 的变量。
        // 当匹配到 Coin::Quarter 时，变量 state 将会绑定 25 美分硬币所对应州的值。
        Coin::Quarter(state) => {
            println!("State quarter from {:?}!", state);
            25
        }
    }
}

fn plus_one(x: Option<i32>)  -> Option<i32>{
    // Rust 中的匹配是穷举的方式，必须穷举到最后的可能性来使代码有效。
    match x {
        //如果没有处理 None 的情况，这些代码会造成一个 bug
        None => None,
        Some(x) => Some(x+1)
    }
}

fn route(ip_kind: IpAddrKind) {
    println!("{:?}",ip_kind)
}

// Option 枚举和其相对于空值的优势
// Option 是标准库定义的另一个枚举。
// Option 类型应用广泛因为它编码了一个非常普遍的场景，即一个值要么有值要么没值。