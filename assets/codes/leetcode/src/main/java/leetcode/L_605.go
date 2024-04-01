package sample

func canPlaceFlowers(flowerbed []int, n int) bool {
	pre := -1
	c := 0
	length := len(flowerbed)
	i := 0
	for ; i < length; i++ {
		if flowerbed[i] == 1 {
			if pre == -1 {
				c += (i - pre) / 2
				pre = i
			} else if i-pre > 3 {
				c += (i - pre) / 3
				pre = i
			} else {
				pre = i
			}
		}
	}
	if pre == -1 {
		c += (i - pre) / 2
	} else if i-pre >= 2 {
		c += (i - pre) / 2
	}

	return n <= c
}
