## 常见SQL查询

#### 排名第几——limit

Limit 2,1代表第三

limit 5,5查询第6-10行记录

limit 5查询前五行记录

limit n 等价于 limit 0,n

```java
select * from employees order by hire_date desc  limit 2,1
```

#### 查询薪水第二高的员工

- 如果薪水都不相同则直接用limit即可

```sql
select emp_no , salary from salaries
order by salary desc limit 1,1
```

- 如果薪水第二高的有10个人

```sql
select s.emp_no ,s.salary 
from salaries s where s.salary = 
(select distinct salary  from salaries 
 order by salary desc limit 1,1)
```



#### having和group by

查询薪水涨幅大于15次的员工

```sql
select emp_no ,count(distinct salary) t
from salaries 
group by emp_no
having t >15
```

#### 查询每个部门中薪水最高的员工

- 先给一个错误的方案

```sql
SELECT
    a.dept_no AS dept_no,
    a.emp_no AS emp_no,
    MAX(b.salary) AS salary
FROM
    dept_emp a , salaries b
    ON a.emp_no = b.emp_no
GROUP BY
    a.dept_no
ORDER BY
    a.dept_no ASC;
```

其实就是一个问题：如果同部门有多条同等最大salary，要不要一起显示出来

- 如果同部门有多条同等最大salary，仅显示一条，这样其实和上面写的一样

```sql
select r.dept_no, r.emp_no, max(r.salary)
from (
         select d.dept_no, d.emp_no, s.salary
         from dept_emp d,
              salaries s
         where d.emp_no = s.emp_no

         order by s.salary desc
     ) as r
group by r.dept_no
order by r.dept_no asc
```

- 如果同部门有多条同等最大salary，都显示出来
  - 先查询每个部门的最大薪资
  - 再带入查询

```sql
select r.dept_no, ss.emp_no, r.maxSalary
from (
         select d.dept_no, max(s.salary) as maxSalary
         from dept_emp d, salaries s
         where d.emp_no = s.emp_no
         group by d.dept_no
     ) as r,
     salaries ss,dept_emp dd
where r.maxSalary = ss.salary ## 薪资等于部门最大薪资
  and r.dept_no = dd.dept_no 
  and dd.emp_no = ss.emp_no
order by r.dept_no asc
```

#### 手写rank排序

参见：[Rank排序](https://blog.csdn.net/qq_39455116/article/details/87923035)

最后的排名结果是：1、1、2、2、3、4、5，即分数相同的排名相同

```sql
select s.emp_no ,s.salary ,count(distinct s2.salary) t_rank
from salaries s ,salaries s2 
where s.salary <=s2.salary
group by s.emp_no
order by t_rank 
```

如果排名的结果是：1、1、3、3、5、6、7

```sql
select s.emp_no ,s.salary ,count(  s2.salary) t_rank
from salaries s ,salaries s2 
where s.salary <=s2.salary
group by s.emp_no
order by t_rank 
```

#### 最差是第几名

和上面rank排序对应

```sql
select a.grade, sum(b.number)
from class_grade a, class_grade b
where a.grade >= b.grade
group by a.grade
order by a.grade 
```



#### 同一个用户只保留最小记录的ID

```sql
delete from titles_test where id not in 
 (select min(id) from titles_test group by emp_no)
```

#### 查询每个用户最近的登陆时间

```sql
select user_id,max(date)as d 
from login group by user_id  order by user_id
```

#### 大于每门课程平均分的学生

- 查询每门课程的平均分

```java
select job , round(avg(score),3)avg
from grade group by job
order by avg desc
```

- 查询大于每门课程平均分的学生

```sql
select g.* from grade g
where g.score > (
    select avg(score) avg
    from grade g2
    where g2.job = g.job
    group by g2.job
)
order by g.id
```

#### 查询每门课程的前两名

```sql
-- 不会用窗口函数，泪目！
SELECT 
    g.id,
    l.name,
    g.score
FROM grade g
JOIN language l ON g.language_id = l.id
WHERE g.score IN (
    SELECT a.score
    FROM (
        SELECT DISTINCT g1.score
        FROM  grade g1
        WHERE g1.language_id = g.language_id
        ORDER BY g1.score DESC
        LIMIT 2
    ) AS a
)
ORDER BY l.name,g.score DESC,g.id
```

#### 查询第一名

```mysql
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `class_id` int(11) DEFAULT NULL,
  `score` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `language` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of student
-- ----------------------------
BEGIN;
INSERT INTO `student` VALUES (1, 1, '99', 'A', '语文');
INSERT INTO `student` VALUES (2, 1, '97', 'B', '语文');
INSERT INTO `student` VALUES (3, 1, '65', 'C', '语文');
INSERT INTO `student` VALUES (4, 2, '75', 'D', '语文');
INSERT INTO `student` VALUES (5, 1, '25', 'A', '数学');
INSERT INTO `student` VALUES (6, 1, '76', 'B', '数学');
COMMIT;
```

- 查看某个班级中各科成绩最高的学生成绩以及名称

```sql
select distinct name, score, language from student as a 
where score = 
(select  max(b.score) from student as b where a.class_id=1 and b.language = a.language ) and class_id = 1;
```

- 查看每个班级的各科第一名的成绩

```sql
select distinct class_id, name, score, language from student as a
where score = 
(select  max(b.score) from student as b where a.class_id=b.class_id and b.language = a.language );
```

