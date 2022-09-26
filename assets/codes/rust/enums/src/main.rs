#[derive(Debug)]
enum IpAddrKind {
    V4,
    V6,
}

#[derive(Debug)]
enum IpAddr {
    V4(String),// IpAddr 枚举的新定义表明了 V4 和 V6 成员都关联了 String 值
    V6(String),// IpAddr 枚举的新定义表明了 V4 和 V6 成员都关联了 String 值
    // 如果我们想要将 V4 地址存储为四个 u8 值而 V6 地址仍然表现为一个 String，
    // 这就不能使用结构体了。枚举则可以轻易的处理这个情况：
    v4(u8,u8,u8,u8),
    
}

// 更多枚举样式
enum Message {
    Quit, // 没有关联任何数据。
    Move {x:i32, y:i32}, // 类似结构体包含命名字段。
    Write(String), // 包含单独一个 String
    ChangeColor(i32, i32, i32), //  包含三个 i32
}

// 也可以在枚举上定义方法，与结构体一样
impl Message{
    fn call(&self)->String{
        // 在这里定义方法体
        String::from("fadsfads")
    }
        
}

fn main() {
    let four = IpAddrKind::V4;
    let six = IpAddrKind::V6;
    println!("{:?}", four);
    println!("{:?}", six);
    // IpAddr::V4() 是一个获取 String 参数并返回 IpAddr 类型实例的函数调用。
    let home = IpAddr::V4(String::from("127.0.0"));
    let loopback = IpAddr::V6(String::from("::1"));
    // println!("{},{}",home,loopback);
    let home2 = IpAddr::v4(127,0,0,1);
    let loopback2 = IpAddr::V6(String::from("::1"));
    // println!("{},{}",home2,loopback2);
    let m = Message::Write(String::from("Hello"));
    m.call();
}

fn route(ip_kind: IpAddrKind){

}