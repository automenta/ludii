/*
 * Decompiled with CFR 0.150.
 */
package collections;

import math.BitTwiddling;
import main.Utilities;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Arrays;

public final class ChunkSet
implements Cloneable,
Serializable {
    private static final int ADDRESS_BITS_PER_WORD = 6;
    private static final int BITS_PER_WORD = 64;
    private long[] words;
    private transient int wordsInUse = 0;
    private transient boolean sizeIsSticky = false;
    private static final long serialVersionUID = 1L;
    private static final long WORD_MASK = -1L;
    static final long MASK_NOT_1 = -6148914691236517206L;
    static final long MASK_NOT_2 = -3689348814741910324L;
    static final long MASK_NOT_4 = -1085102592571150096L;
    static final long MASK_NOT_8 = -71777214294589696L;
    static final long MASK_NOT_16 = -281470681808896L;
    static final long MASK_NOT_32 = -4294967296L;
    protected final int chunkSize;
    protected final long chunkMask;
    static final long[] bitNMasks = new long[64];
    private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("bits", long[].class)};

    public ChunkSet() {
        for (int n = 0; n < 64; ++n) {
            ChunkSet.bitNMasks[n] = (1L << n) - 1L;
        }
        this.initWords(64);
        this.sizeIsSticky = false;
        this.chunkSize = 1;
        this.chunkMask = (1L << this.chunkSize) - 1L;
    }

    public ChunkSet(int chunkSize, int numChunks) {
        int nbits;
        for (int n = 0; n < 64; ++n) {
            ChunkSet.bitNMasks[n] = (1L << n) - 1L;
        }
        this.chunkSize = chunkSize;
        this.chunkMask = (1L << chunkSize) - 1L;
        if (!BitTwiddling.isPowerOf2(chunkSize)) {
            System.out.println("** BitSetS: chunkSize " + chunkSize + " is not a power of 2.");
            Utilities.stackTrace();
        }
        if ((nbits = chunkSize * numChunks) < 0) {
            throw new NegativeArraySizeException("nbits < 0: " + nbits);
        }
        this.initWords(nbits);
        this.sizeIsSticky = true;
    }

    private static int wordIndex(int bitIndex) {
        return bitIndex >> 6;
    }

    private void checkInvariants() {
        assert (this.wordsInUse == 0 || this.words[this.wordsInUse - 1] != 0L);
        assert (this.wordsInUse >= 0 && this.wordsInUse <= this.words.length);
        assert (this.wordsInUse == this.words.length || this.words[this.wordsInUse] == 0L);
    }

    private void recalculateWordsInUse() {
        int i;
        for (i = this.wordsInUse - 1; i >= 0 && this.words[i] == 0L; --i) {
        }
        this.wordsInUse = i + 1;
    }

    private void initWords(int nbits) {
        this.words = new long[ChunkSet.wordIndex(nbits - 1) + 1];
    }

    private void ensureCapacity(int wordsRequired) {
        if (this.words.length < wordsRequired) {
            int request = Math.max(2 * this.words.length, wordsRequired);
            this.words = Arrays.copyOf(this.words, request);
            this.sizeIsSticky = false;
        }
    }

    private void expandTo(int wordIndex) {
        int wordsRequired = wordIndex + 1;
        if (this.wordsInUse < wordsRequired) {
            this.ensureCapacity(wordsRequired);
            this.wordsInUse = wordsRequired;
        }
    }

    private static void checkRange(int fromIndex, int toIndex) {
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException("fromIndex < 0: " + fromIndex);
        }
        if (toIndex < 0) {
            throw new IndexOutOfBoundsException("toIndex < 0: " + toIndex);
        }
        if (fromIndex > toIndex) {
            throw new IndexOutOfBoundsException("fromIndex: " + fromIndex + " > toIndex: " + toIndex);
        }
    }

    public void flip(int bitIndex) {
        if (bitIndex < 0) {
            throw new IndexOutOfBoundsException("bitIndex < 0: " + bitIndex);
        }
        int wordIndex = ChunkSet.wordIndex(bitIndex);
        this.expandTo(wordIndex);
        int n = wordIndex;
        this.words[n] = this.words[n] ^ 1L << bitIndex;
        this.recalculateWordsInUse();
        this.checkInvariants();
    }

    public void flip(int fromIndex, int toIndex) {
        ChunkSet.checkRange(fromIndex, toIndex);
        if (fromIndex == toIndex) {
            return;
        }
        int startWordIndex = ChunkSet.wordIndex(fromIndex);
        int endWordIndex = ChunkSet.wordIndex(toIndex - 1);
        this.expandTo(endWordIndex);
        long firstWordMask = -1L << fromIndex;
        long lastWordMask = -1L >>> -toIndex;
        if (startWordIndex == endWordIndex) {
            int n = startWordIndex;
            this.words[n] = this.words[n] ^ firstWordMask & lastWordMask;
        } else {
            int n = startWordIndex;
            this.words[n] = this.words[n] ^ firstWordMask;
            int i = startWordIndex + 1;
            while (i < endWordIndex) {
                int n2 = i++;
                this.words[n2] = this.words[n2] ^ 0xFFFFFFFFFFFFFFFFL;
            }
            int n3 = endWordIndex;
            this.words[n3] = this.words[n3] ^ lastWordMask;
        }
        this.recalculateWordsInUse();
        this.checkInvariants();
    }

    public void set(int bitIndex) {
        if (bitIndex < 0) {
            throw new IndexOutOfBoundsException("bitIndex < 0: " + bitIndex);
        }
        int wordIndex = ChunkSet.wordIndex(bitIndex);
        this.expandTo(wordIndex);
        int n = wordIndex;
        this.words[n] = this.words[n] | 1L << bitIndex;
        this.checkInvariants();
    }

    public void set(int bitIndex, boolean value) {
        if (value) {
            this.set(bitIndex);
        } else {
            this.clear(bitIndex);
        }
    }

    public void set(int fromIndex, int toIndex) {
        ChunkSet.checkRange(fromIndex, toIndex);
        if (fromIndex == toIndex) {
            return;
        }
        int startWordIndex = ChunkSet.wordIndex(fromIndex);
        int endWordIndex = ChunkSet.wordIndex(toIndex - 1);
        this.expandTo(endWordIndex);
        long firstWordMask = -1L << fromIndex;
        long lastWordMask = -1L >>> -toIndex;
        if (startWordIndex == endWordIndex) {
            int n = startWordIndex;
            this.words[n] = this.words[n] | firstWordMask & lastWordMask;
        } else {
            int n = startWordIndex;
            this.words[n] = this.words[n] | firstWordMask;
            for (int i = startWordIndex + 1; i < endWordIndex; ++i) {
                this.words[i] = -1L;
            }
            int n2 = endWordIndex;
            this.words[n2] = this.words[n2] | lastWordMask;
        }
        this.checkInvariants();
    }

    public void set(int fromIndex, int toIndex, boolean value) {
        if (value) {
            this.set(fromIndex, toIndex);
        } else {
            this.clear(fromIndex, toIndex);
        }
    }

    public void clear(int bitIndex) {
        if (bitIndex < 0) {
            throw new IndexOutOfBoundsException("bitIndex < 0: " + bitIndex);
        }
        int wordIndex = ChunkSet.wordIndex(bitIndex);
        if (wordIndex >= this.wordsInUse) {
            return;
        }
        int n = wordIndex;
        this.words[n] = this.words[n] & (1L << bitIndex ^ 0xFFFFFFFFFFFFFFFFL);
        this.recalculateWordsInUse();
        this.checkInvariants();
    }

    public void clear(int fromIndex, int toIndex) {
        ChunkSet.checkRange(fromIndex, toIndex);
        if (fromIndex == toIndex) {
            return;
        }
        int startWordIndex = ChunkSet.wordIndex(fromIndex);
        if (startWordIndex >= this.wordsInUse) {
            return;
        }
        int to = toIndex;
        int endWordIndex = ChunkSet.wordIndex(to - 1);
        if (endWordIndex >= this.wordsInUse) {
            to = this.length();
            endWordIndex = this.wordsInUse - 1;
        }
        long firstWordMask = -1L << fromIndex;
        long lastWordMask = -1L >>> -to;
        if (startWordIndex == endWordIndex) {
            int n = startWordIndex;
            this.words[n] = this.words[n] & (firstWordMask & lastWordMask ^ 0xFFFFFFFFFFFFFFFFL);
        } else {
            int n = startWordIndex;
            this.words[n] = this.words[n] & (firstWordMask ^ 0xFFFFFFFFFFFFFFFFL);
            for (int i = startWordIndex + 1; i < endWordIndex; ++i) {
                this.words[i] = 0L;
            }
            int n2 = endWordIndex;
            this.words[n2] = this.words[n2] & (lastWordMask ^ 0xFFFFFFFFFFFFFFFFL);
        }
        this.recalculateWordsInUse();
        this.checkInvariants();
    }

    public void clear() {
        while (this.wordsInUse > 0) {
            this.words[--this.wordsInUse] = 0L;
        }
    }

    public boolean get(int bitIndex) {
        if (bitIndex < 0) {
            throw new IndexOutOfBoundsException("bitIndex < 0: " + bitIndex);
        }
        this.checkInvariants();
        int wordIndex = ChunkSet.wordIndex(bitIndex);
        return wordIndex < this.wordsInUse && (this.words[wordIndex] & 1L << bitIndex) != 0L;
    }

    public int nextSetBit(int fromIndex) {
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException("fromIndex < 0: " + fromIndex);
        }
        this.checkInvariants();
        int u = ChunkSet.wordIndex(fromIndex);
        if (u >= this.wordsInUse) {
            return -1;
        }
        long word = this.words[u] & -1L << fromIndex;
        while (word == 0L) {
            if (++u == this.wordsInUse) {
                return -1;
            }
            word = this.words[u];
        }
        return u * 64 + Long.numberOfTrailingZeros(word);
    }

    public int nextClearBit(int fromIndex) {
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException("fromIndex < 0: " + fromIndex);
        }
        this.checkInvariants();
        int u = ChunkSet.wordIndex(fromIndex);
        if (u >= this.wordsInUse) {
            return fromIndex;
        }
        long word = (this.words[u] ^ 0xFFFFFFFFFFFFFFFFL) & -1L << fromIndex;
        while (word == 0L) {
            if (++u == this.wordsInUse) {
                return this.wordsInUse * 64;
            }
            word = this.words[u] ^ 0xFFFFFFFFFFFFFFFFL;
        }
        return u * 64 + Long.numberOfTrailingZeros(word);
    }

    public int length() {
        if (this.wordsInUse == 0) {
            return 0;
        }
        return 64 * (this.wordsInUse - 1) + (64 - Long.numberOfLeadingZeros(this.words[this.wordsInUse - 1]));
    }

    public boolean isEmpty() {
        return this.wordsInUse == 0;
    }

    public boolean intersects(ChunkSet set) {
        for (int i = Math.min(this.wordsInUse, set.wordsInUse) - 1; i >= 0; --i) {
            if ((this.words[i] & set.words[i]) == 0L) continue;
            return true;
        }
        return false;
    }

    public int cardinality() {
        int sum = 0;
        for (int i = 0; i < this.wordsInUse; ++i) {
            sum += Long.bitCount(this.words[i]);
        }
        return sum;
    }

    public void and(ChunkSet set) {
        if (this == set) {
            return;
        }
        while (this.wordsInUse > set.wordsInUse) {
            this.words[--this.wordsInUse] = 0L;
        }
        for (int i = 0; i < this.wordsInUse; ++i) {
            int n = i;
            this.words[n] = this.words[n] & set.words[i];
        }
        this.recalculateWordsInUse();
        this.checkInvariants();
    }

    public void or(ChunkSet set) {
        if (this == set) {
            return;
        }
        int wordsInCommon = Math.min(this.wordsInUse, set.wordsInUse);
        if (this.wordsInUse < set.wordsInUse) {
            this.ensureCapacity(set.wordsInUse);
            this.wordsInUse = set.wordsInUse;
        }
        for (int i = 0; i < wordsInCommon; ++i) {
            int n = i;
            this.words[n] = this.words[n] | set.words[i];
        }
        if (wordsInCommon < set.wordsInUse) {
            System.arraycopy(set.words, wordsInCommon, this.words, wordsInCommon, this.wordsInUse - wordsInCommon);
        }
        this.checkInvariants();
    }

    public void xor(ChunkSet set) {
        int wordsInCommon = Math.min(this.wordsInUse, set.wordsInUse);
        if (this.wordsInUse < set.wordsInUse) {
            this.ensureCapacity(set.wordsInUse);
            this.wordsInUse = set.wordsInUse;
        }
        for (int i = 0; i < wordsInCommon; ++i) {
            int n = i;
            this.words[n] = this.words[n] ^ set.words[i];
        }
        if (wordsInCommon < set.wordsInUse) {
            System.arraycopy(set.words, wordsInCommon, this.words, wordsInCommon, set.wordsInUse - wordsInCommon);
        }
        this.recalculateWordsInUse();
        this.checkInvariants();
    }

    public void andNot(ChunkSet set) {
        for (int i = Math.min(this.wordsInUse, set.wordsInUse) - 1; i >= 0; --i) {
            int n = i;
            this.words[n] = this.words[n] & (set.words[i] ^ 0xFFFFFFFFFFFFFFFFL);
        }
        this.recalculateWordsInUse();
        this.checkInvariants();
    }

    public int hashCode() {
        long h = 1234L;
        int i = this.wordsInUse;
        while (--i >= 0) {
            h ^= this.words[i] * (i + 1);
        }
        return (int)(h >> 32 ^ h);
    }

    public int size() {
        return this.words.length * 64;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ChunkSet)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        ChunkSet set = (ChunkSet)obj;
        this.checkInvariants();
        set.checkInvariants();
        if (this.wordsInUse != set.wordsInUse) {
            return false;
        }
        for (int i = 0; i < this.wordsInUse; ++i) {
            if (this.words[i] == set.words[i]) continue;
            return false;
        }
        return true;
    }

    public ChunkSet clone() {
        if (!this.sizeIsSticky) {
            this.trimToSize();
        }
        try {
            ChunkSet result = (ChunkSet)super.clone();
            result.words = this.words.clone();
            result.checkInvariants();
            return result;
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    private void trimToSize() {
        if (this.wordsInUse != this.words.length) {
            this.words = Arrays.copyOf(this.words, this.wordsInUse);
            this.checkInvariants();
        }
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        this.checkInvariants();
        if (!this.sizeIsSticky) {
            this.trimToSize();
        }
        s.writeObject(this.words);
        s.writeInt(this.chunkSize);
        s.writeLong(this.chunkMask);
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        this.words = (long[])s.readObject();
        int newChunkSize = s.readInt();
        long newChunkMask = s.readLong();
        try {
            Field chunkSizeField = this.getClass().getDeclaredField("chunkSize");
            chunkSizeField.setAccessible(true);
            chunkSizeField.set(this, newChunkSize);
            Field chunkMaskField = this.getClass().getDeclaredField("chunkMask");
            chunkMaskField.setAccessible(true);
            chunkMaskField.set(this, newChunkMask);
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        this.wordsInUse = this.words.length;
        this.recalculateWordsInUse();
        this.sizeIsSticky = this.words.length > 0 && this.words[this.words.length - 1] == 0L;
        this.checkInvariants();
    }

    public String toString() {
        this.checkInvariants();
        int numBits = this.wordsInUse > 128 ? this.cardinality() : this.wordsInUse * 64;
        StringBuilder b = new StringBuilder(6 * numBits + 2);
        b.append('{');
        int i = this.nextSetBit(0);
        if (i != -1) {
            b.append(i);
            i = this.nextSetBit(i + 1);
            while (i >= 0) {
                int endOfRun = this.nextClearBit(i);
                do {
                    b.append(", ").append(i);
                } while (++i < endOfRun);
                i = this.nextSetBit(i + 1);
            }
        }
        b.append('}');
        return b.toString();
    }

    public int chunkSize() {
        return this.chunkSize;
    }

    public int numChunks() {
        return this.chunkSize == 0 ? 0 : this.size() / this.chunkSize;
    }

    public int numNonZeroChunks() {
        int count = 0;
        int numChunks = this.numChunks();
        for (int i = 0; i < numChunks; ++i) {
            if (this.getChunk(i) == 0) continue;
            ++count;
        }
        return count;
    }

    public int getChunk(int chunk) {
        int bitIndex = chunk * this.chunkSize;
        int wordIndex = bitIndex >> 6;
        int down = bitIndex & 0x3F;
        if (wordIndex >= this.words.length) {
            return 0;
        }
        return (int)(this.words[wordIndex] >>> down & this.chunkMask);
    }

    public void setChunk(int chunk, int value) {
        if (value < 0 || value >= 1 << this.chunkSize) {
            throw new IllegalArgumentException("Chunk value " + value + " is out of range for size = " + this.chunkSize);
        }
        if (this.chunkSize == 0) {
            return;
        }
        int bitIndex = chunk * this.chunkSize;
        int wordIndex = bitIndex >> 6;
        this.expandTo(wordIndex);
        int up = bitIndex & 0x3F;
        int n = wordIndex;
        this.words[n] = this.words[n] & (this.chunkMask << up ^ 0xFFFFFFFFFFFFFFFFL);
        int n2 = wordIndex;
        this.words[n2] = this.words[n2] | (value & this.chunkMask) << up;
        this.recalculateWordsInUse();
        this.checkInvariants();
    }

    public void clearChunk(int chunk) {
        if (this.chunkSize == 0) {
            return;
        }
        int bitIndex = chunk * this.chunkSize;
        int wordIndex = bitIndex >> 6;
        if (wordIndex > this.wordsInUse) {
            return;
        }
        int up = bitIndex & 0x3F;
        int n = wordIndex;
        this.words[n] = this.words[n] & (this.chunkMask << up ^ 0xFFFFFFFFFFFFFFFFL);
        this.recalculateWordsInUse();
        this.checkInvariants();
    }

    public void clearNoResize() {
        for (int w = 0; w < this.wordsInUse; ++w) {
            this.words[w] = 0L;
        }
        this.wordsInUse = 0;
        this.checkInvariants();
    }

    public int getBit(int chunk, int bit) {
        int bitIndex = chunk * this.chunkSize;
        int wordIndex = bitIndex >> 6;
        int down = bitIndex & 0x3F;
        return (int)(this.words[wordIndex] >>> down + bit & 1L);
    }

    public void setBit(int chunk, int bit, boolean value) {
        if (this.chunkSize == 0) {
            return;
        }
        int bitIndex = chunk * this.chunkSize;
        int wordIndex = bitIndex >> 6;
        this.expandTo(wordIndex);
        int up = bitIndex & 0x3F;
        long bitMask = 1L << up + bit;
        if (value) {
            int n = wordIndex;
            this.words[n] = this.words[n] | bitMask;
        } else {
            int n = wordIndex;
            this.words[n] = this.words[n] & (bitMask ^ 0xFFFFFFFFFFFFFFFFL);
        }
        this.recalculateWordsInUse();
        this.checkInvariants();
    }

    public void toggleBit(int chunk, int bit) {
        if (this.chunkSize == 0) {
            return;
        }
        int bitIndex = chunk * this.chunkSize;
        int wordIndex = bitIndex >> 6;
        this.expandTo(wordIndex);
        int up = bitIndex & 0x3F;
        long bitMask = 1L << up + bit;
        int n = wordIndex;
        this.words[n] = this.words[n] ^ bitMask;
        this.recalculateWordsInUse();
        this.checkInvariants();
    }

    public void setNBits(int chunk, int numBits, boolean value) {
        if (this.chunkSize == 0) {
            return;
        }
        int bitIndex = chunk * this.chunkSize;
        int wordIndex = bitIndex >> 6;
        this.expandTo(wordIndex);
        int up = bitIndex & 0x3F;
        long bitsNMask = bitNMasks[numBits] << up;
        if (value) {
            int n = wordIndex;
            this.words[n] = this.words[n] | bitsNMask;
        } else {
            int n = wordIndex;
            this.words[n] = this.words[n] & (bitsNMask ^ 0xFFFFFFFFFFFFFFFFL);
        }
        this.recalculateWordsInUse();
        this.checkInvariants();
    }

    public void resolveToBit(int chunk, int bit) {
        if (this.chunkSize == 0) {
            return;
        }
        int bitIndex = chunk * this.chunkSize;
        int wordIndex = bitIndex >> 6;
        this.expandTo(wordIndex);
        int up = bitIndex & 0x3F;
        long bitMask = 1L << up + bit;
        int n = wordIndex;
        this.words[n] = this.words[n] & (this.chunkMask << up ^ 0xFFFFFFFFFFFFFFFFL);
        int n2 = wordIndex;
        this.words[n2] = this.words[n2] | bitMask;
        this.recalculateWordsInUse();
        this.checkInvariants();
    }

    public int numBitsOn(int chunk) {
        int bitIndex = chunk * this.chunkSize;
        int wordIndex = bitIndex >> 6;
        int down = bitIndex & 0x3F;
        int value = (int)(this.words[wordIndex] >>> down & this.chunkMask);
        int numBits = 0;
        for (int b = 0; b < this.chunkSize; ++b) {
            if ((1 << b & value) == 0) continue;
            ++numBits;
        }
        return numBits;
    }

    public boolean isResolved(int chunk) {
        return this.numBitsOn(chunk) == 1;
    }

    public int resolvedTo(int chunk) {
        int bitIndex = chunk * this.chunkSize;
        int wordIndex = bitIndex >> 6;
        int down = bitIndex & 0x3F;
        int value = (int)(this.words[wordIndex] >>> down & this.chunkMask);
        int result = -1;
        int numBits = 0;
        for (int b = 0; b < this.chunkSize; ++b) {
            if ((1 << b & value) == 0) continue;
            result = b;
            ++numBits;
        }
        return numBits == 1 ? result : 0;
    }

    public void shiftL(int numBits, boolean expand) {
        int size;
        if (numBits == 0) {
            return;
        }
        if (expand) {
            int maxIndex = ChunkSet.wordIndex(this.length() + numBits);
            this.expandTo(maxIndex);
        }
        int remnant = 64 - numBits;
        long carry = 0L;
        for (int idx = 0; idx < this.wordsInUse; ++idx) {
            long temp = this.words[idx] >>> remnant;
            this.words[idx] = this.words[idx] << numBits | carry;
            carry = temp;
        }
        if (!expand && ((size = this.size()) & 0x3F) > 0) {
            long mask = (1L << (size & 0x3F)) - 1L;
            int n = this.wordsInUse - 1;
            this.words[n] = this.words[n] & mask;
        }
        this.recalculateWordsInUse();
        this.checkInvariants();
    }

    public void shiftR(int numBits) {
        if (numBits == 0) {
            return;
        }
        int remnant = 64 - numBits;
        long carry = 0L;
        for (int idx = this.wordsInUse - 1; idx >= 0; --idx) {
            long temp = this.words[idx] << remnant;
            this.words[idx] = this.words[idx] >>> numBits | carry;
            carry = temp;
        }
        this.recalculateWordsInUse();
        this.checkInvariants();
    }

    public boolean matches(ChunkSet mask, ChunkSet pattern) {
        int maskWordsInUse = mask.wordsInUse;
        if (this.wordsInUse < maskWordsInUse) {
            return false;
        }
        for (int n = 0; n < maskWordsInUse; ++n) {
            if ((this.words[n] & mask.words[n]) == pattern.words[n]) continue;
            return false;
        }
        return true;
    }

    public boolean violatesNot(ChunkSet mask, ChunkSet pattern) {
        return this.violatesNot(mask, pattern, 0);
    }

    public boolean violatesNot(ChunkSet mask, ChunkSet pattern, int startWord) {
        int wordsToCheck = Math.min(this.wordsInUse, mask.wordsInUse);
        for (int n = startWord; n < wordsToCheck; ++n) {
            long temp = (this.words[n] ^ pattern.words[n] ^ 0xFFFFFFFFFFFFFFFFL) & mask.words[n];
            if (this.chunkSize > 1) {
                temp = (temp & 0xAAAAAAAAAAAAAAAAL) >>> 1 & temp;
                if (this.chunkSize > 2) {
                    temp = (temp & 0xCCCCCCCCCCCCCCCCL) >>> 2 & temp;
                    if (this.chunkSize > 4) {
                        temp = (temp & 0xF0F0F0F0F0F0F0F0L) >>> 4 & temp;
                        if (this.chunkSize > 8) {
                            temp = (temp & 0xFF00FF00FF00FF00L) >>> 8 & temp;
                            if (this.chunkSize > 16) {
                                temp = (temp & 0xFFFF0000FFFF0000L) >>> 16 & temp;
                                if (this.chunkSize > 32) {
                                    temp = (temp & 0xFFFFFFFF00000000L) >>> 32 & temp;
                                }
                            }
                        }
                    }
                }
            }
            if (temp == 0L) continue;
            return true;
        }
        return false;
    }

    public String toChunkString() {
        this.checkInvariants();
        int numBits = this.wordsInUse > 128 ? this.cardinality() : this.wordsInUse * 64;
        StringBuilder b = new StringBuilder(6 * numBits + 2);
        b.append('{');
        for (int i = 0; i < this.numChunks(); ++i) {
            int value = this.getChunk(i);
            if (value == 0) continue;
            if (b.toString().length() > 1) {
                b.append(", ");
            }
            b.append("chunk " + i + " = " + value);
        }
        b.append('}');
        return b.toString();
    }
}

