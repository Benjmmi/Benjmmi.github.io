package sample

var graph [][]int
var rb map[int]int // 0 红,1 蓝

func possibleBipartition(n int, dislikes [][]int) bool {
	graph = make([][]int, n+1)
	rb = make(map[int]int)
	for i := 0; i < n+1; i++ {
		graph[i] = []int{}
	}
	for _, edge := range dislikes {
		graph[edge[0]] = append(graph[edge[0]], edge[1])
		graph[edge[1]] = append(graph[edge[1]], edge[0])
	}
	for node := 1; node <= n; node++ {
		if _, exist := rb[node]; !exist && !dsf(node, 0) {
			return false
		}
	}
	return true
}

func dsf(node, color int) bool {
	if c, exist := rb[node]; exist {
		return c == color
	}
	rb[node] = color
	for _, i2 := range graph[node] {
		if !dsf(i2, color^1) {
			return false
		}
	}
	return true
}
