package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.CategoryDao;
import cn.itcast.travel.dao.impl.CategoryDaoImpl;
import cn.itcast.travel.domain.Category;
import cn.itcast.travel.service.CategoryService;
import cn.itcast.travel.util.JedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CategoryServiceImpl implements CategoryService {
    public CategoryDao categoryDao=new CategoryDaoImpl();
    @Override
    public List<Category> getAll() {
        //1.从redis中查询
        //1.1获取jedis客户端
        Jedis jedis = JedisUtil.getJedis();
        //1.2查询cid和cname
        Set<Tuple> categorys = jedis.zrangeWithScores("category", 0, -1);//0到-1,查询全部

        //2.判断查询的集合是否为空
        List<Category> cs = null;
        if (categorys == null || categorys.size() == 0) {
            //3.如果为空，需要从数据库查询，再将数据存入redis
            //3.1从数据库查询
            cs = categoryDao.getAll();
            //3.2将集合数据存储到redis中的名为“category”的key
            for (int i = 0; i < cs.size(); i++) {
                jedis.zadd("category", cs.get(i).getCid(), cs.get(i).getCname());
            }
        } else {
            //4.如果不为空，将set数据存入list（因为我们返回要求的格式是list）
            cs = new ArrayList<Category>();
            for (Tuple tuple : categorys) {
                Category category = new Category();
                category.setCname(tuple.getElement());
                category.setCid((int)tuple.getScore());
                cs.add(category);
            }
        }

        return cs;//无论有无缓存，都要返回cs
    }
}


