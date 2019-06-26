package org.sang.service;

import org.sang.bean.Category;
import org.sang.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sang on 2017/12/19.
 */
@Service
@Transactional
public class CategoryService {
    @Autowired
    CategoryMapper categoryMapper;

    public List<Category> getAllCategories() {
        return categoryMapper.getAllCategories();
    }

    public boolean deleteCategoryByIds(String ids) {
        String[] split = ids.split(",");
        int result = categoryMapper.deleteCategoryByIds(split);
        return result == split.length;
    }

    public int updateCategoryById(Category category) {
        return categoryMapper.updateCategoryById(category);
    }

    public int addCategory(Category category) {
        category.setDate(new Timestamp(System.currentTimeMillis()));
        return categoryMapper.addCategory(category);
    }

    public List<Category> getCategoriesById(Integer id){
        List<Category> list=new ArrayList<Category>();
        if(id == 1){            //获取法律法规下的所有子类别
            list = categoryMapper.getCategoriesById(20,35);
        }
        if(id == 2)             //获取法律法规下的所有子类别
            list = categoryMapper.getCategoriesById(36,50);
        return list;
    }
}
