static LUCK: i32 = 123;

fn add_two_ints(x: i32, y: i32) -> i32 {
    println!("hiroong");
    x + y + LUCK
}

fn main() {
    println!("Hello, world!");
    println!("{}", add_two_ints(3, 4));
    for x in 0..10 {
        println!("{}", x);
    }
}
