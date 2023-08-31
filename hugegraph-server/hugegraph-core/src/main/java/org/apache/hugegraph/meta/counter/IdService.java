package org.apache.hugegraph.meta.counter;

public interface IdService {
    void resetIdByKey(String graphName);

    long getIdByKey(String key, int delta);
}
