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

import hu.tigra.jee.model.Allocation;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Produces;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "filteredView")
@RequestScoped
public class AllocationListProducer {


    @Inject
    private AllocationRepository allocationRepository;

    private List<Allocation> allocations;
    private List<Allocation> allocations1;
    private List<Allocation> allocations2;

    // @Named provides access the return value via the EL variable name "members" in the UI (e.g.
    // Facelets or JSP view)
    @Produces
    @Named
    public List<Allocation> getAllocations() {
        List<Allocation> filteredAllocation = new ArrayList();
        for (Allocation allocation : allocations) {
            if (allocation.getRoomId() == 1) {
                filteredAllocation.add(allocation);
            }
        }
        return filteredAllocation;
    }

    @Produces
    @Named
    public List<Allocation> getAllocations1() {
        List<Allocation> filteredAllocation = new ArrayList();
        for (Allocation allocation : allocations) {
            if (allocation.getRoomId() == 2) {
                filteredAllocation.add(allocation);
            }
        }
        return filteredAllocation;
    }

    @Produces
    @Named
    public List<Allocation> getAllocations2() {
        List<Allocation> filteredAllocation = new ArrayList();
        for (Allocation allocation : allocations) {
            if (allocation.getRoomId() == 3) {
                filteredAllocation.add(allocation);
            }
        }
        return filteredAllocation;
    }

    public void onAllocationListChanged(@Observes(notifyObserver = Reception.IF_EXISTS) final Allocation allocation) {
        retrieveAllAllocationsOrderedByDate();
    }

    @PostConstruct
    public void retrieveAllAllocationsOrderedByDate() {
        allocations = allocationRepository.findAllOrderedByDate();
    }


}
