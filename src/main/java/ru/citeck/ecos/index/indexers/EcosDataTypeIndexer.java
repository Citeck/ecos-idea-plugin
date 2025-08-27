package ru.citeck.ecos.index.indexers;

import com.intellij.json.psi.JsonArray;
import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.util.text.Strings;
import com.intellij.psi.PsiFile;
import com.intellij.util.indexing.FileContent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLUtil;
import org.jetbrains.yaml.psi.YAMLFile;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLSequence;
import org.jetbrains.yaml.psi.YAMLSequenceItem;
import ru.citeck.ecos.files.FileType;
import ru.citeck.ecos.files.types.ecos.EcosArtifact;
import ru.citeck.ecos.files.types.ecos.model.DataType;
import ru.citeck.ecos.index.EcosFileIndexer;
import ru.citeck.ecos.index.IndexKey;
import ru.citeck.ecos.index.IndexValue;
import ru.citeck.ecos.index.Indexes;
import ru.citeck.ecos.utils.CommonUtils;
import ru.citeck.ecos.utils.EcosPsiUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;


public interface EcosDataTypeIndexer extends EcosFileIndexer {

    String ROLE = "role";
    String STATUS = "status";
    String ATTRIBUTE = "attribute";

    Map<String, String> MODEL_PARTITIONS_MAPPING = Map.of(
            ROLE, "roles",
            STATUS, "statuses",
            ATTRIBUTE, "attributes"
    );

    class JSON implements EcosDataTypeIndexer {

        @Override
        public boolean accept(FileType fileType) {
            return fileType instanceof DataType.JSON;
        }

        @Override
        public void map(@NotNull FileContent inputData, Indexes.FileIndexes indexes) {

            PsiFile psiFile = inputData.getPsiFile();
            if (!(psiFile instanceof JsonFile jsonFile)) {
                return;
            }

            String typeId = ((EcosArtifact) getFileType(inputData)).getFullId(psiFile);
            if (Strings.isEmpty(typeId)) {
                return;
            }

            JsonObject model = Optional
                    .ofNullable(jsonFile.getTopLevelValue())
                    .map(CommonUtils.filterAndCast(JsonObject.class))
                    .map(jsonObject -> jsonObject.findProperty("model"))
                    .map(JsonProperty::getValue)
                    .map(CommonUtils.filterAndCast(JsonObject.class))
                    .orElse(null);

            if (model == null) {
                return;
            }

            MODEL_PARTITIONS_MAPPING.forEach((key, value) ->
                    getItems(model, value).forEach(jsonProperty -> {
                        String id = EcosPsiUtils.getValue(jsonProperty);
                        if (StringUtil.isEmpty(id)) {
                            return;
                        }
                        indexes.add(new IndexKey(typeId, key), id, jsonProperty);
                    }));

        }

        private Stream<JsonProperty> getItems(JsonObject model, String key) {
            return Optional
                    .ofNullable(model.findProperty(key))
                    .map(JsonProperty::getValue)
                    .map(CommonUtils.filterAndCast(JsonArray.class))
                    .map(JsonArray::getValueList)
                    .stream()
                    .flatMap(Collection::stream)
                    .map(CommonUtils.filterAndCast(JsonObject.class))
                    .map(jsonObject -> jsonObject.findProperty("id"));
        }

    }

    class YAML implements EcosDataTypeIndexer {

        @Override
        public boolean accept(FileType fileType) {
            return fileType instanceof DataType.YAML;
        }

        @Override
        public void map(@NotNull FileContent inputData, Indexes.FileIndexes indexes) {

            PsiFile psiFile = inputData.getPsiFile();
            if (!(psiFile instanceof YAMLFile yamlFile)) {
                return;
            }

            String typeId = ((EcosArtifact) getFileType(inputData)).getFullId(psiFile);
            if (Strings.isEmpty(typeId)) {
                return;
            }

            YAMLKeyValue parentRefKeyValue = YAMLUtil.getQualifiedKeyInFile(yamlFile, "parentRef");
            Optional
                    .ofNullable(YAMLUtil.getQualifiedKeyInFile(yamlFile, "parentRef"))
                    .map(YAMLKeyValue::getValueText)
                    .map(String::trim)
                    .filter(parentRef -> parentRef.startsWith(DataType.EMODEL_TYPE))
                    .map(parentRef -> parentRef.replace(DataType.EMODEL_TYPE, DataType.SOURCE_ID + "@"))
                    .ifPresent(parentRef -> indexes
                            .add(new IndexKey(typeId, "parentRef"), parentRef, parentRefKeyValue)
                    );

            MODEL_PARTITIONS_MAPPING.forEach((key, value) ->
                    getItems(yamlFile, value).forEach(keyValue -> {
                        String id = keyValue.getValueText();
                        if (StringUtil.isEmpty(id)) {
                            return;
                        }
                        indexes.add(new IndexKey(typeId, key), id, keyValue);
                    }));

        }

        public Stream<YAMLKeyValue> getItems(YAMLFile yamlFile, String key) {
            return Optional
                    .ofNullable(YAMLUtil.getQualifiedKeyInFile(yamlFile, "model", key))
                    .map(YAMLKeyValue::getValue)
                    .map(CommonUtils.filterAndCast(YAMLSequence.class))
                    .map(YAMLSequence::getItems)
                    .stream()
                    .flatMap(Collection::stream)
                    .map(YAMLSequenceItem::getKeysValues)
                    .flatMap(Collection::stream)
                    .filter(keyValue -> "id".equals(keyValue.getKeyText()));
        }

    }

}
