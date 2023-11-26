package sample

type Node struct {
	Val   int
	Left  *Node
	Right *Node
	Next  *Node
}

/*
*
解题思考
层次遍历加记忆
在层次遍历是会将同一层级的节点，存储到数组，所以一直指向的是右边
*/
func connect(root *Node) *Node {
	if root == nil {
		return nil
	}
	queue := []*Node{}
	queue = append(queue, root)
	for {
		if len(queue) == 0 {
			break
		}
		tmp := queue
		queue = []*Node{}
		for i, node := range tmp {
			if i+1 < len(tmp) {
				node.Next = tmp[i+1]
			}
			if node.Left != nil {
				queue = append(queue, node.Left)
			}
			if node.Right != nil {
				queue = append(queue, node.Right)
			}
		}

	}
	return root
}
