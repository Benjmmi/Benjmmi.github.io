package sample

import "sort"

func groupAnagrams(strs []string) [][]string {
	result := make(map[string][]string)
	for _, s := range strs {
		var data []byte = []byte(s)
		sort.Slice(data, func(i, j int) bool {
			return data[i] > data[j]
		})
		str := string(data)
		if v, e := result[str]; e {
			v = append(v, s)
			result[str] = v
		} else {
			result[str] = []string{s}
		}
	}
	var res = [][]string{}
	for _, strings := range result {
		res = append(res, strings)
	}
	return res
}
