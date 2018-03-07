package com.ggp.theclub.util;

import com.ggp.theclub.BaseTest;
import com.ggp.theclub.model.Category;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import java8.util.stream.StreamSupport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CategoryUtilsTest extends BaseTest {

    @Test
    public void testGetDisplayNameForCategories() {
        Category category1 = new Category();
        Category category2 = new Category();
        Category category3 = new Category();
        category1.setLabel("Shopping");
        category2.setLabel("Store Opening");
        category3.setLabel("Men's Fashion");
        category1.setCode("SHOPPING");
        category2.setCode("STORE_OPENING");
        category3.setCode("MENS_FASHION");
        List<Category> categories = Arrays.asList(category1, category2, category3);

        assertEquals("Men's Fashion, Shopping", CategoryUtils.getDisplayNameForCategories(categories));
        assertEquals("", CategoryUtils.getDisplayNameForCategories(null));
    }

    @Test
    public void testMapChildCategories() {
        Category parent1 = new Category();
        Category parent2 = new Category();
        Category child1 = new Category();
        Category child2 = new Category();
        parent1.setId(1);
        parent2.setId(2);
        child1.setParentId(1);
        child2.setParentId(1);
        List<Category> categories = Arrays.asList(parent1, parent2, child1, child2);

        List<Category> mappedCategories = CategoryUtils.mapChildCategories(categories);
        Category mappedParent1 = StreamSupport.stream(mappedCategories).filter(c -> c.getId() == 1).findFirst().get();
        Category mappedParent2 = StreamSupport.stream(mappedCategories).filter(c -> c.getId() == 2).findFirst().get();

        assertEquals(2, mappedCategories.size());
        assertEquals(2, mappedParent1.getChildCategories().size());
        assertEquals(0, mappedParent2.getChildCategories().size());
        assertTrue(mappedParent1.getChildCategories().contains(child1));
        assertTrue(mappedParent1.getChildCategories().contains(child2));

        mappedCategories = CategoryUtils.mapChildCategories(null);
        assertTrue(mappedCategories.isEmpty());
    }

    @Test
    public void testCategoriesContainCode() {
        Category category1 = new Category();
        Category category2 = new Category();
        category1.setCode("code1");
        category2.setCode("code2");
        List<Category> categories = Arrays.asList(category1, category2);

        assertFalse(CategoryUtils.categoriesContainCode(null, "code"));
        assertTrue(CategoryUtils.categoriesContainCode(null, null));
        assertTrue(CategoryUtils.categoriesContainCode(categories, null));
        assertFalse(CategoryUtils.categoriesContainCode(categories, "code"));
        assertTrue(CategoryUtils.categoriesContainCode(categories, "code1"));
        assertTrue(CategoryUtils.categoriesContainCode(categories, "code2"));
    }
}
