package main

import (
	"github.com/asavie/xdp/pkg/service"
	"log"
	"net/http"
)

func main() {
	http.HandleFunc("/",service.Index)

	err := http.ListenAndServe(":8080",nil)
	if err != nil{
		log.Print("http server panic:",err)
	}
}
