package shardmaster

import (
	"fmt"
	"testing"
)

func TestMakeMapping(t *testing.T) {
	got := MakeMapping([]int{0, 0, 1, 1, 1, 2, 2, 3, 3, 4})
	expected := map[int][]int{
		0: {0, 1},
		1: {2, 3, 4},
		2: {5, 6},
		3: {7, 8},
		4: {9},
	}
	for server, shards := range got {
		expectedShards, ok := expected[server]
		if !ok {
			t.Fail()
		}
		for i := range shards {
			if shards[i] != expectedShards[i] {
				t.Fail()
			}
		}
	}
}

func TestReBalance1(t *testing.T) {
	fmt.Printf("Test: rebalance with more servers, 1 to 2, 0 left\n")
	got := []int{0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
	expected := []int{0, 0, 0, 0, 0, 1, 1, 1, 1, 1}
	ReBalance(got, map[int][]string{0: {""}, 1: {""}})
	for server := range got {
		if got[server] != expected[server] {
			t.Fail()
		}
	}
}

func TestReBalance2(t *testing.T) {
	fmt.Printf("Test: rebalance with more servers, 1 to 2, 1 left\n")
	got := []int{0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
	expected := []int{1, 1, 1, 1, 1, 2, 2, 2, 2, 2}
	ReBalance(got, map[int][]string{1: {""}, 2: {""}})
	for server := range got {
		if got[server] != expected[server] {
			t.Fail()
		}
	}
}

func TestReBalance3(t *testing.T) {
	fmt.Printf("Test: rebalance with more servers, 1 to 3, 0 left\n")
	got := []int{0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
	expected := []int{0, 0, 0, 0, 1, 1, 1, 2, 2, 2}
	ReBalance(got, map[int][]string{0: {""}, 1: {""}, 2: {""}})
	for server := range got {
		if got[server] != expected[server] {
			t.Fail()
		}
	}
}

func TestReBalance4(t *testing.T) {
	fmt.Printf("Test: rebalance with more servers, 3 to 4, 1 left\n")
	got := []int{0, 0, 0, 0, 1, 1, 1, 2, 2, 2}
	expected := []int{0, 0, 0, 3, 1, 1, 1, 3, 4, 4}
	ReBalance(got, map[int][]string{0: {""}, 1: {""}, 3: {""}, 4: {""}})
	for server := range got {
		if got[server] != expected[server] {
			t.Fail()
		}
	}
}

func TestReBalance5(t *testing.T) {
	fmt.Printf("Test: rebalance with fewer servers, 2 to 1, 1 join\n")
	got := []int{0, 0, 0, 0, 0, 1, 1, 1, 1, 1}
	expected := []int{2, 2, 2, 2, 2, 2, 2, 2, 2, 2}
	ReBalance(got, map[int][]string{2: {""}})
	for server := range got {
		if got[server] != expected[server] {
			t.Fail()
		}
	}
}

func TestReBalance6(t *testing.T) {
	fmt.Printf("Test: rebalance with fewer servers, 4 to 3, 0 join\n")
	got := []int{0, 0, 0, 2, 1, 1, 1, 3, 2, 3}
	expected := []int{0, 0, 0, 2, 1, 1, 1, 0, 2, 2}
	ReBalance(got, map[int][]string{0: {""}, 1: {""}, 2: {""}})
	for server := range got {
		if got[server] != expected[server] {
			t.Fail()
		}
	}
}

func TestReBalance7(t *testing.T) {
	fmt.Printf("Test: rebalance with fewer servers, 5 to 4, 1 join\n")
	got := []int{0, 0, 1, 1, 2, 2, 3, 3, 4, 4}
	expected := []int{0, 0, 1, 1, 2, 2, 0, 1, 5, 5}
	ReBalance(got, map[int][]string{0: {""}, 1: {""}, 2: {""}, 5: {""}})
	for server := range got {
		if got[server] != expected[server] {
			t.Fail()
		}
	}
}
