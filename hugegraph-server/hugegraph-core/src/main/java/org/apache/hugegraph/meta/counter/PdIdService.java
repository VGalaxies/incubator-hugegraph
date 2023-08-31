package org.apache.hugegraph.meta.counter;

import org.apache.hugegraph.pd.client.PDClient;
import org.apache.hugegraph.pd.client.PDConfig;
import org.apache.hugegraph.pd.common.PDException;
import org.apache.hugegraph.pd.grpc.Pdpb;

public class PdIdService implements IdService {

    private final PDClient client;

    public PdIdService(PDClient client) {
        this.client = client;
    }

    @Override
    public void resetIdByKey(String graphName) {
        try {
            Pdpb.ResetIdResponse response = client.resetIdByKey(graphName);
            if (response.getResult() != 0) {
                throw new RuntimeException("PD client reset id error");
            }
        } catch (PDException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getIdByKey(String key, int delta) {
        try {
            Pdpb.GetIdResponse response = client.getIdByKey(key, delta);
            return response.getId();
        } catch (PDException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        PdIdService idService = new PdIdService(PDClient.create(PDConfig.of("127.0.0.1:8686")));
        System.out.println(idService.getIdByKey("123", 10000));
        System.out.println(idService.getIdByKey("123", 10000));
    }
}
