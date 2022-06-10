package niukewang

import "fmt"

type Node struct {
	Val  int
	Next *Node
}

func Reserved(pHeader *Node) *Node {
	var pReversedHead *Node
	var pNode = pHeader
	var pPrev *Node

	for pNode != nil {
		pNext := pNode.Next
		if pNext == nil {
			pReversedHead = pNode
		}
		pNode.Next = pPrev
		pPrev = pNode
		pNode = pNext
	}

	for {
		fmt.Print(pReversedHead.Val, "->")
		if pReversedHead.Next == nil {
			break
		}
		pReversedHead = pReversedHead.Next
	}
	return nil
}
