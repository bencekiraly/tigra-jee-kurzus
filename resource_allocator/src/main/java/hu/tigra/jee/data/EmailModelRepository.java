/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hu.tigra.jee.data;

import hu.tigra.jee.model.EmailModel;
import org.hibernate.validator.constraints.Email;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

import static hu.tigra.jee.model.EmailModel_.sender;

@ApplicationScoped
public class EmailModelRepository {

    @Inject
    private EntityManager em;

    public EmailModel findById(Long id) {
        return em.find(EmailModel.class, id);
    }

    public EmailModel findByEmail(String email) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EmailModel> criteria = cb.createQuery(EmailModel.class);
        Root<EmailModel> emailModel = criteria.from(EmailModel.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(member).where(cb.equal(member.get(Member_.name), email));
        criteria.select(emailModel).where(cb.equal(emailModel.get("sender"), sender));
        return em.createQuery(criteria).getSingleResult();
    }

    public List<EmailModel> findAllOrderedById() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EmailModel> criteria = cb.createQuery(EmailModel.class);
        Root<EmailModel> emailModel = criteria.from(EmailModel.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(member).orderBy(cb.asc(member.get(Member_.name)));
        criteria.select(emailModel).orderBy(cb.asc(emailModel.get("id")));
        return em.createQuery(criteria).getResultList();
    }
}
