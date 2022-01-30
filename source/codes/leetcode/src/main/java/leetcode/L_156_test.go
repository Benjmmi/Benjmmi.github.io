package sample

import "testing"

func Test_upsideDownBinaryTree(t *testing.T) {
	root := &TreeNode{Val: 1}
	root.Left = &TreeNode{Val: 2, Left: &TreeNode{Val: 4}, Right: &TreeNode{Val: 5}}
	root.Right = &TreeNode{Val: 3}
	upsideDownBinaryTree(root)
}
