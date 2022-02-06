package sample

var rootx map[int]int
var visit map[int]int

func findDistance(root *TreeNode, p int, q int) int {
	rootx = map[int]int{}
	visit = map[int]int{}
	buildRoot(root)
	count := 0
	visit[p] = count
	for p != root.Val {
		p = rootx[p]
		count++
		visit[p] = count
	}
	count = 0
	for {
		if _, es := visit[q]; !es {
			visit[q] = count
			q = rootx[q]
			count++
		} else {
			break
		}
	}
	return visit[q] + count
}

func buildRoot(root *TreeNode) {
	if root == nil {
		return
	}
	if root.Left != nil {
		rootx[root.Left.Val] = root.Val
		buildRoot(root.Left)
	}
	if root.Right != nil {
		rootx[root.Right.Val] = root.Val
		buildRoot(root.Right)
	}
}
