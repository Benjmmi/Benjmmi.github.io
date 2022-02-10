package kaoshi

type StringIterator struct {
	Char             byte
	Num              int
	Index            int
	compressedString []byte
}

func Constructor(compressedString string) StringIterator {
	s := StringIterator{
		Char:             compressedString[0],
		Num:              int(compressedString[1] - '0'),
		Index:            0,
		compressedString: []byte(compressedString),
	}
	return s
}

func (this *StringIterator) Next() byte {
	if this.Index < len(this.compressedString) {
		if this.Num > 0 {
			this.Num--
		} else {
			this.Index += 2
			if this.HasNext() {
				this.Char = this.compressedString[this.Index]
				this.Num = int(this.compressedString[this.Index+1] - '0')
				return this.Next()
			}
		}

		return this.Char
	}
	return ' '
}

func (this *StringIterator) HasNext() bool {
	return !(this.Index > len(this.compressedString)-1)
}
