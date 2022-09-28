fn main() {
    // 与其他变量一样如果想要能够改变它的值，必须使用 mut 关键字使其可变。
    let mut v: Vec<i32> = Vec::new();
    {
        let mut v = vec![1, 2, 3, 4];
        // 类似于任何其他的 struct，vector 在其离开作用域时会被释放
        // 当 vector 被丢弃时，所有其内容也会被丢弃，这意味着这里它包含的整数将被清理。
        v.push(5);
        v.push(6);
        v.push(7);
    }
    v.push(5);
    v.push(6);
    v.push(7);

    // 使用 & 和 [] 返回一个引用
    let third: &i32 = &v[2];
    println!("The third element is {}", third);

    //使用 get 方法以索引作为参数来返回一个 Option<&T>
    match v.get(2) {
        Some(third) => println!("The third element is {}", third),
        None => println!("There is no third element")
    }

    if let Some(thrid) = v.get(1) {
        println!("There third element is {}", thrid)
    } else {
        println!("There is no thrid element")
    }


    {
        let v = &vec![1, 2, 3, 4];
        // [] 取值方法，当引用一个不存在的元素时 Rust 会造成 panic。
        // let does_not_exist = &v[100];
        // get 方法被传递了一个数组外的索引时，它不会 panic 而是返回 None
        // let does_not_exist = v.get(300);
    }

    {
        // panic
        // 为什么第一个元素的引用会关心 vector 结尾的变化？
        // 不能这么做的原因是由于 vector 的工作方式：在 vector 的结尾增加新元素时，
        // 在没有足够空间将所有所有元素依次相邻存放的情况下，可能会要求分配新内存
        // 并将老的元素拷贝到新的空间中。这时，第一个元素的引用就指向了被释放的内存。
        // 借用规则阻止程序陷入这种状况。
        // let mut v = vec![1, 2, 3, 4, 5];
        // let first = &v[0];
        // v.push(6);
        // println!("The first element is: {}", first);
    }
    {
        //     遍历 vector 中的元素
        let v = vec![1, 2, 3, 4];
        for i in &v {
            println!("{}", i);
        }
    }
    {
        //     遍历并修改 vector 中的元素
        let mut v = vec![1, 2, 3, 4, 5];
        for i in &mut v {
            *i += 50;
            println!("{}", i);
        }
    }
//     Rust 在编译时就必须准确的知道 vector 中类型的原因在于它需要知道储存
// 每个元素到底需要多少内存。第二个好处是可以准确的知道这个 vector 中允许什么类型。
// 如果 Rust 允许 vector 存放任意类型，
// 那么当对 vector 元素执行操作时一个或多个类型的值就有可能会造成错误。
}
