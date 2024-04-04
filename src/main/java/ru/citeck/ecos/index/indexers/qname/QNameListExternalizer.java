package ru.citeck.ecos.index.indexers.qname;

import com.intellij.util.io.DataExternalizer;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QNameListExternalizer implements DataExternalizer<List<QName>> {

    @Override
    public void save(@NotNull DataOutput out, List<QName> value) throws IOException {
        out.writeInt(value.size());
        for (QName qName : value) {
            out.writeUTF(qName.getLocalName());
            out.writeUTF(qName.getUri());
            out.writeUTF(qName.getPrefix());
            out.writeUTF(qName.getJavaField());
            out.writeUTF(qName.getJavaClass());
        }
    }

    @Override
    public List<QName> read(@NotNull DataInput in) throws IOException {
        int size = in.readInt();
        List<QName> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(new QName(
                    in.readUTF(), in.readUTF(), in.readUTF(), in.readUTF(), in.readUTF()
            ));
        }
        return result;
    }

}
