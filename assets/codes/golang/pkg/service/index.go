package service

import (
	"math"
	"fmt"
	"net/http"
)

func Index(rep http.ResponseWriter,req *http.Request)  {
	var j float64
	for i := 0; i < 10000000; i++ {
		j +=math.Sqrt(float64(i))
	}
	str := fmt.Sprintf("It's Workd %d",j)
	rep.Write([]byte(str))
}
