package sample

var kv = map[string]int{"()": 1}
var arr = map[int][]string{1: {"()"}}

func generateParenthesis(n int) []string {
	if len(arr[n]) > 0 {
		return arr[n]
	}

	//insert()
	for i := 1; i <= n; i++ {
		if len(arr[i]) > 0 && arr[i][0] != "" {
			continue
		}
		for j := 0; j < len(arr[i-1]); j++ {
			for k := 0; k < len(arr[i-1][j]); k++ {
				nk := arr[i-1][j][:k] + "()" + arr[i-1][j][k:]
				if _, ex := kv[nk]; !ex {
					kv[nk] = i
					arr[i] = append(arr[i], nk)
				}
				// 只考虑当前是 左括号的情况
				if arr[i-1][j][k] == '(' {
					pairs := 1
					for p := k; p < len(arr[i-1][j]); p++ {
						if arr[i-1][j][p] == '(' {
							pairs += 1
						}
						if arr[i-1][j][p] == ')' {
							pairs -= 1
						}
						if pairs == 0 {
							ap := arr[i-1][j][:p] + ")" + arr[i-1][j][p:]
							ap = ap[:k] + "(" + ap[k:]
							if _, ex := kv[ap]; !ex {
								kv[ap] = i
								arr[i] = append(arr[i], ap)
							}
							break
						}
					}
				}
			}
		}
	}

	return arr[n]
}
