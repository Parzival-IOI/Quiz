package com.group1.quiz.util;
import com.group1.quiz.enums.OrderEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class TableQueryBuilder {
    private final String search;
    private final String orderBy;
    private final OrderEnum order;
    private final int page;
    private final int size;

    public TableQueryBuilder(String search, String orderBy, OrderEnum order, int page, int size) {
        this.search = search;
        this.orderBy = orderBy;
        this.order = order;
        this.page = page;
        this.size = size;
    }

    public Query getQuery(){
        Query query = new Query();
        if(!StringUtils.isEmpty(search)) {
            query.addCriteria(Criteria.where("name").is(search));
        }
        if(order.equals(OrderEnum.DESC)) {
            query.with(Sort.by(Sort.Direction.DESC, orderBy));
        } else if(order.equals(OrderEnum.ASC)) {
            query.with(Sort.by(Sort.Direction.ASC, orderBy));
        }
        query.with(PageRequest.of(page, size));
        return query;
    }
}
