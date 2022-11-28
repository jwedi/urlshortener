package com.johanwedin.urlshortener.db.cassandra.entities;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.schema.CreateTable;
import com.google.common.base.Objects;

@Entity
public class UrlMapping {
    private static final String TABLE_NAME = "url_mapping";
    @PartitionKey private String urlId;
    private String originalUrl;

    public UrlMapping() {}

    public UrlMapping(String urlId, String originalUrl) {
        this.urlId = urlId;
        this.originalUrl = originalUrl;
    }

    public String getUrlId() {
        return urlId;
    }

    public void setUrlId(String urlId) {
        this.urlId = urlId;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public static void createTable(CqlSession session, String keyspace) {
        CreateTable createTableStatement = SchemaBuilder.createTable(keyspace, TABLE_NAME).ifNotExists()
                .withPartitionKey("url_id", DataTypes.TEXT)
                .withColumn("original_url", DataTypes.TEXT);

        session.execute(createTableStatement.build());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UrlMapping that = (UrlMapping) o;
        return Objects.equal(urlId, that.urlId) && Objects.equal(originalUrl, that.originalUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(urlId, originalUrl);
    }
}
