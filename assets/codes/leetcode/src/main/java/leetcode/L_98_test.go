package sample

import (
	"fmt"
	"testing"
)

func Test_isValidBST(t *testing.T) {
	fmt.Println(isValidBST(&TreeNode{Val: 0}))
}
