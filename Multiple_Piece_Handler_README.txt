
Works:
```
num_cubes=7
num_dimensions=3
search_start_depth=6
batch_size=2000
batch_index_to_search=0
```


Probably works:

I might go with a lower start depth though:
```
num_cubes=16
num_dimensions=3
search_start_depth=9
batch_size=17000
batch_index_to_search=2
```



Tested that summing up the results of batch_index_to_search index 0 to 2 of this config got f(12) (or 18598427 results)
```
num_cubes=12
num_dimensions=3
search_start_depth=7
batch_size=350
batch_index_to_search=2
```

no solutions for iteration:

Finished task.
Index pre-shuffle to post-shuffle: 70 to 382
Done piece index: 382
Num solutions found after 1 iteration(s): 641981902
-----


---

New task description:
Target task index: 249
start depth: 7
5.|
4.|
1.|
02|
3.|


Run compute task for index 249: (Please wait)
Finished task.
Index pre-shuffle to post-shuffle: 71 to 249
Done piece index: 249
Num solutions found after 2 iteration(s): 641981902
-----
(It actually makes sense)
