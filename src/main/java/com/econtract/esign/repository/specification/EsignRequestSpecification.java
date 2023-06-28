/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.econtract.esign.repository.specification;

import com.econtract.esign.model.EsignRequest;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;


public class EsignRequestSpecification implements Specification<EsignRequest> {
 
    private SearchCriteria criteria;

    public EsignRequestSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }
    
    

    @Override
    public javax.persistence.criteria.Predicate toPredicate(Root<EsignRequest> root, CriteriaQuery<?> cq, CriteriaBuilder builder) {
        
        if (criteria.getOperation().equalsIgnoreCase(">")) {
            return builder.greaterThanOrEqualTo(
              root.<String> get(criteria.getKey()), criteria.getValue().toString());
        } 
        else if (criteria.getOperation().equalsIgnoreCase("<")) {
            return builder.lessThanOrEqualTo(
              root.<String> get(criteria.getKey()), criteria.getValue().toString());
        } 
        else if (criteria.getOperation().equalsIgnoreCase(":")) {
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                return builder.like(
                  root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%");
            } else {
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            }
        } 
        else if (criteria.getOperation().equalsIgnoreCase("in")) {
            CriteriaBuilder.In<Integer> inClause = builder.in(root.get(criteria.getKey()));
            List<Integer> branches = (List<Integer>) criteria.getValue();
            for (Integer t : branches) {
                inClause.value(t);
            }
            return inClause;
        }
        return null;
    }
 
    
}