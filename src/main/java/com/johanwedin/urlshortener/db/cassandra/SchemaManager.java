package com.johanwedin.urlshortener.db.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.johanwedin.urlshortener.db.cassandra.entities.UrlMapping;

import java.util.List;

public class SchemaManager {

    public static void initKeyspace(CqlSession session, String keyspace, int replication) {
            session.execute(String.format("CREATE KEYSPACE IF NOT EXISTS %s WITH replication = \n" +
                    "{'class':'SimpleStrategy','replication_factor':'%d'};", keyspace, replication));
        }


    public static void initTables(CqlSession session, String keyspace) {
        UrlMapping.createTable(session, keyspace);
    }
}
