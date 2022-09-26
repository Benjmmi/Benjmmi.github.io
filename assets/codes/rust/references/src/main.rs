fn main() {
    // 数据除了 move 外还有引用
    // 引用（reference）像一个指针，因为它是一个地址，我们可以由此访问储存于该地址的属于其他变量的数据。
    let s1 = String::from("hello world");
    // & 符号就是 引用，它们允许你使用值但不获取其所有权。
    // 与使用 & 引用相反的操作是 解引用（dereferencing），它使用解引用运算符，*
    let len = calculate_length(&s1);
    println!("{}", len);
    let mut s2 = String::from("hello world");
    let len = change(&mut s2);
    println!("{}", len);

    // Rust 防止同一时间对同一数据存在多个可变引用。
    // 在编译期间就不允许同时存在多个引用
    // let r1 = &mut s2;
    // let r2 = &mut s2;
    // 但是可以使用大括号来创建一个新的作用域，以允许拥有多个可变引用，
    // 只是不能 同时 拥有：
    {
        let r1 = &mut s2;
        println!("{}", r1);
    }
    let r2 = &mut s2;
    println!("{}", r2);
    right_references();
}

// （默认）不允许修改引用的值。
fn calculate_length(s: &String) -> usize { // s 是一个引用的 String
    s.len()
    // 这里，s 离开了作用域。但因为它并不拥有引用值的所有权，
    // 不会回收 s 所以什么也不会发生
}

// 可变的引用
// 首先，我们必须将 s 改为 mut。然后在调用 change 函数的地方创建一个可变引用 &mut s，
// 并更新函数签名以接受一个可变引用 some_string: &mut String。
fn change(s: &mut String) -> usize{
    s.push_str("string");
    s.len()
}

fn right_references() {
    let mut s = String::from("hello world");
    // 不可变引用的用户可不希望在他们的眼皮底下值就被意外的改变了！然而，多个不可变引用是可以的，
    // 因为没有哪个只能读取数据的人有能力影响其他人读取到的数据。
    let r1 = &s; // 没问题
    let r2 = &s; // 没问题
    //  不可变引用 r1 和 r2 的作用域在 println! 最后一次使用之后结束
    println!("{} and {}",r1,r2);  // 此位置之后 r1 和 r2 不再使用，引用关系不存在

    let r3 = &mut s;
    println!("{}",r3);    
}

// 悬垂指针（dangling pointer），所谓悬垂指针是其指向的内存可能已经被分配给其它持有者。
// Rust 中编译器确保引用永远也不会变成悬垂状态：
// 当你拥有一些数据的引用，编译器确保数据不会在其引用之前离开作用域。
// fn dangle() -> &String { // dangle 返回一个字符串的引用
//     let s = String::from("hello"); // s 是一个新字符串

//     &s // 返回字符串 s 的引用
// } // 这里 s 离开作用域并被丢弃。其内存被释放。
// s 是在 dangle 函数内创建的，当 dangle 的代码执行完毕后，s 将被释放。
// 不过我们尝试返回它的引用。这意味着这个引用会指向一个无效的 String！
// Rust 不会允许我们这么做。所以 Rust 不允许存在悬挂指针