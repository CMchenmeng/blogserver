package org.sang.bean.vo;

import org.sang.bean.Article;

@SuppressWarnings("serial")
public class ArticleBean extends Article {

    private Integer quantity;

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
