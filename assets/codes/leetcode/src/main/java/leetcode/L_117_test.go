package sample

import "testing"

func Test_connect(t *testing.T) {
	node := &Node{1, &Node{2, &Node{4, nil, nil, nil}, &Node{5, nil, nil, nil}, nil}, &Node{3, nil, &Node{7, nil, nil, nil}, nil}, nil}
	connect(node)
}
