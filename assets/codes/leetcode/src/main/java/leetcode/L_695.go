package sample

import (
	"container/list"
	"math"
)

type point struct {
	x int
	y int
}

var grade = []point{{1, 0}, {-1, 0}, {0, -1}, {0, 1}}

func maxAreaOfIsland(grid [][]int) int {
	m := len(grid)
	n := len(grid[0])
	maxArea := 0
	queue := list.New()

	for i := 0; i < m; i++ {
		for j := 0; j < n; j++ {
			if grid[i][j] == 1 {
				area := 1
				grid[i][j] = 0
				queue.PushBack(point{i, j})
				for queue.Len() != 0 {
					p := queue.Front().Value.(point)
					queue.Remove(queue.Front())

					for _, pt := range grade {
						if (p.x+pt.x) >= 0 && (p.x+pt.x) < m &&
							(p.y+pt.y) >= 0 && (p.y+pt.y) < n &&
							grid[p.x+pt.x][p.y+pt.y] != 0 {
							area++
							grid[p.x+pt.x][p.y+pt.y] = 0
							queue.PushBack(point{p.x + pt.x, p.y + pt.y})
						}
					}
				}
				maxArea = int(math.Max(float64(area), float64(maxArea)))
			}
		}
	}

	return maxArea
}
