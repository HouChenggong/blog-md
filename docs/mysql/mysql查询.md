## 常用查询

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

