package com.xjh.myblog.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

// 自定义json的反序列化  将接受到的空字符串转换为null  自动去除两边空字符
public class IgnoreEmptyStringDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        JsonNode node = jsonParser.readValueAsTree();
        String str = node.asText().trim();
        return str.length() == 0 ? null : str;
    }
}
