package com.johanwedin.urlshortener.db.cassandra.dao;


import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.johanwedin.urlshortener.db.cassandra.entities.UrlMapping;

import java.util.Optional;

@Dao
public interface UrlDao {
    @Select
    Optional<UrlMapping> findById(String urlId);

    @Insert(ifNotExists = true)
    Optional<UrlMapping> insertIfNotExists(UrlMapping urlMapping);
}
