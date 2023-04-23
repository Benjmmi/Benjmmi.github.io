package sample

func read4(buf4 []byte) int {
	buf4[0] = 'a'
	buf4[1] = 'b'
	buf4[2] = 'c'
	return 3
}

var solution1 = func(read4 func([]byte) int) func([]byte, int) int {
	var str = []byte{}
	var pos = 0
	// implement read below.
	return func(buf []byte, n int) int {

		look := n/4 + 1
		for i := 0; i < look; i++ {
			cs := make([]byte, 4)
			r := read4(cs)
			str = append(str, cs[0:r]...)
			if r < 4 {
				break
			}
		}
		if pos+n >= len(str) {
			n = len(str) - pos
		}
		a := str[pos : pos+n]
		for i := 0; i < len(a); i++ {
			buf[i] = a[i]
		}
		pos += n
		return n
	}
}
