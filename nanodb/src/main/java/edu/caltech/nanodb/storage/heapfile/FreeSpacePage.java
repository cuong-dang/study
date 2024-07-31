package edu.caltech.nanodb.storage.heapfile;

import edu.caltech.nanodb.storage.DBPage;

import java.util.Arrays;

public class FreeSpacePage {
    public static void initNewPage(DBPage dbPage) {
        byte[] b = new byte[dbPage.getPageSize()];
        Arrays.fill(b, (byte) 0xff);
        dbPage.write(0, b);
    }

    public static int firstFreePage(DBPage dbPage) {
        int firstSetBit = firstSetBitPos(dbPage);
        if (firstSetBit == -1) {
            return -1;
        }
        return (dbPage.getPageNo() - 1) * (dbPage.getPageSize() * 8) + firstSetBit;
    }

    private static int firstSetBitPos(DBPage dbPage) {
        byte[] bytes = new byte[dbPage.getPageSize()];
        dbPage.read(0, bytes);
        return firstSetBitPos(bytes);
    }

    public static int firstSetBitPos(byte[] bytes) {
        int i = 0;
        int offset = 0;
        for (; i < bytes.length; i += 4, offset += 32) {
            int v = bytes[i];
            if (i + 1 < bytes.length) v |= bytes[i + 1] << 8;
            if (i + 2 < bytes.length) v |= bytes[i + 2] << 16;
            if (i + 3 < bytes.length) v |= bytes[i + 3] << 24;
            int r = Integer.numberOfTrailingZeros(v);
            if (r < 32) {
                return r + offset;
            }
        }
        return -1;
    }
}
