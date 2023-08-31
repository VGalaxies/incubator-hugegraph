package org.apache.hugegraph.meta.counter;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.hugegraph.meta.EtcdMetaDriver;

/**
 * refer to org.apache.hugegraph.pd.meta.IdMetaStore
 */
public class EtcdIdService implements IdService {
    private final EtcdMetaDriver driver;

    private static final ConcurrentHashMap<String, Object> SEQUENCES = new ConcurrentHashMap<>();

    public EtcdIdService(EtcdMetaDriver driver) {
        this.driver = driver;
    }

    @Override
    public void resetIdByKey(String graphName) {
        resetId(graphName);
    }

    @Override
    public long getIdByKey(String key, int delta) {
        return getId(key, delta);
    }

    private long getId(String key, int delta) {
        Object probableLock = getLock(key);
        synchronized (probableLock) {
            String value = driver.get(key);
            long current = value != null ? bytesToLong(value.getBytes()) : 0L;
            long next = current + delta;
            driver.put(key, Arrays.toString(longToBytes(next)));
            return current;
        }
    }

    private Object getLock(String key) {
        Object probableLock = new Object();
        Object currentLock = SEQUENCES.putIfAbsent(key, probableLock);
        if (currentLock != null) {
            probableLock = currentLock;
        }
        return probableLock;
    }

    private void resetId(String key) {
        Object probableLock = new Object();
        Object currentLock = SEQUENCES.putIfAbsent(key, probableLock);
        if (currentLock != null) {
            probableLock = currentLock;
        }
        synchronized (probableLock) {
            driver.deleteWithPrefix(key);
        }
    }

    private static long bytesToLong(byte[] b) {
        ByteBuffer buf = ByteBuffer.wrap(b);
        return buf.getLong();
    }

    private static byte[] longToBytes(long l) {
        ByteBuffer buf = ByteBuffer.wrap(new byte[Long.SIZE]);
        buf.putLong(l);
        buf.flip();
        return buf.array();
    }
}
