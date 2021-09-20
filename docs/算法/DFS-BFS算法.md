## DFS-BFS 算法

这里主要介绍一些能用BFS- DFS算法求解的典型题目

#### [省份数量](https://leetcode-cn.com/problems/number-of-provinces/)

题目的意思就是一个N*N的矩阵，用`isConnected[i][j] = 1` 表示第 `i` 个城市和第 `j` 个城市直接相连，而 `isConnected[i][j] = 0` 表示二者不直接相连。其实就是岛屿问题，相邻的岛屿合并为一个，最后求岛屿的数量

- 解法一、DFS深度优先遍历
  - 从一个节点开始找，记录能连接的数量，最后和总数相减即可，代码如下：

```java
    public int findCircleNum(int[][] isConnected) {
        //城市的数量
        int length = isConnected.length;
        //表示哪些城市被访问过
        boolean[] visited = new boolean[length];
        int count = 0;//相连的城市数量，也就是省份
        //遍历所有的城市
        for (int i = 0; i < length; i++) {
            //如果当前城市没有被访问过，说明是一个新的省份，count
            //要加1，并且和这个城市相连的都标记为已访问过，也就是
            //同一省份的
            if (!visited[i]) {
                dfs(isConnected, visited, i);
                count++;
            }
        }
        //返回省份的数量
        return count;
    }

    public void dfs(int[][] isConnected, boolean[] visited, int i) {
        for (int j = 0; j < isConnected.length; j++) {
            if (isConnected[i][j] == 1 && !visited[j]) {
                //如果第i和第j个城市相连，说明他们是同一个省份的，把它标记为已访问过
                visited[j] = true;
                //然后继续查找和第j个城市相连的城市
                dfs(isConnected, visited, j);
            }
        }
    }

```

- BFS查询

```java
    public int findCircleNum(int[][] isConnected) {
        //城市的数量
        int length = isConnected.length;
        //表示哪些城市被访问过
        boolean[] visited = new boolean[length];
        //相连的城市数量，也就是省份
        int count = 0;
        //队列，存放的是同一个省份的城市
        Queue<Integer> queue = new LinkedList<>();
        //遍历所有的城市
        for (int i = 0; i < length; i++) {
            //如果当前城市被访问过，则跳过
            if (visited[i])
                continue;
            //否则表示遇到了一个新的城市，count要加1
            count++;
            //然后把当前城市加入到队列中
            queue.add(i);
            //这个队列中的所有城市都是同一省份的，需要把他们都
            //标记为已访问过，然后在继续查找和他们相连的，他们
            //也是同一省份的，也需要加入到队列中
            while (!queue.isEmpty()) {
                //出队
                int j = queue.poll();
                visited[j] = true;//把它标记为已访问过
                //然后再继续查找和他相连的并且没被访问过的城市
                for (int k = 0; k < length; k++) {
                    if (isConnected[j][k] == 1 && !visited[k]) {
                        queue.add(k);
                    }
                }
            }
        }
        return count;
    }
```

