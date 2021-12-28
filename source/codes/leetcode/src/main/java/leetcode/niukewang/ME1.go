package niukewang

//import "fmt"
//
//func main() {
//	a := []int{1, 2, 3, 4, 5}
//	b := []int{6, 7, 8, 9, 10}
//	n := 5
//	k := 3
//	// 将值改为排名后几位
//	k = n*n - k + 1
//	fmt.Println("排名第", k, "位")
//	fmt.Println(slove(n, a, b, k))
//}

func find(val int, n int, a, b []int, k int) bool {
	// 大于当前值的数量统计
	cnt := 0
	i := 0
	j := n - 1

	for ; i < n; i++ {
		//  遍历数组
		for j > 0 && a[i]*b[j] > val {
			// 如果当前的值大于目标值，排名减一
			j--
		}
		// 得到当前排名
		cnt += j + 1
	}
	// 排名是不是太小了
	return cnt < k
}

func slove(n int, a, b []int, k int) int {
	l := a[0] * b[0]
	r := a[len(a)-1] * b[len(b)-1]

	for l < r {
		// middle value
		mid := (l + r) >> 1

		if find(mid, n, a, b, k) {
			// 当前排名小于 K ，说明太小了
			l = mid + 1
		} else {
			// 太大了
			r = mid
		}
	}
	return l
}
