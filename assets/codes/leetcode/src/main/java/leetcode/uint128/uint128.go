package main

import (
	"encoding/json"
	"fmt"
	"pkg.poizon.com/ee/ee-bi-sdk-go/pkg/common/consts"
	"pkg.poizon.com/ee/ee-bi-sdk-go/pkg/eebi"
	"pkg.poizon.com/ee/ee-bi-sdk-go/pkg/executor"
)

func main() {
	context := executor.EEBIContext{
		AppId:      "ee-bi_YaIuHkKi",
		AppSecret:  "4f7cb8b5cb53705e6971b83cd68ab72108ae3972",
		Endpoint:   "https://t1-eebi.shizhuang-inc.net",
		ApiVersion: consts.ApiVersionV1,
	}
	req := executor.NewEEBIDatasetRequest()
	req.DatasetId = "44bd0d5b-b662-4e31-b1a8-16ff6fc858f8"
	req.Conditions["appName"] = "eebi"
	req.Conditions["date"] = []string{"2022-03-17 15:21:23", "2022-03-17 15:18:12"}
	response, err := eebi.Dataset().Execute(req, context)
	if err != nil {
		fmt.Println("error:", err)
		return
	}
	bs, _ := json.Marshal(response)
	fmt.Println(string(bs))
}

func pr_str(s string) {
	b := &s
	*b = "fads"
}
