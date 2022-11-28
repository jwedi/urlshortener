package com.johanwedin.urlshortener.db.cassandra.mapper;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import com.johanwedin.urlshortener.db.cassandra.dao.UrlDao;

@Mapper
public interface UrlMapper {
    @DaoFactory
    UrlDao urlDao(@DaoKeyspace CqlIdentifier keyspace);
}