def f(n,m,k):
	i=0
	check = False
	d={}
	while i <= m or not check:
		for j in range(n):
			if not k[i][j] in d:
				if (j <n//2):
					d[k[i][j]] = set(k[i][n//2:])
				else:
					d[k[i][j]] = set(k[i][:n//2])

		i ++ 1
	return True


n=int(input())
m=int(input())
k= [[int(val) for val in pair.split()] for pair in input().strip().split(',')]

f(n,m,k)
6
6
3 1 4 5 6 2, 5 3 2 4 1 6, 5 3 6 4 2 1, 6 5 3 2 1 4, 5 4 1 2 6 3, 4 1 6 2 5 3

6
6
1 6 3 4 5 2, 6 4 2 3 1 5, 4 2 1 5 6 3, 4 5 1 6 2 3, 3 2 5 1 6 4, 2 3 6 4 1 5


all_permutations = []
expected_permutations = int((n * m)/ (n/2))
for i in range(m):
	all_permutations.append(k[i][:n//2])
	all_permutations.append(k[i][n//2:])
unique_data = [list(x) for x in set(tuple(x) for x in all_permutations)]
check = expected_permutations == len(unique_data)