package net.ooder.server.httpproxy.core;

import java.security.SecureRandom;
import java.util.StringTokenizer;

public class UUID implements Comparable {

    private static SecureRandom randomGenerator = new SecureRandom();

    private long high;
    private long low;

    public UUID(long high, long low) {
        this.high = high;
        this.low = low;
    }

    public static UUID createUUID() {
        byte[] uuid = new byte[16];
        randomGenerator.nextBytes(uuid);
        uuid[6] &= 0xf;
        uuid[6] |= 0x40;
        uuid[8] &= 0x3f;
        uuid[8] |= 0x80;
        uuid[10] |= 0x80;
        return new UUID( copyToLong( uuid, 0 ), copyToLong( uuid, 8 ) );
    }

    public int compareTo(Object o) {
        UUID uuid = (UUID) o;

        if( this.high < uuid.high ) {
            return -1;
        } else if( this.high == uuid.high ) {
            if( this.low < uuid.low ) {
                return -1;
            } else if( this.low == uuid.low ) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return 1;
        }
    }

    private static long copyToLong(byte[] uuid, int start) {
        long num = 0L;
        for(int i = start; i < (start + 8); i++)
            num = num << 8 | (long)(uuid[i] & 0xff);
        return num;
    }

    public int hashCode() {
        return (int)(high >> 32 ^ high ^ low >> 32 ^ low);
    }

    public boolean equals(Object obj) {
        if( obj == this ) return true;

        if( obj instanceof UUID ) {
            UUID uuid = (UUID) obj;

            return (uuid.high == this.high) && (uuid.low == this.low);
        }

        return false;
    }

    private static String digits(long num, int i) {
        long shifted = 1L << i * 4;
        return Long.toHexString(shifted | num & shifted - 1L).substring(1);
    }

    public String toString() {
        return digits(high >> 32, 8) + "-" + digits(high >> 16, 4) + "-" + digits(high, 4) + "-" + digits(low >> 48, 4) + "-" + digits(low, 12);
    }

    public static UUID parse( String uuidStr ) {
        StringTokenizer token = new StringTokenizer( uuidStr, "-" );
        long upper = Long.parseLong( token.nextToken(), 16 ) << 32;
        upper = upper | (Long.parseLong( token.nextToken(), 16 ) << 16);
        upper = upper | (Long.parseLong( token.nextToken(), 16 ) );

        long lower = Long.parseLong( token.nextToken(), 16 ) << 48;
        lower = lower | Long.parseLong( token.nextToken(), 16 );

        return new UUID( upper, lower );
    }

    public static void main(String[] args) {
        UUID uuid = UUID.createUUID();
        System.out.println("UUID: (" + uuid + ")" );
        System.out.println("UUID: (" + UUID.parse( uuid.toString() ) + ")");

        UUID uuid2 = UUID.createUUID();
        System.out.println("UUID: (" + uuid2 + ")" );
        System.out.println("UUID: (" + UUID.parse( uuid2.toString() ) + ")");

        UUID uuid3 = UUID.createUUID();
        System.out.println("UUID: (" + uuid3 + ")" );
        System.out.println("UUID: (" + UUID.parse( uuid3.toString() ) + ")");
    }
}
