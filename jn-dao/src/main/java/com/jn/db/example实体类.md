这几天刚接触example，很多内容都是破碎的，写一篇博文加深理解。

一、什么是example类

     mybatis-generator会为每个字段产生如上的Criterion，如果表的字段比较多,产生的Example类会十分庞大。理论上通过example类可以构造你想到的任何筛选条件。在mybatis-generator中加以配置，配置数据表的生成操作就可以自动生成example了。具体配置可以参考MBG有关配置。
     下面是mybatis自动生成example的使用。

二、了解example成员变量

1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
//升序还是降序
  //参数格式：字段+空格+asc(desc)
  protected String orderByClause;
  //去除重复
  //true是选择不重复记录
  protected boolean distinct;
  //自定义查询条件
  //Criteria的集合，集合中对象是由or连接
  protected List<Criteria> oredCriteria;
  //内部类Criteria包含一个Cretiron的集合，
  //每一个Criteria对象内包含的Cretiron之间
  //是由AND连接的
  public static class Criteria extends GeneratedCriteria {
   protected Criteria() {
    super(); 
   }
  }
  //是mybatis中逆向工程中的代码模型
  protected abstract static class GeneratedCriteria
  {…..}
  //是最基本,最底层的Where条件，用于字段级的筛选
  public static class Criterion {……}
三、example使用前的准备

     比如我的example是根据user表生成的，UserMapper属于dao层，UserMapper.xml是对应的映射文件
     UserMapper接口：

1
2
long countByExample(CompetingStoreExample example);
List<CompetingStore> selectByExample(CompetingStoreExample example);
    在我们的测试类里：

1
2
     UserExample example = new UserExample();
     UserExample.Criteria criteria = example.createCriteria();
四、查询用户数量

1
long count = UserMapper.countByExample(example);
      类似于：select count(*) from user

五、where条件查询或多条件查询

1
2
3
4
5
6
7
8
9
10
11
12
example.setOrderByClause("age asc");//升序
     example.setDistinct(false);//不去重
  
     if(!StringUtils.isNotBlank(user.getName())){
          Criteria.andNameEqualTo(user.getName());
     }
  
     if(!StringUtils.isNotBlank(user.getSex())){
          Criteria.andSexEqualTo(user.getSex());
     }
  
     List<User> userList=userMapper.selectByExample(example);
     类似于：select * from user where name={#user.name} and sex={#user.sex} order by age asc;

1
2
3
4
5
6
7
8
9
10
11
12
13
UserExample.Criteria criteria1 = example.createCriteria();
     UserExample.Criteria criteria2 = example.createCriteria();
  
     if(!StringUtils.isNotBlank(user.getName())){
          Criteria1.andNameEqualTo(user.getName());
     }
  
     if(!StringUtils.isNotBlank(user.getSex())){
          Criteria2.andSexEqualTo(user.getSex());
     }
  
     Example.or(criteria2);
     List<User> userList=userMapper.selectByExample(example);
     类似于：select * from user where name={#user.name} or sex={#user.sex} ;

六、模糊查询

1
2
3
4
5
      if(!StringUtils.isNotBlank(user.getName())){
           criteria.andNameLIke(‘%'+name+'%');
      }
  
      List<User>  userList=userMapper.selectByExample(example);
      类似于：

1
select * from user where name like %{#user.name}%
七、分页查询

1
2
3
4
5
6
        int start = (currentPage - 1) * rows;
        //分页查询中的一页数量
        example.setPageSize(rows);   
        //开始查询的位置
        example.setStartRow(start);
        List<User> userList=userMapper.selectByExample(example);
        类似于：

1
select * from user limit start to rows