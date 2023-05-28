package ru.demichev.movies.testUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.support.TransactionTemplate;

@TestComponent
public class TestDBFacade {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private TestEntityManager testEntityManager;


    public void cleanDatabase(){
        transactionTemplate.executeWithoutResult(
           transactionStatus -> JdbcTestUtils.deleteFromTables(jdbcTemplate,"movies.genre","movies.director","movies.actor","movies.movie","movies.user","movies.role")
        );
    }

    public <T> T persist(T t){
        return transactionTemplate.execute(
                status -> testEntityManager.persistAndFlush(t)
        );
    }

    public <T> long count(Class<?> clazz){
        return transactionTemplate.execute(
                status ->
                        (long) testEntityManager
                                .getEntityManager()
                                .createQuery("select count(e) from  " + clazz.getName() + " e")
                                .getSingleResult()
        );
    }

    public <T> Object getById(Class<?> clazz, Long id){
        return transactionTemplate.execute(
                status ->
                        testEntityManager
                                .getEntityManager()
                                .createQuery("select e from  " + clazz.getName() + " e where e.id =" + id)
                                .getSingleResult()
        );
    }
}
