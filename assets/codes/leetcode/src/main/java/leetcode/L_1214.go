package sample

func twoSumBSTs(root1 *TreeNode, root2 *TreeNode, target int) bool {
	result := map[int]byte{}
	iter(root1, result)

	return iter2(root2, result, target)
}

func iter(root *TreeNode, result map[int]byte) {
	if root == nil {
		return
	}
	result[root.Val] = '0'
	iter(root.Left, result)
	iter(root.Right, result)
}

func iter2(root *TreeNode, result map[int]byte, target int) bool {
	if root == nil {
		return false
	}
	if _, e := result[target-root.Val]; e {
		return e
	}
	return iter2(root.Left, result, target) || iter2(root.Right, result, target)
}
