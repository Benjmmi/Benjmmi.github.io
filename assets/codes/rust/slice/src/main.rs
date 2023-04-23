fn main() {
    let mut s = String::from("Hello, world!");
    let word = first_word(&s); // word 的值为 6
    s.clear();// 这清空了字符串，使其等于 ""
    println!("{}",word);
    println!("{}",s);
    let s2 = String::from("hello world!");
    // 如果想要从索引 0 开始，可以不写两个点号之前的值
    let hello = &s2[..5];
    // slice 包含 String 的最后一个字节，也可以舍弃尾部的数字
    let world =  &s2[6..];
    // 同时舍弃这两个值来获取整个字符串的 slice
    let slice = &s2[..];
    println!("{}",hello);
    println!("{}", world);
    println!("{}", s2);
    println!("{}",slice);
    let s_word = second_word(&slice);
    println!("{}",s_word);
    // Rust 不允许 clear 中的可变引用和 word 中的不可变引用同时存在

    // 这里 s 的类型是 &str：它是一个指向二进制程序特定位置的 slice。
    // 这也就是为什么字符串字面值是不可变的；&str 是一个不可变引用。
    let s3 = "hello world";
    println!("{}",s3);
    let a = [1, 2, 3, 4, 5];
    let int_slice = &a[..3];
    println!("{}",int_slice[0]);
}

fn first_word(s: &String)-> usize { 
    let bytes = s.as_bytes();
    // iter 方法返回集合中的每一个元素
    // enumerate 包装了 iter 的结果
    // enumerate 方法返回一个元组，我们可以使用模式来解构
    for (i, &item) in bytes.iter().enumerate() {
        if item == b' '{
            return i;
        }
    }
    s.len()
}


fn second_word(s: &str) -> &str {
    let bytes = s.as_bytes();
    for (i,&item) in bytes.iter().enumerate() {
        if item == b' '{
            return &s[0..i];
        }
    }
    &s[..]
}