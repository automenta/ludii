// 
// Decompiled by Procyon v0.5.36
// 

package main.collections;

import main.Utilities;
import main.math.BitTwiddling;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Arrays;

public final class ChunkSet implements Cloneable, Serializable
{
    private static final int ADDRESS_BITS_PER_WORD = 6;
    private static final int BITS_PER_WORD = 64;
    private long[] words;
    private transient int wordsInUse;
    private transient boolean sizeIsSticky;
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
    static final long[] bitNMasks;
    private static final ObjectStreamField[] serialPersistentFields;
    
    public ChunkSet() {
        this.wordsInUse = 0;
        this.sizeIsSticky = false;
        for (int n = 0; n < 64; ++n) {
            ChunkSet.bitNMasks[n] = (1L << n) - 1L;
        }
        this.initWords(64);
        this.sizeIsSticky = false;
        this.chunkSize = 1;
        this.chunkMask = (1L << this.chunkSize) - 1L;
    }
    
    public ChunkSet(final int chunkSize, final int numChunks) {
        this.wordsInUse = 0;
        this.sizeIsSticky = false;
        for (int n = 0; n < 64; ++n) {
            ChunkSet.bitNMasks[n] = (1L << n) - 1L;
        }
        this.chunkSize = chunkSize;
        this.chunkMask = (1L << chunkSize) - 1L;
        if (!BitTwiddling.isPowerOf2(chunkSize)) {
            System.out.println("** BitSetS: chunkSize " + chunkSize + " is not a power of 2.");
            Utilities.stackTrace();
        }
        final int nbits = chunkSize * numChunks;
        if (nbits < 0) {
            throw new NegativeArraySizeException("nbits < 0: " + nbits);
        }
        this.initWords(nbits);
        this.sizeIsSticky = true;
    }
    
    private static int wordIndex(final int bitIndex) {
        return bitIndex >> 6;
    }
    
    private void checkInvariants() {
        assert this.words[this.wordsInUse - 1] != 0L;
        assert this.wordsInUse >= 0 && this.wordsInUse <= this.words.length;
        assert this.words[this.wordsInUse] == 0L;
    }
    
    private void recalculateWordsInUse() {
        int i;
        for (i = this.wordsInUse - 1; i >= 0 && this.words[i] == 0L; --i) {}
        this.wordsInUse = i + 1;
    }
    
    private void initWords(final int nbits) {
        this.words = new long[wordIndex(nbits - 1) + 1];
    }
    
    private void ensureCapacity(final int wordsRequired) {
        if (this.words.length < wordsRequired) {
            final int request = Math.max(2 * this.words.length, wordsRequired);
            this.words = Arrays.copyOf(this.words, request);
            this.sizeIsSticky = false;
        }
    }
    
    private void expandTo(final int wordIndex) {
        final int wordsRequired = wordIndex + 1;
        if (this.wordsInUse < wordsRequired) {
            this.ensureCapacity(wordsRequired);
            this.wordsInUse = wordsRequired;
        }
    }
    
    private static void checkRange(final int fromIndex, final int toIndex) {
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
    
    public void flip(final int bitIndex) {
        if (bitIndex < 0) {
            throw new IndexOutOfBoundsException("bitIndex < 0: " + bitIndex);
        }
        final int wordIndex = wordIndex(bitIndex);
        this.expandTo(wordIndex);
        final long[] words = this.words;
        final int n = wordIndex;
        words[n] ^= 1L << bitIndex;
        this.recalculateWordsInUse();
        this.checkInvariants();
    }
    
    public void flip(final int fromIndex, final int toIndex) {
        checkRange(fromIndex, toIndex);
        if (fromIndex == toIndex) {
            return;
        }
        final int startWordIndex = wordIndex(fromIndex);
        final int endWordIndex = wordIndex(toIndex - 1);
        this.expandTo(endWordIndex);
        final long firstWordMask = -1L << fromIndex;
        final long lastWordMask = -1L >>> -toIndex;
        if (startWordIndex == endWordIndex) {
            final long[] words = this.words;
            final int n = startWordIndex;
            words[n] ^= (firstWordMask & lastWordMask);
        }
        else {
            final long[] words2 = this.words;
            final int n2 = startWordIndex;
            words2[n2] ^= firstWordMask;
            for (int i = startWordIndex + 1; i < endWordIndex; ++i) {
                final long[] words3 = this.words;
                final int n3 = i;
                words3[n3] ^= -1L;
            }
            final long[] words4 = this.words;
            final int n4 = endWordIndex;
            words4[n4] ^= lastWordMask;
        }
        this.recalculateWordsInUse();
        this.checkInvariants();
    }
    
    public void set(final int bitIndex) {
        if (bitIndex < 0) {
            throw new IndexOutOfBoundsException("bitIndex < 0: " + bitIndex);
        }
        final int wordIndex = wordIndex(bitIndex);
        this.expandTo(wordIndex);
        final long[] words = this.words;
        final int n = wordIndex;
        words[n] |= 1L << bitIndex;
        this.checkInvariants();
    }
    
    public void set(final int bitIndex, final boolean value) {
        if (value) {
            this.set(bitIndex);
        }
        else {
            this.clear(bitIndex);
        }
    }
    
    public void set(final int fromIndex, final int toIndex) {
        checkRange(fromIndex, toIndex);
        if (fromIndex == toIndex) {
            return;
        }
        final int startWordIndex = wordIndex(fromIndex);
        final int endWordIndex = wordIndex(toIndex - 1);
        this.expandTo(endWordIndex);
        final long firstWordMask = -1L << fromIndex;
        final long lastWordMask = -1L >>> -toIndex;
        if (startWordIndex == endWordIndex) {
            final long[] words = this.words;
            final int n = startWordIndex;
            words[n] |= (firstWordMask & lastWordMask);
        }
        else {
            final long[] words2 = this.words;
            final int n2 = startWordIndex;
            words2[n2] |= firstWordMask;
            for (int i = startWordIndex + 1; i < endWordIndex; ++i) {
                this.words[i] = -1L;
            }
            final long[] words3 = this.words;
            final int n3 = endWordIndex;
            words3[n3] |= lastWordMask;
        }
        this.checkInvariants();
    }
    
    public void set(final int fromIndex, final int toIndex, final boolean value) {
        if (value) {
            this.set(fromIndex, toIndex);
        }
        else {
            this.clear(fromIndex, toIndex);
        }
    }
    
    public void clear(final int bitIndex) {
        if (bitIndex < 0) {
            throw new IndexOutOfBoundsException("bitIndex < 0: " + bitIndex);
        }
        final int wordIndex = wordIndex(bitIndex);
        if (wordIndex >= this.wordsInUse) {
            return;
        }
        final long[] words = this.words;
        final int n = wordIndex;
        words[n] &= ~(1L << bitIndex);
        this.recalculateWordsInUse();
        this.checkInvariants();
    }
    
    public void clear(final int fromIndex, final int toIndex) {
        checkRange(fromIndex, toIndex);
        if (fromIndex == toIndex) {
            return;
        }
        final int startWordIndex = wordIndex(fromIndex);
        if (startWordIndex >= this.wordsInUse) {
            return;
        }
        int to = toIndex;
        int endWordIndex = wordIndex(to - 1);
        if (endWordIndex >= this.wordsInUse) {
            to = this.length();
            endWordIndex = this.wordsInUse - 1;
        }
        final long firstWordMask = -1L << fromIndex;
        final long lastWordMask = -1L >>> -to;
        if (startWordIndex == endWordIndex) {
            final long[] words = this.words;
            final int n = startWordIndex;
            words[n] &= ~(firstWordMask & lastWordMask);
        }
        else {
            final long[] words2 = this.words;
            final int n2 = startWordIndex;
            words2[n2] &= ~firstWordMask;
            for (int i = startWordIndex + 1; i < endWordIndex; ++i) {
                this.words[i] = 0L;
            }
            final long[] words3 = this.words;
            final int n3 = endWordIndex;
            words3[n3] &= ~lastWordMask;
        }
        this.recalculateWordsInUse();
        this.checkInvariants();
    }
    
    public void clear() {
        while (this.wordsInUse > 0) {
            this.words[--this.wordsInUse] = 0L;
        }
    }
    
    public boolean get(final int bitIndex) {
        if (bitIndex < 0) {
            throw new IndexOutOfBoundsException("bitIndex < 0: " + bitIndex);
        }
        this.checkInvariants();
        final int wordIndex = wordIndex(bitIndex);
        return wordIndex < this.wordsInUse && (this.words[wordIndex] & 1L << bitIndex) != 0x0L;
    }
    
    public int nextSetBit(final int fromIndex) {
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException("fromIndex < 0: " + fromIndex);
        }
        this.checkInvariants();
        int u = wordIndex(fromIndex);
        if (u >= this.wordsInUse) {
            return -1;
        }
        long word;
        for (word = (this.words[u] & -1L << fromIndex); word == 0L; word = this.words[u]) {
            if (++u == this.wordsInUse) {
                return -1;
            }
        }
        return u * 64 + Long.numberOfTrailingZeros(word);
    }
    
    public int nextClearBit(final int fromIndex) {
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException("fromIndex < 0: " + fromIndex);
        }
        this.checkInvariants();
        int u = wordIndex(fromIndex);
        if (u >= this.wordsInUse) {
            return fromIndex;
        }
        long word;
        for (word = (~this.words[u] & -1L << fromIndex); word == 0L; word = ~this.words[u]) {
            if (++u == this.wordsInUse) {
                return this.wordsInUse * 64;
            }
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
    
    public boolean intersects(final ChunkSet set) {
        for (int i = Math.min(this.wordsInUse, set.wordsInUse) - 1; i >= 0; --i) {
            if ((this.words[i] & set.words[i]) != 0x0L) {
                return true;
            }
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
    
    public void and(final ChunkSet set) {
        if (this == set) {
            return;
        }
        while (this.wordsInUse > set.wordsInUse) {
            this.words[--this.wordsInUse] = 0L;
        }
        for (int i = 0; i < this.wordsInUse; ++i) {
            final long[] words = this.words;
            final int n = i;
            words[n] &= set.words[i];
        }
        this.recalculateWordsInUse();
        this.checkInvariants();
    }
    
    public void or(final ChunkSet set) {
        if (this == set) {
            return;
        }
        final int wordsInCommon = Math.min(this.wordsInUse, set.wordsInUse);
        if (this.wordsInUse < set.wordsInUse) {
            this.ensureCapacity(set.wordsInUse);
            this.wordsInUse = set.wordsInUse;
        }
        for (int i = 0; i < wordsInCommon; ++i) {
            final long[] words = this.words;
            final int n = i;
            words[n] |= set.words[i];
        }
        if (wordsInCommon < set.wordsInUse) {
            System.arraycopy(set.words, wordsInCommon, this.words, wordsInCommon, this.wordsInUse - wordsInCommon);
        }
        this.checkInvariants();
    }
    
    public void xor(final ChunkSet set) {
        final int wordsInCommon = Math.min(this.wordsInUse, set.wordsInUse);
        if (this.wordsInUse < set.wordsInUse) {
            this.ensureCapacity(set.wordsInUse);
            this.wordsInUse = set.wordsInUse;
        }
        for (int i = 0; i < wordsInCommon; ++i) {
            final long[] words = this.words;
            final int n = i;
            words[n] ^= set.words[i];
        }
        if (wordsInCommon < set.wordsInUse) {
            System.arraycopy(set.words, wordsInCommon, this.words, wordsInCommon, set.wordsInUse - wordsInCommon);
        }
        this.recalculateWordsInUse();
        this.checkInvariants();
    }
    
    public void andNot(final ChunkSet set) {
        for (int i = Math.min(this.wordsInUse, set.wordsInUse) - 1; i >= 0; --i) {
            final long[] words = this.words;
            final int n = i;
            words[n] &= ~set.words[i];
        }
        this.recalculateWordsInUse();
        this.checkInvariants();
    }
    
    @Override
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
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof ChunkSet)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        final ChunkSet set = (ChunkSet)obj;
        this.checkInvariants();
        set.checkInvariants();
        if (this.wordsInUse != set.wordsInUse) {
            return false;
        }
        for (int i = 0; i < this.wordsInUse; ++i) {
            if (this.words[i] != set.words[i]) {
                return false;
            }
        }
        return true;
    }
    
    public ChunkSet clone() {
        if (!this.sizeIsSticky) {
            this.trimToSize();
        }
        try {
            final ChunkSet result = (ChunkSet)super.clone();
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
    
    private void writeObject(final ObjectOutputStream s) throws IOException {
        this.checkInvariants();
        if (!this.sizeIsSticky) {
            this.trimToSize();
        }
        s.writeObject(this.words);
        s.writeInt(this.chunkSize);
        s.writeLong(this.chunkMask);
    }
    
    private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
        this.words = (long[])s.readObject();
        final int newChunkSize = s.readInt();
        final long newChunkMask = s.readLong();
        try {
            final Field chunkSizeField = this.getClass().getDeclaredField("chunkSize");
            chunkSizeField.setAccessible(true);
            chunkSizeField.set(this, newChunkSize);
            final Field chunkMaskField = this.getClass().getDeclaredField("chunkMask");
            chunkMaskField.setAccessible(true);
            chunkMaskField.set(this, newChunkMask);
        }
        catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex2) {
            ex2.printStackTrace();
        }
        this.wordsInUse = this.words.length;
        this.recalculateWordsInUse();
        this.sizeIsSticky = (this.words.length > 0 && this.words[this.words.length - 1] == 0L);
        this.checkInvariants();
    }
    
    @Override
    public String toString() {
        this.checkInvariants();
        final int numBits = (this.wordsInUse > 128) ? this.cardinality() : (this.wordsInUse * 64);
        final StringBuilder b = new StringBuilder(6 * numBits + 2);
        b.append('{');
        int i = this.nextSetBit(0);
        if (i != -1) {
            b.append(i);
            for (i = this.nextSetBit(i + 1); i >= 0; i = this.nextSetBit(i + 1)) {
                final int endOfRun = this.nextClearBit(i);
                do {
                    b.append(", ").append(i);
                } while (++i < endOfRun);
            }
        }
        b.append('}');
        return b.toString();
    }
    
    public int chunkSize() {
        return this.chunkSize;
    }
    
    public int numChunks() {
        return (this.chunkSize == 0) ? 0 : (this.size() / this.chunkSize);
    }
    
    public int numNonZeroChunks() {
        int count = 0;
        for (int numChunks = this.numChunks(), i = 0; i < numChunks; ++i) {
            if (this.getChunk(i) != 0) {
                ++count;
            }
        }
        return count;
    }
    
    public int getChunk(final int chunk) {
        final int bitIndex = chunk * this.chunkSize;
        final int wordIndex = bitIndex >> 6;
        final int down = bitIndex & 0x3F;
        if (wordIndex >= this.words.length) {
            return 0;
        }
        return (int)(this.words[wordIndex] >>> down & this.chunkMask);
    }
    
    public void setChunk(final int chunk, final int value) {
        if (value < 0 || value >= 1 << this.chunkSize) {
            throw new IllegalArgumentException("Chunk value " + value + " is out of range for size = " + this.chunkSize);
        }
        if (this.chunkSize == 0) {
            return;
        }
        final int bitIndex = chunk * this.chunkSize;
        final int wordIndex = bitIndex >> 6;
        this.expandTo(wordIndex);
        final int up = bitIndex & 0x3F;
        final long[] words = this.words;
        final int n = wordIndex;
        words[n] &= ~(this.chunkMask << up);
        final long[] words2 = this.words;
        final int n2 = wordIndex;
        words2[n2] |= ((long)value & this.chunkMask) << up;
        this.recalculateWordsInUse();
        this.checkInvariants();
    }
    
    public void clearChunk(final int chunk) {
        if (this.chunkSize == 0) {
            return;
        }
        final int bitIndex = chunk * this.chunkSize;
        final int wordIndex = bitIndex >> 6;
        if (wordIndex > this.wordsInUse) {
            return;
        }
        final int up = bitIndex & 0x3F;
        final long[] words = this.words;
        final int n = wordIndex;
        words[n] &= ~(this.chunkMask << up);
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
    
    public int getBit(final int chunk, final int bit) {
        final int bitIndex = chunk * this.chunkSize;
        final int wordIndex = bitIndex >> 6;
        final int down = bitIndex & 0x3F;
        return (int)(this.words[wordIndex] >>> down + bit & 0x1L);
    }
    
    public void setBit(final int chunk, final int bit, final boolean value) {
        if (this.chunkSize == 0) {
            return;
        }
        final int bitIndex = chunk * this.chunkSize;
        final int wordIndex = bitIndex >> 6;
        this.expandTo(wordIndex);
        final int up = bitIndex & 0x3F;
        final long bitMask = 1L << up + bit;
        if (value) {
            final long[] words = this.words;
            final int n = wordIndex;
            words[n] |= bitMask;
        }
        else {
            final long[] words2 = this.words;
            final int n2 = wordIndex;
            words2[n2] &= ~bitMask;
        }
        this.recalculateWordsInUse();
        this.checkInvariants();
    }
    
    public void toggleBit(final int chunk, final int bit) {
        if (this.chunkSize == 0) {
            return;
        }
        final int bitIndex = chunk * this.chunkSize;
        final int wordIndex = bitIndex >> 6;
        this.expandTo(wordIndex);
        final int up = bitIndex & 0x3F;
        final long bitMask = 1L << up + bit;
        final long[] words = this.words;
        final int n = wordIndex;
        words[n] ^= bitMask;
        this.recalculateWordsInUse();
        this.checkInvariants();
    }
    
    public void setNBits(final int chunk, final int numBits, final boolean value) {
        if (this.chunkSize == 0) {
            return;
        }
        final int bitIndex = chunk * this.chunkSize;
        final int wordIndex = bitIndex >> 6;
        this.expandTo(wordIndex);
        final int up = bitIndex & 0x3F;
        final long bitsNMask = ChunkSet.bitNMasks[numBits] << up;
        if (value) {
            final long[] words = this.words;
            final int n = wordIndex;
            words[n] |= bitsNMask;
        }
        else {
            final long[] words2 = this.words;
            final int n2 = wordIndex;
            words2[n2] &= ~bitsNMask;
        }
        this.recalculateWordsInUse();
        this.checkInvariants();
    }
    
    public void resolveToBit(final int chunk, final int bit) {
        if (this.chunkSize == 0) {
            return;
        }
        final int bitIndex = chunk * this.chunkSize;
        final int wordIndex = bitIndex >> 6;
        this.expandTo(wordIndex);
        final int up = bitIndex & 0x3F;
        final long bitMask = 1L << up + bit;
        final long[] words = this.words;
        final int n = wordIndex;
        words[n] &= ~(this.chunkMask << up);
        final long[] words2 = this.words;
        final int n2 = wordIndex;
        words2[n2] |= bitMask;
        this.recalculateWordsInUse();
        this.checkInvariants();
    }
    
    public int numBitsOn(final int chunk) {
        final int bitIndex = chunk * this.chunkSize;
        final int wordIndex = bitIndex >> 6;
        final int down = bitIndex & 0x3F;
        final int value = (int)(this.words[wordIndex] >>> down & this.chunkMask);
        int numBits = 0;
        for (int b = 0; b < this.chunkSize; ++b) {
            if ((1 << b & value) != 0x0) {
                ++numBits;
            }
        }
        return numBits;
    }
    
    public boolean isResolved(final int chunk) {
        return this.numBitsOn(chunk) == 1;
    }
    
    public int resolvedTo(final int chunk) {
        final int bitIndex = chunk * this.chunkSize;
        final int wordIndex = bitIndex >> 6;
        final int down = bitIndex & 0x3F;
        final int value = (int)(this.words[wordIndex] >>> down & this.chunkMask);
        int result = -1;
        int numBits = 0;
        for (int b = 0; b < this.chunkSize; ++b) {
            if ((1 << b & value) != 0x0) {
                result = b;
                ++numBits;
            }
        }
        return (numBits == 1) ? result : 0;
    }
    
    public void shiftL(final int numBits, final boolean expand) {
        if (numBits == 0) {
            return;
        }
        if (expand) {
            final int maxIndex = wordIndex(this.length() + numBits);
            this.expandTo(maxIndex);
        }
        final int remnant = 64 - numBits;
        long carry = 0L;
        for (int idx = 0; idx < this.wordsInUse; ++idx) {
            final long temp = this.words[idx] >>> remnant;
            this.words[idx] = (this.words[idx] << numBits | carry);
            carry = temp;
        }
        if (!expand) {
            final int size = this.size();
            if ((size & 0x3F) > 0) {
                final long mask = (1L << (size & 0x3F)) - 1L;
                final long[] words = this.words;
                final int n = this.wordsInUse - 1;
                words[n] &= mask;
            }
        }
        this.recalculateWordsInUse();
        this.checkInvariants();
    }
    
    public void shiftR(final int numBits) {
        if (numBits == 0) {
            return;
        }
        final int remnant = 64 - numBits;
        long carry = 0L;
        for (int idx = this.wordsInUse - 1; idx >= 0; --idx) {
            final long temp = this.words[idx] << remnant;
            this.words[idx] = (this.words[idx] >>> numBits | carry);
            carry = temp;
        }
        this.recalculateWordsInUse();
        this.checkInvariants();
    }
    
    public boolean matches(final ChunkSet mask, final ChunkSet pattern) {
        final int maskWordsInUse = mask.wordsInUse;
        if (this.wordsInUse < maskWordsInUse) {
            return false;
        }
        for (int n = 0; n < maskWordsInUse; ++n) {
            if ((this.words[n] & mask.words[n]) != pattern.words[n]) {
                return false;
            }
        }
        return true;
    }
    
    public boolean violatesNot(final ChunkSet mask, final ChunkSet pattern) {
        return this.violatesNot(mask, pattern, 0);
    }
    
    public boolean violatesNot(final ChunkSet mask, final ChunkSet pattern, final int startWord) {
        for (int wordsToCheck = Math.min(this.wordsInUse, mask.wordsInUse), n = startWord; n < wordsToCheck; ++n) {
            long temp = ~(this.words[n] ^ pattern.words[n]) & mask.words[n];
            if (this.chunkSize > 1) {
                temp &= (temp & 0xAAAAAAAAAAAAAAAAL) >>> 1;
                if (this.chunkSize > 2) {
                    temp &= (temp & 0xCCCCCCCCCCCCCCCCL) >>> 2;
                    if (this.chunkSize > 4) {
                        temp &= (temp & 0xF0F0F0F0F0F0F0F0L) >>> 4;
                        if (this.chunkSize > 8) {
                            temp &= (temp & 0xFF00FF00FF00FF00L) >>> 8;
                            if (this.chunkSize > 16) {
                                temp &= (temp & 0xFFFF0000FFFF0000L) >>> 16;
                                if (this.chunkSize > 32) {
                                    temp &= (temp & 0xFFFFFFFF00000000L) >>> 32;
                                }
                            }
                        }
                    }
                }
            }
            if (temp != 0L) {
                return true;
            }
        }
        return false;
    }
    
    public String toChunkString() {
        this.checkInvariants();
        final int numBits = (this.wordsInUse > 128) ? this.cardinality() : (this.wordsInUse * 64);
        final StringBuilder b = new StringBuilder(6 * numBits + 2);
        b.append('{');
        for (int i = 0; i < this.numChunks(); ++i) {
            final int value = this.getChunk(i);
            if (value != 0) {
                if (b.toString().length() > 1) {
                    b.append(", ");
                }
                b.append("chunk " + i + " = " + value);
            }
        }
        b.append('}');
        return b.toString();
    }
    
    static {
        bitNMasks = new long[64];
        serialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("bits", long[].class) };
    }
}
