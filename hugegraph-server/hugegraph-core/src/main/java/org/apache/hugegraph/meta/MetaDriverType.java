package org.apache.hugegraph.meta;

import org.apache.hugegraph.type.define.SerialEnum;

public enum MetaDriverType implements SerialEnum {

    // etcd meta driver
    ETCD(1, "etcd"),

    // pd meta driver
    PD(2, "pd");

    private final byte code;
    private final String name;

    static {
        SerialEnum.register(MetaDriverType.class);
    }

    MetaDriverType(int code, String name) {
        assert code < 256;
        this.code = (byte) code;
        this.name = name;
    }

    public byte code() {
        return this.code;
    }

    public String string() {
        return this.name;
    }

    public static MetaDriverType fromCode(byte code) {
        switch (code) {
            case 1:
                return ETCD;
            case 2:
                return PD;
            default:
                throw new AssertionError(
                    "Unsupported meta driver code: " + code);
        }
    }
}
