package sample

var binary []int

func findIntegers(n int) int {
	num := 2
	binary = append(binary, 1)
	binary = append(binary, 2)
	for !(num >= 1000000000) {
		num = num << 1
		binary = append(binary, num)
	}
	n_binary := []int{}
	for n > 0 {
		n_binary = append(n_binary, n%2)
		n /= 2
	}
	sum := 0
	for i := 0; i < len(n_binary); i++ {
		if n_binary[i] == 1 {
			sum += reone(binary[i])
		}
	}
	return sum
}

func reone(n int) int {
	return (n<<1 - 1) - (n | (n >> 1))
}
