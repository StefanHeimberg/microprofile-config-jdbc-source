package com.example.myproject.config.test;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;

@RequestScoped
@Alternative
public class EntityManagerProducer {

    private static final String UNIT_NAME = "myproject-test";

    @Produces
    @RequestScoped
    public EntityManager connectionProducer() {
        return Persistence.createEntityManagerFactory(UNIT_NAME).createEntityManager();
    }

    public void close(@Disposes @Any EntityManager em) {
        if (em.isOpen()) {
            em.close();
        }
    }

}
