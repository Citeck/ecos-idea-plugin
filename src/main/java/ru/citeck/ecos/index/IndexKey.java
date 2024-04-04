package ru.citeck.ecos.index;

import com.intellij.util.io.KeyDescriptor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

@Data
@EqualsAndHashCode
public class IndexKey {

    public static final String SEARCH_EVERYWHERE_TYPE = "search-everywhere";
    public static final String REFERENCE_TYPE = "reference";
    public static final String ALL = "*";
    public static final IndexKey SEARCH_EVERYWHERE = new IndexKey(SEARCH_EVERYWHERE_TYPE, ALL);
    private final String type;
    private final String id;
    public IndexKey(String type) {
        this.type = type;
        this.id = ALL;
    }

    public IndexKey(String type, String id) {
        this.type = type;
        this.id = id;
    }

    public static class Descriptor implements KeyDescriptor<IndexKey> {

        @Override
        public int getHashCode(IndexKey key) {
            return key.hashCode();
        }

        @Override
        public boolean isEqual(IndexKey key1, IndexKey key2) {
            return Objects.equals(key1, key2);
        }

        @Override
        public void save(@NotNull DataOutput out, IndexKey key) throws IOException {
            out.writeUTF(key.getType());
            out.writeUTF(key.getId());
        }

        @Override
        public IndexKey read(@NotNull DataInput in) throws IOException {
            return new IndexKey(
                    in.readUTF(),
                    in.readUTF()
            );
        }

    }

}
