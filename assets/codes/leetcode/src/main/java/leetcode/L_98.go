package sample

var stackNode []int

func isValidBST(root *TreeNode) bool {
	stackNode = []int{}
	helper(root)
	if len(stackNode) == 1 {
		return true
	}
	for i := 1; i < len(stackNode); i++ {
		if stackNode[i] <= stackNode[i-1] {
			return false
		}
	}
	return true
}

func helper(root *TreeNode) {
	if root == nil {
		return
	}
	helper(root.Left)
	stackNode = append(stackNode, root.Val)
	helper(root.Right)
}
