package com.prolog.masking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class MaskingUtil {
  private MaskingUtil() {
  }
  private static final ObjectMapper objectMapper = new ObjectMapper();


  public static JsonNode maskSensitiveData(String jsonString, String mask, String[] sensitiveFields) throws JsonProcessingException {
    JsonNode rootNode = objectMapper.readTree(jsonString);
    if (rootNode.has("arguments")) {
      JsonNode argumentsNode = rootNode.get("arguments");
      maskFields(argumentsNode, mask, sensitiveFields);
    }

    if (rootNode.has("responseBody")) {
      JsonNode responseBodyNode = rootNode.get("responseBody");
      maskFields(responseBodyNode, mask, sensitiveFields);
    }
    return rootNode;
  }

  public static void maskFields(JsonNode node, String mask, String[] sensitiveFields) {
    Set<String> fields = new HashSet<>(List.of(sensitiveFields));
    maskFieldsRecursive(node, mask, fields);
  }

  private static void maskFieldsRecursive(JsonNode node, String mask, Set<String> sensitiveFields) {
    if (node.isObject()) {
      ObjectNode objectNode = (ObjectNode) node;
      Stream<Map.Entry<String, JsonNode>> fieldsStream = StreamSupport.stream(
          ((Iterable<Map.Entry<String, JsonNode>>) objectNode::fields).spliterator(), false);

      fieldsStream.forEach(field -> {
        if (sensitiveFields.contains(field.getKey())) {
          objectNode.put(field.getKey(), mask);
        } else {
          maskFieldsRecursive(field.getValue(), mask, sensitiveFields);
        }
      });
    } else if (node.isArray()) {
      node.forEach(arrayItem -> maskFieldsRecursive(arrayItem, mask, sensitiveFields));
    }
  }
}



