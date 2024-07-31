package edu.caltech.test.nanodb.storage.heapfile;

import org.testng.annotations.Test;

import static edu.caltech.nanodb.storage.heapfile.FreeSpacePage.firstSetBitPos;
import static org.testng.Assert.assertEquals;

@Test
public class TestFreeSpacePage {
    public void testFirstSetBitPosTypicalPageSizeFirstByte() {
        assert firstSetBitPos(new byte[]{0, 0, 0, 0}) == -1;
        assert firstSetBitPos(new byte[]{1, 0, 0, 0}) == 0;
        assert firstSetBitPos(new byte[]{2, 0, 0, 0}) == 1;
        assert firstSetBitPos(new byte[]{3, 0, 0, 0}) == 0;
        assert firstSetBitPos(new byte[]{4, 0, 0, 0}) == 2;
        assert firstSetBitPos(new byte[]{5, 0, 0, 0}) == 0;
        assert firstSetBitPos(new byte[]{6, 0, 0, 0}) == 1;
        assert firstSetBitPos(new byte[]{7, 0, 0, 0}) == 0;
        assert firstSetBitPos(new byte[]{8, 0, 0, 0}) == 3;

        assert firstSetBitPos(new byte[]{11, 0, 0, 0}) == 0;
        assert firstSetBitPos(new byte[]{24, 0, 0, 0}) == 3;
        assert firstSetBitPos(new byte[]{32, 0, 0, 0}) == 5;
        assert firstSetBitPos(new byte[]{42, 0, 0, 0}) == 1;
        assert firstSetBitPos(new byte[]{54, 0, 0, 0}) == 1;
    }

    public void testFirstSetBitPosTypicalPageSizeMultiBytes() {
        assertEquals(firstSetBitPos(new byte[]{0, 1, 0, 0}), 8);
        assertEquals(firstSetBitPos(new byte[]{0, 4, 0, 0}), 10);
        assertEquals(firstSetBitPos(new byte[]{0, 8, 0, 0}), 11);
        assertEquals(firstSetBitPos(new byte[]{0, 11, 0, 0}), 8);
        assertEquals(firstSetBitPos(new byte[]{0, 24, 0, 0}), 11);
        assertEquals(firstSetBitPos(new byte[]{0, 32, 0, 0}), 13);

        assertEquals(firstSetBitPos(new byte[]{11, 11, 0, 0}), 0);
        assertEquals(firstSetBitPos(new byte[]{24, 24, 0, 0}), 3);
        assertEquals(firstSetBitPos(new byte[]{32, 32, 0, 0}), 5);

        assertEquals(firstSetBitPos(new byte[]{0, 0, 11, 0}), 16);
        assertEquals(firstSetBitPos(new byte[]{0, 0, 24, 0}), 19);
        assertEquals(firstSetBitPos(new byte[]{0, 0, 32, 0}), 21);
    }

    public void testFirstSetBitPosOddPageSize() {
        assert firstSetBitPos(new byte[]{0}) == -1;
        assert firstSetBitPos(new byte[]{1, 0}) == 0;
        assert firstSetBitPos(new byte[]{2, 0, 0}) == 1;
        assert firstSetBitPos(new byte[]{3, 0, 0, 0, 0}) == 0;
        assert firstSetBitPos(new byte[]{4, 0, 0, 0, 0, 0}) == 2;

        assert firstSetBitPos(new byte[]{0, 0, 0, 0, 11}) == 32;
        assert firstSetBitPos(new byte[]{0, 0, 0, 0, 0, 24}) == 43;
        assert firstSetBitPos(new byte[]{0, 0, 0, 0, 0, 0, 32}) == 53;
    }
}
