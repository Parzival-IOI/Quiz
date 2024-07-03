package com.group1.quiz.util;
import com.group1.quiz.enums.OrderEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@Slf4j
public class TableQueryBuilder {
    private final String search;
    private final String searchBy;
    private final String orderBy;
    private final OrderEnum order;
    private final int page;
    private final int size;

    public TableQueryBuilder(String search, String searchBy, String orderBy, OrderEnum order, int page, int size ) {
        this.search = search;
        this.searchBy = searchBy;
        this.orderBy = orderBy;
        this.order = order;
        this.page = page;
        this.size = size;
    }

    /**
     * Get query for searching, order by, order ASC DESC, and pagination
     * @return Query
     */
    public Query getQuery(){
        Query query = new Query();
        if(!StringUtils.isEmpty(search)) {
            query.addCriteria(Criteria.where(this.searchBy).regex(".*"+search+".*", "i"));
        }
        log.info(this.search);
        if(order.equals(OrderEnum.DESC)) {
            query.with(Sort.by(Sort.Direction.DESC, orderBy));
        } else if(order.equals(OrderEnum.ASC)) {
            query.with(Sort.by(Sort.Direction.ASC, orderBy));
        }
        query.with(PageRequest.of(page, size));
        return query;
    }
}
